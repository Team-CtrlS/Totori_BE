package ctrlS.totori.badge.dto;

import ctrlS.totori.badge.entity.MemberBadge;

import java.time.LocalDateTime;

public record MemberBadgeResponseDto(
        Long memberBadgeId,
        BadgeResponseDto badgeResponseDto,
        LocalDateTime acquiredAt
) {
    public static MemberBadgeResponseDto from(MemberBadge memberBadge, String badgeImage) {
        return new MemberBadgeResponseDto(
                memberBadge.getId(),
                BadgeResponseDto.from(memberBadge.getBadge(), badgeImage),
                memberBadge.getCreatedAt()
        );
    }
}
