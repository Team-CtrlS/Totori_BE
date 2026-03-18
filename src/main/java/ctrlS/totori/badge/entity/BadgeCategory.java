package ctrlS.totori.badge.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BadgeCategory {
    BOOK_CREATED("상상력의 마법사"),
    BOOK_READ("지혜의 수집가"),
    ATTENDANCE("숲속의 단골손님"),
    ACORN("도토리 부자");

    private final String title;
}
