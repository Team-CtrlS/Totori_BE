package ctrlS.totori.auth.dto;

import ctrlS.totori.member.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "아이디는 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotNull(message = "역할(PARENT 또는 CHILD)을 선택해주세요.")
    private Role role;
}