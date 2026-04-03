package ctrlS.totori.report.dto.response;

import ctrlS.totori.report.dto.common.ChildDto;
import ctrlS.totori.report.dto.common.DataPointDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class TotalReportResponse {
    private ChildDto child;
    private WpcmTotalDto wcpm;
    private List<AnalysisItemDto> wrongAnalysis;

    @Getter @Builder @AllArgsConstructor
    public static class WpcmTotalDto {
        private double average;      // 전체 평균
        private double childAverage; // 또래(동일 연령대) 평균
        private List<DataPointDto> monthly;
    }

    @Getter @Builder @AllArgsConstructor
    public static class AnalysisItemDto {
        private String label;      // ex) "ㄱ 받침 발음"
        private int wrongCount;
        private int totalCount;
    }
}
