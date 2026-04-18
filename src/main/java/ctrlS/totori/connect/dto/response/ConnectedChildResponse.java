package ctrlS.totori.connect.dto.response;

import ctrlS.totori.member.entity.Member;

import java.time.LocalDate;
import java.time.Period;


public record ConnectedChildResponse(
        Long memberId,
        String name,
        Integer acorn,
        Integer age
) {
    public static ConnectedChildResponse from(Member child) {
        return new ConnectedChildResponse(
                child.getId(),
                child.getName(),
                child.getAcorn(),
                Period.between(child.getBirthDate(), LocalDate.now()).getYears()
        );
    }
}
