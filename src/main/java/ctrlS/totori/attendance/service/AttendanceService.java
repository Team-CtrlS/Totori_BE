package ctrlS.totori.attendance.service;

import ctrlS.totori.attendance.dto.AttendanceResponse;
import ctrlS.totori.badge.entity.BadgeCategory;
import ctrlS.totori.badge.service.BadgeService;
import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.MemberStat;
import ctrlS.totori.member.repository.MemberRepository;
import ctrlS.totori.member.repository.MemberStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final MemberRepository memberRepository;
    private final MemberStatRepository memberStatRepository;
    private final BadgeService badgeService;

    @Transactional
    public AttendanceResponse checkAttendance(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        MemberStat stat = memberStatRepository.findByMember(member)
                .orElseThrow(() -> new CustomException(ErrorCode.STAT_NOT_FOUND));

        LocalDate today = LocalDate.now();

        if (!stat.canAttendToday(today)) {
            return AttendanceResponse.of(
                    false,
                    stat.getTotalAttendanceDays()
            );
        }

        stat.attend(today);

        badgeService.checkAndGrantBadge(memberId, BadgeCategory.ATTENDANCE);

        return AttendanceResponse.of(
                true,
                stat.getTotalAttendanceDays()
        );
    }
}
