package ctrlS.totori.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "mistake_infos")
public class MistakeInfo {

    @Column(name = "mistake_type")
    private String mistakeType;

    @Column(name = "mistake_count")
    private String mistakeCount;
}
