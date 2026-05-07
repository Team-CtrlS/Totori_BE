package ctrlS.totori.book.dto.response;

import ctrlS.totori.badge.dto.BadgeResponseDto;
import ctrlS.totori.book.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "책 완독 처리 응답 DTO")
public record BookCompleteResponse(
        @Schema(description = "책 ID") Long bookId,
        @Schema(description = "이번 완독으로 획득한 도토리 개수") int acornCount,
        @Schema(description = "이 책의 누적 획득 도토리") int totalReceivedAcorn,
        @Schema(description = "이번 완독으로 새로 획득한 뱃지 목록 (없으면 빈 배열)")
        List<BadgeResponseDto> newlyAcquiredBadges
) {
    public static BookCompleteResponse of(Book book, int acornCount, List<BadgeResponseDto> newlyAcquiredBadges) {
        return new BookCompleteResponse(
                book.getId(),
                acornCount,
                book.getReceivedAcorn(),
                newlyAcquiredBadges
        );
    }
}
