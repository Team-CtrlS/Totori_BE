package ctrlS.totori.book.dto.summary;

import ctrlS.totori.book.entity.Book;
import ctrlS.totori.book.entity.BookReadingRecord;

public record BookCardSummary(
        Long bookId,
        String title,
        String coverImageUrl,
        double progress,
        boolean hasBadge

) {
    public static BookCardSummary of(Book book, BookReadingRecord recentRecord, String presignedUrl) {
        double progress = 0.0;
        boolean hasBadge = false;
        if (recentRecord != null) {
            if (book.getTotalPages() > 0) {
                progress = (double) recentRecord.getReadPages() / book.getTotalPages();
            }
            hasBadge = book.isFullyAcorned();
        }

        return new BookCardSummary(
                book.getId(),
                book.getTitle(),
                presignedUrl,
                progress,
                hasBadge
        );
    }
}