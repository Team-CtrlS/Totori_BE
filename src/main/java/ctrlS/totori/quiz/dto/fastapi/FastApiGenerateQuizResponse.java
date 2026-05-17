package ctrlS.totori.quiz.dto.fastapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FastApiGenerateQuizResponse(
        @JsonProperty("quiz_items")
        List<String> quizItems,
        @JsonProperty("audio_data")
        List<String> audioData
) {
}
