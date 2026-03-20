package ctrlS.totori.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookGenerateRequest (
        @NotNull(message = "회원 ID는 필수입니다.")
        Long memberId,

        @NotBlank(message = "STT 텍스트는 비어 있을 수 없습니다.")
        String sttText
) {
}
