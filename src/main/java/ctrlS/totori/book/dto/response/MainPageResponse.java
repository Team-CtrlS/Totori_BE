package ctrlS.totori.book.dto.response;

import ctrlS.totori.badge.dto.BadgeResponseDto;
import ctrlS.totori.book.dto.summary.BookCoverSummary;

public record MainPageResponse(
        BookCoverSummary currentBook,
        BadgeResponseDto badge
) {}
