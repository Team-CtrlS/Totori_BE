package ctrlS.totori.badge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "badges")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BadgeCategory category;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int targetValue; // 레벨 별 목표 수치

    @Column(nullable = false)
    private String imageURL;
}
