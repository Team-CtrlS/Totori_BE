package ctrlS.totori.badge.dto;

import ctrlS.totori.badge.entity.Badge;
import ctrlS.totori.badge.entity.BadgeCategory;

import java.util.List;

public record CategoryBadgeResponseDto(
        BadgeCategory category,
        String categoryName,
        int currentCount,
        List<BadgeDetailDto> badges
) {
    public record BadgeDetailDto(
            Long badgeId,
            String name,
            int level,
            int targetValue,
            String imageUrl,
            boolean isAcquired
    ) {
        public static BadgeDetailDto from(Badge badge, boolean isAcquired) {
            return new BadgeDetailDto(
                    badge.getId(),
                    badge.getName(),
                    badge.getLevel(),
                    badge.getTargetValue(),
                    badge.getImageUrl(),
                    isAcquired
            );
        }
    }
}
