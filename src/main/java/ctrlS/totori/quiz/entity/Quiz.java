package ctrlS.totori.quiz.entity;

import ctrlS.totori.book.entity.Book;
import ctrlS.totori.global.entity.BaseTimeEntity;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.MemberLevel;
import ctrlS.totori.quiz.dto.fastapi.FastApiGenerateQuizResponse;
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
@Table(name = "quizzes")
public class Quiz extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Enumerated(EnumType.STRING)
    private QuizType quizType; // PHONEME | JOSA

    @Enumerated(EnumType.STRING)
    private MemberLevel level; // L1-L6

    @ElementCollection
    @CollectionTable(name = "quiz_items",
            joinColumns = @JoinColumn(name = "quiz_id"))
    @Column(name = "item")
    @OrderColumn(name = "item_order")
    private List<String> quizItems;

    @ElementCollection
    @CollectionTable(name = "quiz_audio_keys",
            joinColumns = @JoinColumn(name = "quiz_id"))
    @Column(name = "audio_key")
    @OrderColumn(name = "audio_order")
    private List<String> audioKeys;

    @Column(nullable = false)
    private boolean rewarded = false;

    @Column(nullable = false)
    private int correctCount = 0;

    @Builder
    public Quiz(Book book,
                Member member,
                MemberLevel level,
                QuizType quizType,
                List<String> quizItems,
                List<String> audioKeys) {
        this.book = book;
        this.member = member;
        this.level = level;
        this.quizType = quizType;
        this.quizItems = quizItems != null ? new ArrayList<>(quizItems) : new ArrayList<>();
        this.audioKeys = audioKeys != null ? new ArrayList<>(audioKeys) : new ArrayList<>();
    }

    public static Quiz of(Book book,
                          Member member,
                          List<String> quizItems,
                          List<String> audioKeys) {
        return Quiz.builder()
                .book(book)
                .member(member)
                .level(member.getLevel())
                .quizType(member.getLevel().toQuizType())
                .quizItems(quizItems)
                .audioKeys(audioKeys)
                .build();
    }

    public void markAsRewarded() {
        this.rewarded = true;
    }

    public boolean isRewardable() {
        return !rewarded;
    }

    public void incrementCorrect() {
        correctCount++;
    }

    public boolean isAllCorrect() {
        return correctCount == quizItems.size();
    }
}
