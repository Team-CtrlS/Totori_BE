package ctrlS.totori.book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FastApiStoryResponse(
        String title,
        List<FastApiPageResponse> pages
) {
}
