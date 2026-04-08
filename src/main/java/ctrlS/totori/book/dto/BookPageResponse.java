package ctrlS.totori.book.dto;

import ctrlS.totori.book.entity.BookPage;
import ctrlS.totori.book.service.S3ImageStorageService;

import java.util.List;

public record BookPageResponse(
        Long pageId,
        int pageOrder,
        List<String> sentences,
        String imagePrompt,
        String imageUrl
) {
    public static BookPageResponse of(BookPage page, String presignedUrl) {
        return new BookPageResponse(
                page.getId(),
                page.getPageOrder(),
                page.getSentences(),
                page.getImagePrompt(),
                presignedUrl
        );
    }
}
