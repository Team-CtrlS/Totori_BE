package ctrlS.totori.badge.repository;

import ctrlS.totori.badge.entity.Badge;
import ctrlS.totori.badge.entity.BadgeCategory;
import ctrlS.totori.badge.entity.MemberBadge;
import ctrlS.totori.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberBadgeRepository extends JpaRepository<MemberBadge, Long> {
    List<MemberBadge> findAllByMember(Member member);

    Optional<MemberBadge> findByMemberAndBadge_Category(Member member, BadgeCategory category);

    boolean existsByMemberAndBadge(Member member, Badge badge);
}
