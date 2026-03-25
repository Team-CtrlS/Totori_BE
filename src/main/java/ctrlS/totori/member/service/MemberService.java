package ctrlS.totori.member.service;

import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.member.dto.MemberMeResponse;
import ctrlS.totori.member.dto.UpdateMemberRequest;
import ctrlS.totori.member.dto.UpdateMemberResponse;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.Role;
import ctrlS.totori.member.repository.MemberRepository;
import ctrlS.totori.member.repository.ParentChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public UpdateMemberResponse updateMyInfo(Long memberId, UpdateMemberRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (memberRepository.existsByLoginIdAndIdNot(request.loginId(), memberId)) {
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        member.updateInfo(request.name(), request.loginId(), request.birthdate());

        return UpdateMemberResponse.from(member);
    }
}
