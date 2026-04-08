package ctrlS.totori.book.dto;

import ctrlS.totori.book.entity.Book;
import ctrlS.totori.book.service.S3ImageStorageService;

import java.util.List;

public record BookGenerateResponse(
        Long bookId,
        String title,
        int totalPages,
        String coverImagePrompt,
        String coverImageUrl,
        List<BookPageResponse> pages
) {
    public static BookGenerateResponse of(Book book, String presignedCoverUrl, List<BookPageResponse> pages) {
        return new BookGenerateResponse(
                book.getId(),
                book.getTitle(),
                book.getTotalPages(),
                book.getCoverImagePrompt(),
                presignedCoverUrl,
                pages
        );
    }
}
