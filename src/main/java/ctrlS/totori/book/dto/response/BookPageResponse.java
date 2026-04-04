package ctrlS.totori.book.dto.response;

import ctrlS.totori.book.entity.BookPage;

import java.util.List;

public record BookPageResponse(
        Long pageId,
        int pageOrder,
        List<String> sentences,
        String imagePrompt,
        String imageUrl
) {
    public static BookPageResponse from(BookPage page) {
        return new BookPageResponse(
                page.getId(),
                page.getPageOrder(),
                page.getSentences(),
                page.getImagePrompt(),
                page.getImageUrl()
        );
    }
}
