package ctrlS.totori.book.dto;

import jakarta.validation.constraints.NotBlank;

public record BookGenerateRequest (

        @NotBlank(message = "STT 텍스트는 비어 있을 수 없습니다.")
        String sttText
) {
}
