package ctrlS.totori.book.dto;

import ctrlS.totori.book.entity.Book;

import java.util.List;

public record BookGenerateResponse(
        Long bookId,
        String title,
        int totalPages,
        List<BookPageResponse> pages
) {
    public static BookGenerateResponse from(Book book) {
        return new BookGenerateResponse(
                book.getId(),
                book.getTitle(),
                book.getTotalPages(),
                book.getPages().stream()
                        .map(BookPageResponse::from)
                        .toList()
        );
    }
}
