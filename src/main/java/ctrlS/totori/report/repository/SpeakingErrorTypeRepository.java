package ctrlS.totori.report.repository;

import ctrlS.totori.member.entity.Member;
import ctrlS.totori.report.entity.SpeakingErrorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpeakingErrorTypeRepository extends JpaRepository<SpeakingErrorType, Long> {
    // 특정 회원의 에러 유형 중 카운트가 높은 상위 4개만 조회
    List<SpeakingErrorType> findTop4ByMemberOrderByCountDesc(Member member);

    // 전체 에러 개수 합계
    @Query("""
        SELECT COALESCE(SUM(s.count), 0)
        FROM SpeakingErrorType s
        WHERE s.member = :member
    """)
    int sumCountByMember(@Param("member") Member member);
}
