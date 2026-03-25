package ctrlS.totori.global.security;

import ctrlS.totori.global.exception.ErrorCode;

public class CustomAuthenticationException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
