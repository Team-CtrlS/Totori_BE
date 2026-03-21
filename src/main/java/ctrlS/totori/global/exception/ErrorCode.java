package ctrlS.totori.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Default
    ERROR(400, "요청 처리에 실패했습니다."),
    UNAUTHORIZED_ACCESS(401, "인증되지 않은 사용자입니다."),

    // Member
    USER_NOT_FOUND(404, "존재하는 사용자가 없습니다."),
    STAT_NOT_FOUND(404, "사용자의 통계 정보를 찾을 수 없습니다."),

    // Badge
    BADGE_NOT_FOUND(404, "보유한 뱃지가 없습니다.");

    private final int status;
    private final String message;
}