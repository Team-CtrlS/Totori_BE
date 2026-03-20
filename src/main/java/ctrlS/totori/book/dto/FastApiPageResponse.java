package ctrlS.totori.book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record FastApiPageResponse(
        @JsonProperty("page_order")
        int pageOrder,

        @JsonProperty("image_prompt")
        String imagePrompt,

        List<String> sentences
) { }