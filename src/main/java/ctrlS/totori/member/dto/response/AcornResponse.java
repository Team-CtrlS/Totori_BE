package ctrlS.totori.member.dto.response;

import ctrlS.totori.member.entity.Member;

public record AcornResponse(
        String name,
        Integer acorn
) {
    public static AcornResponse from(Member member) {
        return new AcornResponse(member.getName(), member.getAcorn());
    }
}
