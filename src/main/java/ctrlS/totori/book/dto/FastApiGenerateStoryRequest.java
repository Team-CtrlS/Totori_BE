package ctrlS.totori.book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FastApiGenerateStoryRequest(
        @JsonProperty("stt_text")
        String sttText,

        String level,

        @JsonProperty("recent_wcpm")
        Float recentWcpm,

        @JsonProperty("weak_phonemes")
        List<String> weakPhonemes
) { }