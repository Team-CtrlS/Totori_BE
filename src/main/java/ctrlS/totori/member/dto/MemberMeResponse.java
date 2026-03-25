package ctrlS.totori.member.dto;

import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.Role;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public record MemberMeResponse(
        Long memberId,
        String name,
        String loginId,
        Role role,
        LocalDate birthDate,
        Integer age,
        Integer acorn,
        List<ConnectedChildResponse>children
) {
    public static MemberMeResponse ofchild(Member member) {
        return new MemberMeResponse(
                member.getId(),
                member.getName(),
                member.getLoginId(),
                member.getRole(),
                member.getBirthDate(),
                Period.between(member.getBirthDate(), LocalDate.now()).getYears(),
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
