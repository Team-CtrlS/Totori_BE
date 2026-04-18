package ctrlS.totori.member.dto.response;

import ctrlS.totori.member.entity.Member;

import java.time.LocalDate;

public record UpdateMemberResponse(
        Long memberId,
        String name,
        String loginId,
        LocalDate birthDate
) {
    public static UpdateMemberResponse from(Member member) {
        return new UpdateMemberResponse(
                member.getId(),
                member.getName(),
                member.getLoginId(),
                member.getBirthDate()
        );
    }
}
