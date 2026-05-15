package ctrlS.totori.book.dto.summary;

public record BookReportSummary (
        Long bookId,
        String title,
        boolean isCompleted // 읽기 완료 여부
) {}