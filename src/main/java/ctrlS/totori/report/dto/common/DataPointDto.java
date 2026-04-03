package ctrlS.totori.report.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DataPointDto {
    private String label;
    private double value;
}
