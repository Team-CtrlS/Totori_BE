package ctrlS.totori.book.dto.response;

import ctrlS.totori.book.dto.summary.BookCardSummary;
import org.springframework.data.domain.Page;

import java.util.List;

public record BookListResponse(
        List<BookCardSummary> books,
        int totalPages, // 전체 페이지 수
        long totalElements, // 전체 아이템 수
        boolean isLast  // 마지막 페이지 여부
) {
    public static BookListResponse of(Page<BookCardSummary> page) {
        return new BookListResponse(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast()
        );
    }
}