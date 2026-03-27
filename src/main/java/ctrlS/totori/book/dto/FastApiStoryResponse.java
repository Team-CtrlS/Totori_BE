package ctrlS.totori.book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FastApiStoryResponse(
        String title,

        @JsonProperty("cover_image_prompt")
        String coverImagePrompt,

        List<FastApiPageResponse> pages
) {
}
