package ctrlS.totori.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Default
    ERROR(400, "요청 처리에 실패했습니다."),
    UNAUTHORIZED_ACCESS(401, "인증되지 않은 사용자입니다."),

    // user
    USER_NOT_FOUND(404, "존재하는 사용자가 없습니다."),

    // auth
    DUPLICATE_LOGIN_ID(409, "이미 존재하는 아이디입니다."),
    LOGIN_ID_NOT_FOUND(404, "가입되지 않은 아이디입니다."),
    INVALID_PASSWORD(401, "비밀번호가 일치하지 않습니다."),
    UNSUPPORTED_OAUTH_PROVIDER(400, "지원하지 않는 OAuth 제공자입니다."),
    LOGGED_OUT_TOKEN(401, "이미 로그아웃된 토큰입니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "만료된 토큰입니다."),

    // connect
    ONLY_CHILD_CAN_CREATE_CONNECT_CODE(403, "아동 계정만 연결 코드를 생성할 수 있습니다."),
    ONLY_PARENT_CAN_CONNECT_CHILD(403, "부모 계정만 연결 코드를 입력할 수 있습니다."),
    INVALID_OR_EXPIRED_CONNECT_CODE(400, "유효하지 않거나 만료된 연결 코드입니다."),
    ALREADY_CONNECTED_CHILD(409, "이미 연결된 아동 계정입니다.");

    private final int status;
    private final String message;
}