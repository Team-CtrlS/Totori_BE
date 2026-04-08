package ctrlS.totori.book.repository;

import ctrlS.totori.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    // 특정 회원의 책 목록을 최신순으로 페이징 조회
    Page<Book> findAllByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}
