package ctrlS.totori.book.dto;

import java.util.List;

public record BookPageResponse(
        Long pageId,
        int pageOrder,
        List<String> sentences,
        String imagePrompt,
        String imageUrl
) {
}
