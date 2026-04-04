package ctrlS.totori.book.dto.response;

import ctrlS.totori.book.entity.Book;

import java.util.List;

public record BookGenerateResponse(
        Long bookId,
        String title,
        int totalPages,
        String coverImagePrompt,
        String coverImageUrl,
        List<BookPageResponse> pages
) {
    public static BookGenerateResponse from(Book book) {
        return new BookGenerateResponse(
                book.getId(),
                book.getTitle(),
                book.getTotalPages(),
                book.getCoverImagePrompt(),
                book.getCoverImageUrl(),
                book.getPages().stream()
                        .map(BookPageResponse::from)
                        .toList()
        );
    }
}
