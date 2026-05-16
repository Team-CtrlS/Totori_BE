package ctrlS.totori.book.entity;

import ctrlS.totori.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "book_reading_records")
public class BookReadingRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // 읽기 진행 상태
    private int readPages;
    private boolean isCompleted;

    // 학습 통계 데이터
    private float wcpm;
    private int totalWordCount;
    private int wrongWordCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> wrongWords = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Integer> mistakes = new HashMap<>();

    @Builder
    public BookReadingRecord(Book book, int readPages, boolean isCompleted, float wcpm,
                             int totalWordCount, int wrongWordCount,
                             List<String> wrongWords, Map<String, Integer> mistakes) {
        this.book = book;
        this.readPages = readPages;
        this.isCompleted = isCompleted;
        this.wcpm = wcpm;
        this.totalWordCount = totalWordCount;
        this.wrongWordCount = wrongWordCount;
        this.wrongWords = wrongWords != null ? wrongWords : new ArrayList<>();
        this.mistakes = mistakes != null ? mistakes : new HashMap<>();
    }

    public void markAsCompleted() {
        this.isCompleted = true;
        this.readPages = this.book.getTotalPages();
    }

    public void updateReadingStat(float wcpm, Map<String, Integer> mistakes) {
        this.wcpm = wcpm;
        this.mistakes = mistakes;
    }

    public void updateReadPages(int readPages) {
        this.readPages = Math.max(this.readPages, readPages);
    }
}
