package ctrlS.totori.member.repository;

import ctrlS.totori.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 로그인 시 아이디로 회원 찾기
    Optional<Member> findByLoginId(String loginId);

    // 중복 가입 방지
    boolean existsByLoginId(String loginId);

    boolean existsByLoginIdAndIdNot(String loginId, Long id);
}
