package ctrlS.totori.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Schema(description = "책 완독 처리 요청 DTO")
public record BookCompleteRequest(
        @Schema(description = "이번 읽기로 획득한 도토리 개수 (0~3)")
        @Min(0) @Max(3)
        int acornCount
) {}
