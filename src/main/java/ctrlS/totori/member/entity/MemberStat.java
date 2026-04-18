package ctrlS.totori.member.entity;

import ctrlS.totori.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_stats")
public class MemberStat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false)
    private int totalCreatedBooks = 0;

    @Column(nullable = false)
    private int totalReadBooks = 0;

    @Column(nullable = false)
    private int totalAttendanceDays = 0; // 누적 출석일

    @Column(nullable = false)
    private int totalAcquiredAcorn = 0; // 누적 획득 도토리

    @Builder
    public MemberStat(Member member) {
        this.member = member;
    }

    public void addCreatedBook() {
        this.totalCreatedBooks++;
    }

    public void addReadBook() {
        this.totalReadBooks++;
    }

    public void addAcquiredAcorn(int amount) {
        this.totalAcquiredAcorn += amount;
    }

    public void attend(boolean isConsecutive) {
        this.totalAttendanceDays++;
    }
}
