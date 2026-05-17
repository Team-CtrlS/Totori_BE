package ctrlS.totori.book.dto.fastApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import ctrlS.totori.book.entity.SentenceData;

import java.util.List;

public record FastApiPageResponse(
        @JsonProperty("page_order")
        int pageOrder,
        @JsonProperty("image_prompt")
        String imagePrompt,
        List<SentenceData> sentences
) { }