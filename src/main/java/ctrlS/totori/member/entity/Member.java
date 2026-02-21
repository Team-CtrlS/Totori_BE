package ctrlS.totori.member.entity;

import ctrlS.totori.global.entity.BaseTimeEntity;
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
    @Column(nullable = true)
    private Role role;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    private Integer level;

    @Column(nullable = false)
    private int dotori = 5;

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

    // 이름 수정 메서드
    public Member updateName(String name) {
        this.name = name;
        return this;
    }

    // 카카오 로그인 시 추가 정보 입력 메서드
    public void completeProfile(Role role, String name, LocalDate birthDate) {
        this.role = role;
        this.name = name;
        this.birthDate = birthDate;
    }
}
