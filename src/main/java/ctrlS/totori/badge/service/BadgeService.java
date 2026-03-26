package ctrlS.totori.badge.service;

import ctrlS.totori.badge.dto.BadgeResponseDto;
import ctrlS.totori.badge.dto.CategoryBadgeResponseDto;
import ctrlS.totori.badge.dto.MemberBadgeResponseDto;
import ctrlS.totori.badge.entity.Badge;
import ctrlS.totori.badge.entity.BadgeCategory;
import ctrlS.totori.badge.entity.MemberBadge;
import ctrlS.totori.badge.repository.BadgeRepository;
import ctrlS.totori.badge.repository.MemberBadgeRepository;
import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.MemberStat;
import ctrlS.totori.member.repository.MemberRepository;
import ctrlS.totori.member.repository.MemberStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final MemberBadgeRepository memberBadgeRepository;
    private final MemberRepository memberRepository;
    private final MemberStatRepository memberStatRepository;

    //전체 뱃지 조회
    public List<BadgeResponseDto> getAllBadges() {
        return badgeRepository.findAll().stream()
                .map(BadgeResponseDto::from)
                .collect(Collectors.toList());
    }

    //유저가 가진 뱃지 전체 조회
    public List<MemberBadgeResponseDto> getMemberBadges(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return memberBadgeRepository.findAllByMember(member).stream()
                .map(MemberBadgeResponseDto::from)
                .collect(Collectors.toList());
    }

    //대표 뱃지 조회
    public MemberBadgeResponseDto getRepresentativeBadge(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        MemberStat stat = memberStatRepository.findByMember(member)
                .orElseThrow(() -> new CustomException(ErrorCode.STAT_NOT_FOUND));

        List<MemberBadge> myBadges = memberBadgeRepository.findAllByMember(member);

        if(myBadges.isEmpty()) {
            //TODO: 보유한 뱃지가 없을 때 표시할 뱃지 설정하기
            throw new CustomException(ErrorCode.BADGE_NOT_FOUND);
        }

        MemberBadge representativeBadge = null;
        double maxProgressPercentage = -1.0;

        for (MemberBadge myBadge: myBadges) {
            Badge currentBadge = myBadge.getBadge();
            BadgeCategory category = currentBadge.getCategory();
            int nextLevel = currentBadge.getLevel() + 1;

            Optional<Badge> nextBadgeOpt = badgeRepository.findByCategoryAndLevel(category, nextLevel);

            // 레벨이 다 찼으면 스킵
            if (nextBadgeOpt.isEmpty()) {
                continue;
            }
            Badge nextBadge = nextBadgeOpt.get();

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
        return MemberBadgeResponseDto.from(result);
    }

    // 뱃지 획득 및 레벨업 검
    @Transactional
    public void checkAndGrantBadge(Long memberId, BadgeCategory category) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        MemberStat memberStat = memberStatRepository.findByMember(member)
                .orElseThrow(() -> new CustomException(ErrorCode.STAT_NOT_FOUND));

        int currentCount = category.getCurrentCount(memberStat);

        List<Badge> categoryBadges = badgeRepository.findAllByCategoryOrderByLevelAsc(category);

        for (Badge badge : categoryBadges) {
            if (currentCount >= badge.getTargetValue()) {
                boolean alreadyHas = memberBadgeRepository.existsByMemberAndBadge(member, badge);

                if(!alreadyHas) {
                    MemberBadge newMemberBadge = MemberBadge.builder()
                            .member(member)
                            .badge(badge)
                            .build();
                    memberBadgeRepository.save(newMemberBadge);
                }
            } else {
                break;
            }
        }

    }

    // 특정 카테고리의 상세 뱃지 리스트 조회
    public CategoryBadgeResponseDto getCategoryBadgeDetail(Long memberId, BadgeCategory category) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        MemberStat memberStat = memberStatRepository.findByMember(member)
                .orElseThrow(() -> new CustomException(ErrorCode.STAT_NOT_FOUND));

        int currentCount = switch (category) {
            case BOOK_CREATED -> memberStat.getTotalCreatedBooks();
            case BOOK_READ -> memberStat.getTotalReadBooks();
            case ATTENDANCE -> memberStat.getConsecutiveAttendanceDays();
            case ACORN -> memberStat.getTotalAcquiredAcorn();
        };

        List<Badge> categoryBadges = badgeRepository.findAllByCategoryOrderByLevelAsc(category);

        List<CategoryBadgeResponseDto.BadgeDetailDto> badgeDetails = new java.util.ArrayList<>();
        for (Badge badge: categoryBadges) {
            boolean isAcquired = currentCount >= badge.getTargetValue();
            badgeDetails.add(CategoryBadgeResponseDto.BadgeDetailDto.from(badge, isAcquired));
        }

        return new CategoryBadgeResponseDto(
                category,
                category.getTitle(),
                currentCount,
                badgeDetails
        );
    }
}
