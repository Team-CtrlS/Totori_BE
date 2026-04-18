package ctrlS.totori.badge.entity;

import ctrlS.totori.member.entity.MemberStat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum BadgeCategory {
    BOOK_CREATED("상상력의 마법사", MemberStat::getTotalCreatedBooks),
    BOOK_READ("지혜의 수집가", MemberStat::getTotalReadBooks),
    ATTENDANCE("숲속의 단골손님", MemberStat::getTotalAttendanceDays),
    ACORN("도토리 부자", MemberStat::getTotalAcquiredAcorn);

    private final String title;

    private final Function<MemberStat, Integer> statExtractor;

    public int getCurrentCount(MemberStat stat) {
        return statExtractor.apply(stat);
    }
}
