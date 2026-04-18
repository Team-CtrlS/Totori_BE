package ctrlS.totori.badge.dto;

import ctrlS.totori.badge.entity.Badge;
import ctrlS.totori.badge.entity.BadgeCategory;

public record BadgeResponseDto(
        Long id,
        BadgeCategory category,
        String categoryName,
        String name,
        int level,
        int targetValue,
        String imageUrl
) {
    public static BadgeResponseDto from(Badge badge, String badgeImage) {
        return new BadgeResponseDto(
                badge.getId(),
                badge.getCategory(),
                badge.getCategory().getTitle(),
                badge.getName(),
                badge.getLevel(),
                badge.getTargetValue(),
                badgeImage
        );
    }

    public Badge toEntity() {
        return Badge.builder()
                .category(this.category)
                .level(this.level)
                .name(this.name)
                .targetValue(this.targetValue)
                .imageUrl(this.imageUrl)
                .build();
    }
}
