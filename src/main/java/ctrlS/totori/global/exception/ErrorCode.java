package ctrlS.totori.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Default
    ERROR(400, "요청 처리에 실패했습니다."),
    UNAUTHORIZED_ACCESS(401, "인증되지 않은 사용자입니다."),

    // auth
    USER_NOT_FOUND(401, "존재하는 사용자가 없습니다.");

    private final int status;
    private final String message;
}