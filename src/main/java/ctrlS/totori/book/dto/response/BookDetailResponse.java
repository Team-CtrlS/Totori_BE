package ctrlS.totori.book.dto.response;

import ctrlS.totori.book.dto.summary.BookCoverSummary;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "동화 상세 조회 응답 DTO")
public record BookDetailResponse(
        @Schema(description = "표지 정보 (제목, 표지 이미지, 도토리, 진행도 등)")
        BookCoverSummary cover,

        @Schema(description = "페이지 목록 (문장 + 이미지)")
        List<BookPageDetailResponse> pages
) {
    public static BookDetailResponse of(BookCoverSummary summary, List<BookPageDetailResponse> pages) {
        return new BookDetailResponse(summary, pages);
    }
}
