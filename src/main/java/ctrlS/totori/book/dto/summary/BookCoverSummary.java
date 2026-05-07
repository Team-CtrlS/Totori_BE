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
    public static BookCoverSummary of(Book book, BookReadingRecord recentRecord, String presignedCoverUrl) {
        int readPages = recentRecord != null ? recentRecord.getReadPages() : 0;
        double progress = 0.0;
        if (recentRecord != null) {
            if (book.getTotalPages() > 0) {
                progress = (double) recentRecord.getReadPages() / book.getTotalPages();
            }
        }

        return new BookCoverSummary(
                book.getId(),
                book.getTitle(),
                presignedCoverUrl,
                book.getReceivedAcorn(),
                readPages,
                book.getTotalPages(),
                progress
        );
    }
}