package ctrlS.totori.book.dto.response;

import ctrlS.totori.badge.dto.BadgeResponseDto;
import ctrlS.totori.book.dto.summary.BookCoverSummary;
import ctrlS.totori.member.dto.AcornResponse;

public record MainPageResponse(
        AcornResponse acorn,
        BookCoverSummary currentBook,
        BadgeResponseDto badge
) {}
