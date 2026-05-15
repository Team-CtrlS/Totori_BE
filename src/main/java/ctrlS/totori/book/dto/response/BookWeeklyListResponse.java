package ctrlS.totori.book.dto.response;

import ctrlS.totori.book.dto.summary.BookReportSummary;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record BookWeeklyListResponse(
        // 날짜별로 책 리스트를 묶어서 응답
        Map<LocalDate, List<BookReportSummary>> weeklyData
) {
    public static BookWeeklyListResponse from(Map<LocalDate, List<BookReportSummary>> weeklyData) {
        return new BookWeeklyListResponse(weeklyData);
    }
}