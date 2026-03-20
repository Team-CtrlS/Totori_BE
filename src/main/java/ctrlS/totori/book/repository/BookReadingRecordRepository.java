package ctrlS.totori.book.repository;

import ctrlS.totori.book.entity.BookReadingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookReadingRecordRepository extends JpaRepository<BookReadingRecord, Long> {
    Optional<BookReadingRecord> findTopByBook_Member_IdOrderByCreatedAtDesc(Long memberId);
}
