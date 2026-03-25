package ctrlS.totori.global.security;

import ctrlS.totori.global.exception.ErrorCode;

public class CustomAuthenticationException extends RuntimeException {
    public CustomAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

}
