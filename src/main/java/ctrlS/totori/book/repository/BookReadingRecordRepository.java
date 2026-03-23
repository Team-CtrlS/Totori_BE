package ctrlS.totori.book.repository;

import ctrlS.totori.book.entity.BookReadingRecord;
import ctrlS.totori.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookReadingRecordRepository extends JpaRepository<BookReadingRecord, Long> {
    Optional<BookReadingRecord> findTopByBook_Member_IdOrderByUpdatedAtDesc(Long memberId);

    // 특정 기간 동안의 아동 도서 학습 이력 조회 (읽은날 기준)
    @Query("SELECT brr FROM BookReadingRecord brr " +
            "JOIN FETCH brr.book b " +
            "WHERE b.member = :member " +
            "AND brr.updatedAt BETWEEN :start AND :end")
    List<BookReadingRecord> findAllByMemberAndUpdatedAtBetween(
            @Param("member") Member member,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 특정 기간 동안의 아동 도서 학습 이력 조회 (생성한 날 기준)
    @Query("SELECT brr FROM BookReadingRecord brr " +
            "JOIN FETCH brr.book b " +
            "WHERE b.member = :member " +
            "AND brr.createdAt BETWEEN :start AND :end")
    List<BookReadingRecord> findAllByMemberAndCreatedAtBetween(
            @Param("member") Member member,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
