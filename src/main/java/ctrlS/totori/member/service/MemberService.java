package ctrlS.totori.member.service;

import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.member.dto.MemberMeResponse;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.Role;
import ctrlS.totori.member.repository.MemberRepository;
import ctrlS.totori.member.repository.ParentChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ParentChildRepository parentChildRepository;

    public MemberMeResponse getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (member.getRole() == Role.CHILD) {
            return MemberMeResponse.ofchild(member);
        }

        List<Member> children = parentChildRepository.findChildrenByParentId(memberId);
        return MemberMeResponse.ofParent(member, children);
    }
}
