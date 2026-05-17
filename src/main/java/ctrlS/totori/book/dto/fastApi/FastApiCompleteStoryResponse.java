package ctrlS.totori.book.dto.fastApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import ctrlS.totori.book.dto.fastApi.reading.FastApiErrorItem;

import java.util.List;

public record FastApiCompleteStoryResponse(
        @JsonProperty("child_id")
        Long childId,
        @JsonProperty("book_id")
        Long bookId,
        List<FastApiErrorItem> errors,
        @JsonProperty("avg_wcpm")
        Float avgWcpm
) {
}
