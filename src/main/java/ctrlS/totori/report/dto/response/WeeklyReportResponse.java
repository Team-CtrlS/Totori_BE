package ctrlS.totori.report.dto.response;

import ctrlS.totori.report.dto.common.ChildDto;
import ctrlS.totori.report.dto.common.DataPointDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class WeeklyReportResponse {
    private ChildDto child;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private List<DailyLearningDto> weeklyLearning;
    private CompletionDto completion;
    private WpcmSummaryDto wcpm;

    @Getter @Builder @AllArgsConstructor
    public static class DailyLearningDto {
        private LocalDate date;
        private String dayOfWeek;
        private boolean studied;
        private int bookCount;
    }

    @Getter @Builder @AllArgsConstructor
    public static class CompletionDto {
        private int completedBookCount;
        private int totalBookCount;
    }

    @Getter @Builder @AllArgsConstructor
    public static class WpcmSummaryDto {
        private double average;
        private List<DataPointDto> daily;
    }
}
