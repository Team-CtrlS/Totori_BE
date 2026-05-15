package ctrlS.totori.member.entity;

import ctrlS.totori.global.entity.BaseTimeEntity;
import ctrlS.totori.quiz.entity.QuizType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "members")
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Email(message = "이메일 형식이어야 합니다.")
    private String loginId;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String name;

    private Integer age;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MemberLevel level = MemberLevel.L1;

    @Builder.Default
    private Integer acorn = 5;

    public void updateInfo(String name, String loginId, LocalDate birthdate) {
        this.name = name;
        this.loginId = loginId;
        this.birthDate = birthdate;
    }

    public void earnAcorn() {
        this.acorn++;
    }
}
