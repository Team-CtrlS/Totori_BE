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

    private int receivedAcorn = 0; // 0~3개

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookPage> pages = new ArrayList<>();

    @Builder
    public Book(Member member, String title, String coverImageURL, int totalPages) {
        this.member = member;
        this.title = title;
        this.coverImageURL = coverImageURL;
        this.totalPages = totalPages;
    }
}
