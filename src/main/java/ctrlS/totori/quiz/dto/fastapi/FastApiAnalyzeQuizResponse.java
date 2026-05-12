package ctrlS.totori.quiz.dto.fastapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FastApiAnalyzeQuizResponse(
        @JsonProperty("is_correct")
        boolean isCorrect
) {
}
