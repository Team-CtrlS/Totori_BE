package ctrlS.totori.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ctrlS.totori.connect.dto.response.ConnectedChildResponse;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.Role;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MemberMeResponse(
        Long memberId,
        String name,
        String loginId,
        Role role,
        LocalDate birthDate,
        Integer age,
        Integer acorn,
        List<ConnectedChildResponse> children
) {
    public static MemberMeResponse ofChild(Member member) {
        Integer age = (member.getBirthDate() != null)
                ? Period.between(member.getBirthDate(), LocalDate.now()).getYears() + 1
                : null;

        return new MemberMeResponse(
                member.getId(),
                member.getName(),
                member.getLoginId(),
                member.getRole(),
                member.getBirthDate(),
                age,
                member.getAcorn(),
                null
        );
    }

    public static MemberMeResponse ofParent(Member member, List<Member> children) {
        return new MemberMeResponse(
                member.getId(),
                member.getName(),
                member.getLoginId(),
                member.getRole(),
                null,
                null,
                null,
                children.stream().map(ConnectedChildResponse::from).toList()
        );
    }
}
