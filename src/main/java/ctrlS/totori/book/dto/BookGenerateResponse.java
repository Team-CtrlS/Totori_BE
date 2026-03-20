package ctrlS.totori.book.dto;

import java.util.List;

public record BookGenerateResponse(
        Long bookId,
        String title,
        int totalPages,
        List<BookPageResponse> pages
) {
}
