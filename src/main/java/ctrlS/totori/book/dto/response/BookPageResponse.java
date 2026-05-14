package ctrlS.totori.book.dto.response;

import ctrlS.totori.book.entity.BookPage;
import ctrlS.totori.book.service.audio.S3AudioStorageService;

import java.util.List;

public record BookPageResponse(
        Long pageId,
        int pageOrder,
        List<SentenceResponse> sentences,
        String imagePrompt,
        String imageUrl
) {
    public static BookPageResponse of(
            BookPage page,
            String presignedImageUrl,
            List<SentenceResponse> sentences
    ) {
        return new BookPageResponse(
                page.getId(),
                page.getPageOrder(),
                sentences,
                page.getImagePrompt(),
                presignedImageUrl
        );
    }
}
