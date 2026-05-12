package ctrlS.totori.book.entity;

import ctrlS.totori.book.dto.fastApi.FastApiStoryResponse;
import ctrlS.totori.global.entity.BaseTimeEntity;
import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "books")
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

    private String coverImageUrl;

    @Column(columnDefinition = "TEXT")
    private String coverImagePrompt;

    @Column(nullable = false)
    private int totalPages;

    private int receivedAcorn = 0; // 0~3개

    public static final int MAX_ACORN_COUNT = 3;    // 한 번에 획득 가능한 도토리 개수

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookPage> pages = new ArrayList<>();

    @Builder
    public Book(Member member, String title, String coverImageUrl, String coverImagePrompt, int totalPages) {
        this.member = member;
        this.title = title;
        this.coverImageUrl = coverImageUrl;
        this.coverImagePrompt = coverImagePrompt;
        this.totalPages = totalPages;
    }

    public static Book of(Member member, FastApiStoryResponse response) {
        return Book.builder()
                .member(member)
                .title(response.title())
                .coverImageUrl(null)
                .coverImagePrompt(response.coverImagePrompt())
                .totalPages(response.pages().size())
                .build();
    }

    public void updateCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public boolean isFullyAcorned() {
        return this.receivedAcorn == MAX_ACORN_COUNT;
    }

    public void addAcorn() {
        if (!isFullyAcorned()) {
            receivedAcorn++;
        } else {
            throw new CustomException(ErrorCode.ACORN_COUNT_EXCEEDED);
        }
    }
}
