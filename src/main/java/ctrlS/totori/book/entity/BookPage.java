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
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "book_pages")
public class BookPage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private int pageOrder;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = false)
    private List<String> sentences = new ArrayList<>();

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    private String imagePrompt;

    @Builder
    public BookPage(Book book, int pageOrder, List<String> sentences, String imagePrompt, String imageUrl) {
        this.book = book;
        this.pageOrder = pageOrder;
        this.sentences = sentences != null ? new ArrayList<>(sentences) : new ArrayList<>();
        this.imagePrompt = imagePrompt;
        this.imageUrl = imageUrl;
    }
}
