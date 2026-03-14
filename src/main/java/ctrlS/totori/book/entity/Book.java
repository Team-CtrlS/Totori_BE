package ctrlS.totori.book.entity;

import ctrlS.totori.global.entity.BaseTimeEntity;
import ctrlS.totori.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    //책 정보
    @Column(nullable = false)
    private String title;

    private String coverImageURL;

    @Column(nullable = false)
    private int totalPages;

    //책 읽기 상태
    private int readPages = 0;

    private boolean isCompleted = false;

    private int receivedAcorn = 0; // 0~3개

    private LocalDateTime lastReadAt;

    //학습 통계 데이터
    private float WCPM;

    private int totalWordCount;

    private int wrongWordCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> wrongWords = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> mistakeTypes = new ArrayList<>();

    @Builder
    public Book(Member member, String title, String coverImageURL, int totalPages) {
        this.member = member;
        this.title = title;
        this.coverImageURL = coverImageURL;
        this.totalPages = totalPages;
    }

    public void updateReadingProgress(int readPages, float WCPM, int totalWordCount, int wrongWordCount,
                                      List<String> wrongWords, List<String> mistakeTypes) {
        this.readPages = readPages;
        this.WCPM = WCPM;
        this.totalWordCount = totalWordCount;
        this.wrongWordCount = wrongWordCount;
        this.lastReadAt = LocalDateTime.now(); // 마지막 읽은 시간 갱신

        this.wrongWords = wrongWords != null ? new ArrayList<>(wrongWords) : new ArrayList<>();
        this.mistakeTypes = mistakeTypes != null ? new ArrayList<>(mistakeTypes) : new ArrayList<>();

        if (this.readPages >= this.totalPages) {
            this.isCompleted = true;
        }
    }
}
