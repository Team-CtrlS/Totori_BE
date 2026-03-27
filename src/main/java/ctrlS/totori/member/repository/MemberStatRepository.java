package ctrlS.totori.member.repository;

import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.MemberStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberStatRepository extends JpaRepository<MemberStat, Long> {
    Optional<MemberStat> findByMember(Member member);
}
