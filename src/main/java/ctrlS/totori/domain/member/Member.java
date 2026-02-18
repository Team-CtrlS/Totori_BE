package ctrlS.totori.domain.member;

import ctrlS.totori.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String loginId;

    private String password;

    @Column(nullable = false)
    private String name;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    private Integer level;

    @Builder
    public Member(String loginId, String password, String name, LocalDate birthDate, Role role, LoginType loginType, Integer level) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.role = role;
        this.loginType = loginType;
        this.level = level;
    }
}
