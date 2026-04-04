package ctrlS.totori.book.dto.summary;

import ctrlS.totori.book.entity.Book;
import ctrlS.totori.book.entity.BookReadingRecord;

public record BookCoverSummary(
        Long bookId,
        String title,
        String coverImageUrl,
        int acornCount,
        int currentPage,
        int totalPage,
        double progress
) {
    public static BookCoverSummary of(Book book, BookReadingRecord record) {
        double progress = (book.getTotalPages() == 0) ? 0
                : (double) record.getReadPages() / book.getTotalPages();

        return new BookCoverSummary(
                book.getId(),
                book.getTitle(),
                book.getCoverImageUrl(),
                book.getReceivedAcorn(),
                record.getReadPages(),
                book.getTotalPages(),
                progress
        );
    }
}