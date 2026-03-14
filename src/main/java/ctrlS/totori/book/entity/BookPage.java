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
public class BookPage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = false)
    private List<String> sentences = new ArrayList<>();

    @Column(nullable = false)
    private String imageUrl;

    @Builder
    public BookPage(Book book, List<String> sentences, String imageUrl) {
        this.book = book;
        this.sentences = sentences != null ? new ArrayList<>(sentences) : new ArrayList<>();
        this.imageUrl = imageUrl;
    }
}
