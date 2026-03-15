package ctrlS.totori.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MistakeInfo {

    @Column(name = "mistake_type")
    private String mistakeType;

    @Column(name = "mistake_count")
    private String mistakeCount;
}
