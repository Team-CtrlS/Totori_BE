package ctrlS.totori.report.service;

import ctrlS.totori.book.entity.BookReadingRecord;
import ctrlS.totori.book.repository.BookReadingRecordRepository;
import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.repository.MemberRepository;
import ctrlS.totori.report.dto.common.ChildDto;
import ctrlS.totori.report.dto.common.DataPointDto;
import ctrlS.totori.report.dto.response.TotalReportResponse;
import ctrlS.totori.report.dto.response.WeeklyReportResponse;
import ctrlS.totori.report.entity.SpeakingErrorType;
import ctrlS.totori.report.repository.SpeakingErrorTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final MemberRepository memberRepository;
    private final BookReadingRecordRepository bookReadingRecordRepository;
    private final SpeakingErrorTypeRepository speakingErrorTypeRepository;

    @Transactional(readOnly = true)
    public WeeklyReportResponse getWeeklyReport(Long memberId) {
        Member child = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        ChildDto childDto = ChildDto.from(child);

        LocalDate today = LocalDate.now();
        LocalDate lastWeek = today.minusDays(6);
        List<BookReadingRecord> readRecords = bookReadingRecordRepository.findAllByMemberAndUpdatedAtBetween(child, lastWeek.atStartOfDay(), today.atTime(LocalTime.MAX));
        List<BookReadingRecord> createdRecords = bookReadingRecordRepository.findAllByMemberAndCreatedAtBetween(child, lastWeek.atStartOfDay(), today.atTime(LocalTime.MAX));

        // 주간학습 현황
        List<WeeklyReportResponse.DailyLearningDto> weeklyLearning = getWeeklyLearning(readRecords, today, lastWeek);

        // 도서 완독률
        WeeklyReportResponse.CompletionDto completionStatus = getCompletionStatus(createdRecords);

        // WCPM 점수 그래프
        WeeklyReportResponse.WpcmSummaryDto wpcmSummary = getWpcmSummary(readRecords, today, lastWeek);

        return WeeklyReportResponse.builder()
                .child(childDto)
                .weekStart(lastWeek)
                .weekEnd(today)
                .weeklyLearning(weeklyLearning)
                .completion(completionStatus)
                .wcpm(wpcmSummary)
                .build();
    }

    @Transactional(readOnly = true)
    public TotalReportResponse getTotalReport(Long memberId) {
        Member child = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        ChildDto childDto = ChildDto.from(child);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(5).withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime endDate = now.with(LocalTime.MAX);

        // 종합 WCPM 점수
        List<BookReadingRecord> allRecords = bookReadingRecordRepository.findAllByMemberAndUpdatedAtBetween(child, startDate, endDate);
        Double globalAvg = null;    // TODO: 추후 기준점 추가 예정
        TotalReportResponse.WpcmTotalDto wcpmTotal = getMonthlyWpcmSummary(allRecords, globalAvg, startDate.toLocalDate());

        // 오답 유형 분석
        List<TotalReportResponse.AnalysisItemDto> errorTypes = getErrorType(child);

        return TotalReportResponse.builder()
                .child(childDto)
                .wcpm(wcpmTotal)
                .wrongAnalysis(errorTypes)
                .build();
    }

    /**
     * 이번 주의 도서 학습 현황
     */
    private List<WeeklyReportResponse.DailyLearningDto> getWeeklyLearning(List<BookReadingRecord> records, LocalDate today, LocalDate lastWeek) {
        // 날짜별로 그룹화
        Map<LocalDate, Long> bookCountByDate = records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getUpdatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        List<WeeklyReportResponse.DailyLearningDto> weeklyLearning = new ArrayList<>();
        for (LocalDate date = lastWeek; !date.isAfter(today); date = date.plusDays(1)) {
            long count = bookCountByDate.getOrDefault(date, 0L);

            weeklyLearning.add(WeeklyReportResponse.DailyLearningDto.builder()
                    .date(date)
                    .dayOfWeek(date.getDayOfWeek().name())
                    .studied(count > 0)
                    .bookCount((int) count)
                    .build());
        }

        return weeklyLearning;
    }

    /**
     * 이번 주에 생성한 도서들의 완독 상태 집계
     */
    private WeeklyReportResponse.CompletionDto getCompletionStatus(List<BookReadingRecord> createdRecords) {
        int totalBookCount = createdRecords.size();

        int completedBookCount = (int) createdRecords.stream()
                .filter(BookReadingRecord::isCompleted)
                .count();

        return WeeklyReportResponse.CompletionDto.builder()
                .completedBookCount(completedBookCount)
                .totalBookCount(totalBookCount)
                .build();
    }

    /**
     * 주간 WCPM 평균 데이터
     */
    private WeeklyReportResponse.WpcmSummaryDto getWpcmSummary(List<BookReadingRecord> readRecords, LocalDate today, LocalDate lastWeek) {
        // 주간 전체 평균
        double totalAverage = readRecords.stream()
                .mapToDouble(BookReadingRecord::getWcpm)
                .average()
                .orElse(0.0);

        // 일별 평균
        Map<LocalDate, Double> dailyWcpmAverage = readRecords.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getUpdatedAt().toLocalDate(),
                        Collectors.averagingDouble(BookReadingRecord::getWcpm)
                ));

        List<DataPointDto> dailyPoints = new ArrayList<>();
        for (LocalDate date = lastWeek; !date.isAfter(today); date = date.plusDays(1)) {
            double avgValue = dailyWcpmAverage.getOrDefault(date, 0.0);

            dailyPoints.add(DataPointDto.builder()
                    .label(date.toString())
                    .value(Math.round(avgValue * 10.0) / 10.0)  // 소숫점 한자리
                    .build());
        }

        return WeeklyReportResponse.WpcmSummaryDto.builder()
                .average(Math.round(totalAverage * 10.0) / 10.0)
                .daily(dailyPoints)
                .build();
    }

    /**
     * 6개월간 WCPM 평균 데이터
     */
    private TotalReportResponse.WpcmTotalDto getMonthlyWpcmSummary(List<BookReadingRecord> records, Double globalAvg, LocalDate startMonthDate) {
        double totalAverage = records.stream()
                .mapToDouble(BookReadingRecord::getWcpm)
                .average()
                .orElse(0.0);

        // 월별 그룹화
        Map<YearMonth, Double> monthlyAverage = records.stream()
                .collect(Collectors.groupingBy(
                        record -> YearMonth.from(record.getUpdatedAt()),
                        Collectors.averagingDouble(BookReadingRecord::getWcpm)
                ));

        List<DataPointDto> monthlyPoints = new ArrayList<>();
        YearMonth startMonth = YearMonth.from(startMonthDate);
        YearMonth currentMonth = YearMonth.from(LocalDate.now());

        for (YearMonth month = startMonth; !month.isAfter(currentMonth); month = month.plusMonths(1)) {
            double avgValue = monthlyAverage.getOrDefault(month, 0.0);

            monthlyPoints.add(DataPointDto.builder()
                    .label(month.toString())
                    .value(Math.round(avgValue * 10.0) / 10.0)
                    .build());
        }

        return TotalReportResponse.WpcmTotalDto.builder()
                .average(Math.round(totalAverage * 10.0) / 10.0)
                .childAverage(Math.round((globalAvg != null ? globalAvg : 0.0) * 10.0) / 10.0)
                .monthly(monthlyPoints)
                .build();
    }

    /**
     * 에러 유형 분석
     */
    private List<TotalReportResponse.AnalysisItemDto> getErrorType(Member member){
        List<SpeakingErrorType> top4Errors = speakingErrorTypeRepository.findTop4ByMemberOrderByCountDesc(member);
        int totalErrors = speakingErrorTypeRepository.sumCountByMember(member);

        return top4Errors.stream()
                .map(error -> TotalReportResponse.AnalysisItemDto.builder()
                        .label(error.getType())
                        .wrongCount(error.getCount())
                        .totalCount(totalErrors)
                        .build())
                .collect(Collectors.toList());
    }
}
