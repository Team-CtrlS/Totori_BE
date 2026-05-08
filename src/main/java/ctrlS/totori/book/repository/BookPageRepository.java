package ctrlS.totori.book.repository;

import ctrlS.totori.book.entity.BookPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookPageRepository extends JpaRepository<BookPage, Long> {
    Optional<BookPage> findByBook_idAndPageOrder(Long bookId, int pageOrder);
}
