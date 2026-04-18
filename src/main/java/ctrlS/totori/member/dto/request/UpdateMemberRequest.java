package ctrlS.totori.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record UpdateMemberRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "로그인 ID는 필수입니다.")
        @Email(message = "이메일 형식이어야 합니다.")
        String loginId,

        @PastOrPresent(message = "미래 날짜는 입력할 수 없습니다.")
        LocalDate birthDate
) {
}
