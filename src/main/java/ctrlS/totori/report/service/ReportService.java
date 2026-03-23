package ctrlS.totori.report.service;

import ctrlS.totori.book.entity.BookReadingRecord;
import ctrlS.totori.book.repository.BookReadingRecordRepository;
import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.repository.MemberRepository;
import ctrlS.totori.report.dto.common.ChildDto;
import ctrlS.totori.report.dto.common.DataPointDto;
import ctrlS.totori.report.dto.response.WeeklyReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Transactional(readOnly = true)
    public WeeklyReportResponse getWeelyReport(Long memberId) {
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
                .weekStart(today)
                .weekEnd(lastWeek)
                .weeklyLearning(weeklyLearning)
                .completion(completionStatus)
                .wcpm(wpcmSummary)
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
}
