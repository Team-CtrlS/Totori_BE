package ctrlS.totori.book.repository;

import ctrlS.totori.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    // 특정 회원의 책 목록을 최신순으로 페이징 조회
    Page<Book> findAllByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    @Query("""
            SELECT b FROM Book b
            LEFT JOIN FETCH b.pages p
            WHERE b.id = :bookId
            ORDER BY p.pageOrder ASC
            """)
    Optional<Book> findByIdWithPages(@Param("bookId") Long bookId);

    @Query("""
        SELECT b FROM Book b
        WHERE b.id = :bookId AND b.member.id = :memberId
        """)
    Optional<Book> findByIdAndMemberId(@Param("bookId") Long bookId, @Param("memberId") Long memberId);

    @Query("SELECT b FROM Book b " +
            "WHERE b.member.id = :memberId " +
            "AND b.createdAt >= :startDate " +
            "ORDER BY b.createdAt DESC")
    List<Book> findWeeklyBooks(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDateTime startDate
    );
}
