package ctrlS.totori.badge.controller;
ㅌ₩
import ctrlS.totori.badge.dto.BadgeResponseDto;
import ctrlS.totori.badge.dto.CategoryBadgeResponseDto;
import ctrlS.totori.badge.dto.MemberBadgeResponseDto;
import ctrlS.totori.badge.entity.BadgeCategory;
import ctrlS.totori.badge.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    // 전체 뱃지 조회
    @GetMapping
    public ResponseEntity<List<BadgeResponseDto>> getAllBadges() {
        return ResponseEntity.ok(badgeService.getAllBadges());
    }

    // 유저의 뱃지 조회
    @GetMapping("/my")
    public ResponseEntity<List<MemberBadgeResponseDto>> getMemberBadges(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(badgeService.getMemberBadges(memberId));
    }

    // 대표 뱃지 조회
    @GetMapping("/my/representative")
    public ResponseEntity<MemberBadgeResponseDto> getRepresentativeBadge(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(badgeService.getRepresentativeBadge(memberId));
    }

    // 특정 카테고리의 상세 뱃지 정보 조회
    @GetMapping("/my/categories/{category}")
    public ResponseEntity<CategoryBadgeResponseDto> getCategoryBadgeDetails(
            @AuthenticationPrincipal Long memberId,
            @PathVariable BadgeCategory category) {
        return ResponseEntity.ok(badgeService.getCategoryBadgeDetail(memberId, category));
    }
}
