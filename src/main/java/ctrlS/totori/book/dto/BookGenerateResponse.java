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
    public static BookGenerateResponse from(Book book, S3ImageStorageService s3Service) {
        return new BookGenerateResponse(
                book.getId(),
                book.getTitle(),
                book.getTotalPages(),
                book.getCoverImagePrompt(),
                book.getCoverImageUrl() != null ? s3Service.getPresignedUrl(book.getCoverImageUrl()) : null,
                book.getPages().stream()
                        .map(page -> BookPageResponse.from(page, s3Service))
                        .toList()
        );
    }
}
