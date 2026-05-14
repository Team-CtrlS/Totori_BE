package ctrlS.totori.badge.service;

import ctrlS.totori.badge.dto.BadgeResponseDto;
import ctrlS.totori.badge.dto.CategoryBadgeResponseDto;
import ctrlS.totori.badge.dto.MemberBadgeResponseDto;
import ctrlS.totori.badge.entity.Badge;
import ctrlS.totori.badge.entity.BadgeCategory;
import ctrlS.totori.badge.entity.MemberBadge;
import ctrlS.totori.badge.repository.BadgeRepository;
import ctrlS.totori.badge.repository.MemberBadgeRepository;
import ctrlS.totori.book.service.image.S3ImageStorageService;
import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.MemberStat;
import ctrlS.totori.member.repository.MemberRepository;
import ctrlS.totori.member.repository.MemberStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final MemberBadgeRepository memberBadgeRepository;
    private final MemberRepository memberRepository;
    private final MemberStatRepository memberStatRepository;
    private final S3ImageStorageService s3ImageStorageService;

    private final static String BADGE_IMAGE_PREFIX = "badges";

    //전체 뱃지 조회
    public List<BadgeResponseDto> getAllBadges() {
        return badgeRepository.findAll().stream()
                .map(badge -> {
                    String presignedBadgeImg = s3ImageStorageService.getPresignedUrl(BADGE_IMAGE_PREFIX, badge.getImageUrl());
                    return BadgeResponseDto.from(badge, presignedBadgeImg);
                })
                .collect(Collectors.toList());
    }

    //유저가 가진 뱃지 전체 조회
    public List<MemberBadgeResponseDto> getMemberBadges(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return memberBadgeRepository.findAllByMember(member).stream()
                .map(memberBadge -> {
                    String presignedBadgeImg = s3ImageStorageService.getPresignedUrl(BADGE_IMAGE_PREFIX, memberBadge.getBadge().getImageUrl());
                    return MemberBadgeResponseDto.from(memberBadge, presignedBadgeImg);
                })
                .collect(Collectors.toList());
    }

    //대표 뱃지 조회
    public MemberBadgeResponseDto getRepresentativeBadge(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        MemberStat stat = memberStatRepository.findByMember(member)
                .orElseThrow(() -> new CustomException(ErrorCode.STAT_NOT_FOUND));

        List<MemberBadge> myBadges = memberBadgeRepository.findAllByMember(member);

        if (myBadges.isEmpty()) {
            throw new CustomException(ErrorCode.BADGE_NOT_FOUND);
        }

        //카테고리별 가장 높은 레벨 벳지 저장
        Map<BadgeCategory, MemberBadge> highestBadges = myBadges.stream()
                .collect(Collectors.toMap(
                        mb -> mb.getBadge().getCategory(),
                        mb -> mb,
                        (mb1, mb2) -> mb1.getBadge().getLevel() > mb2.getBadge().getLevel() ? mb1 : mb2
                ));

        //전체 뱃지를 한번에 조회하여 메모리에 캐싱
        Map<BadgeCategory, Map<Integer, Badge>> allBadgesMap = badgeRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Badge::getCategory,
                        Collectors.toMap(Badge::getLevel, b -> b)
                ));

        MemberBadge representativeBadge = null;
        double maxProgressPercentage = -1.0;

        for (MemberBadge myBadge : highestBadges.values()) {
            Badge currentBadge = myBadge.getBadge();
            BadgeCategory category = currentBadge.getCategory();
            int nextLevel = currentBadge.getLevel() + 1;

            Map<Integer, Badge> categoryBadges = allBadgesMap.getOrDefault(category, Collections.emptyMap());
            Badge nextBadge = categoryBadges.get(nextLevel);

            // 레벨이 다 찼으면 스킵
            if (nextBadge == null) continue;

            int currentCount = category.getCurrentCount(stat);

            double denominator = (double) nextBadge.getTargetValue() - currentBadge.getTargetValue();
            double numerator = (double) currentCount - currentBadge.getTargetValue();

            if (numerator < 0) numerator = 0;
            if (denominator <= 0) denominator = 1;

            double progressPercentage = numerator / denominator;

            if (progressPercentage > maxProgressPercentage) {
                maxProgressPercentage = progressPercentage;
                representativeBadge = myBadge;
            }
        }

        MemberBadge result = representativeBadge != null ? representativeBadge : myBadges.get(0);
        String presignedBadgeImg = s3ImageStorageService.getPresignedUrl(BADGE_IMAGE_PREFIX, result.getBadge().getImageUrl());
        return MemberBadgeResponseDto.from(result, presignedBadgeImg);
    }

    // 뱃지 획득 및 레벨업 검사
    @Transactional
    public List<BadgeResponseDto> checkAndGrantBadge(Long memberId, BadgeCategory category) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        MemberStat memberStat = memberStatRepository.findByMember(member)
                .orElseThrow(() -> new CustomException(ErrorCode.STAT_NOT_FOUND));

        int currentCount = category.getCurrentCount(memberStat);

        List<Badge> categoryBadges = badgeRepository.findAllByCategoryOrderByLevelAsc(category);

        List<BadgeResponseDto> newlyGrantedBadges = new java.util.ArrayList<>();

        for (Badge badge : categoryBadges) {
            if (currentCount >= badge.getTargetValue()) {
                boolean alreadyHas = memberBadgeRepository.existsByMemberAndBadge(member, badge);

                if (!alreadyHas) {
                    MemberBadge newMemberBadge = MemberBadge.builder()
                            .member(member)
                            .badge(badge)
                            .build();
                    memberBadgeRepository.save(newMemberBadge);

                    String presignedBadgeImg = s3ImageStorageService.getPresignedUrl(BADGE_IMAGE_PREFIX, badge.getImageUrl());
                    newlyGrantedBadges.add(BadgeResponseDto.from(badge, presignedBadgeImg));
                }
            } else {
                break;
            }
        }
        return newlyGrantedBadges;
    }

    // 특정 카테고리의 상세 뱃지 리스트 조회
    public CategoryBadgeResponseDto getCategoryBadgeDetail(Long memberId, BadgeCategory category) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        MemberStat memberStat = memberStatRepository.findByMember(member)
                .orElseThrow(() -> new CustomException(ErrorCode.STAT_NOT_FOUND));

        int currentCount = category.getCurrentCount(memberStat);

        List<Badge> categoryBadges = badgeRepository.findAllByCategoryOrderByLevelAsc(category);

        List<CategoryBadgeResponseDto.BadgeDetailDto> badgeDetails = new java.util.ArrayList<>();
        for (Badge badge : categoryBadges) {
            boolean isAcquired = currentCount >= badge.getTargetValue();
            String presignedBadgeImg = s3ImageStorageService.getPresignedUrl(BADGE_IMAGE_PREFIX, badge.getImageUrl());
            badgeDetails.add(CategoryBadgeResponseDto.BadgeDetailDto.from(badge, isAcquired, presignedBadgeImg));
        }

        return new CategoryBadgeResponseDto(
                category,
                category.getTitle(),
                currentCount,
                badgeDetails
        );
    }
}
