package ctrlS.totori.report.dto.common;

import ctrlS.totori.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChildDto {
    private String name;
    private Integer age;

    public static ChildDto from(Member member){
        return ChildDto.builder()
                .name(member.getName())
                .age(member.getAge())
                .build();
    }
}