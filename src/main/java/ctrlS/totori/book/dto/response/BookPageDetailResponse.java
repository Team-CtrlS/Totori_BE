package ctrlS.totori.book.dto.response;

import ctrlS.totori.book.entity.BookPage;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "동화 상세 조회 페이지 DTO")
public record BookPageDetailResponse(
        @Schema(description = "페이지 ID") Long pageId,
        @Schema(description = "페이지 순서") int pageOrder,
        @Schema(description = "페이지 문장 목록") List<String> sentences,
        @Schema(description = "페이지 이미지 URL (presigned)") String imageUrl
) {
    public static BookPageDetailResponse of(BookPage page, String presignedUrl) {
        return new BookPageDetailResponse(
                page.getId(),
                page.getPageOrder(),
                page.getSentences(),
                presignedUrl
        );
    }
}
