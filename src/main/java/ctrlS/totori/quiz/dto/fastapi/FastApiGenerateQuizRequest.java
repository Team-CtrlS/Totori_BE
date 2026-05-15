package ctrlS.totori.quiz.dto.fastapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FastApiGenerateQuizRequest(
        @JsonProperty("child_id")
        Long childId,

        @JsonProperty("book_id")
        Long bookId,

        String level
) {
}
