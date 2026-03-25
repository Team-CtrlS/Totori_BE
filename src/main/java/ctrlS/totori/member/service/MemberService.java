package ctrlS.totori.member.service;

import ctrlS.totori.auth.service.AuthRedisService;
import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.global.security.CustomUserPrincipal;
import ctrlS.totori.global.security.JwtTokenProvider;
import ctrlS.totori.member.dto.AcornResponse;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRedisService authRedisService;

    @Transactional(readOnly = true)
    public MemberMeResponse getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (member.getRole() == Role.CHILD) {
            return MemberMeResponse.ofChild(member);
        }

        List<Member> children = parentChildRepository.findChildrenByParentId(memberId);
        return MemberMeResponse.ofParent(member, children);
    }

    @Transactional(readOnly = true)
    public AcornResponse getMyAcorn(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (member.getRole() != Role.CHILD) {
            throw new CustomException(ErrorCode.FORBIDDEN_CHILD_ONLY);
        }

        return AcornResponse.from(member);
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

    @Transactional
    public void deleteMyAccount(Long memberId, String bearerToken) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (member.getRole() == Role.PARENT) {
            parentChildRepository.deleteByParentId(memberId);
        } else {
            parentChildRepository.deleteByChildId(memberId);
        }

        String token = jwtTokenProvider.resolveToken(bearerToken);
        long expiration = jwtTokenProvider.getRemainingSeconds(token);
        authRedisService.blacklistToken(token, expiration);
    }
}
