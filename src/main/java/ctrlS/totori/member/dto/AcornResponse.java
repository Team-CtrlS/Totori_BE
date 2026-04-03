package ctrlS.totori.member.dto;

import ctrlS.totori.member.entity.Member;

public record AcornResponse(
        Long memberId,
        Integer acorn
) {
    public static AcornResponse from(Member member) {
        return new AcornResponse(member.getId(), member.getAcorn());
    }
}
