package ctrlS.totori.global.response.dto;

import ctrlS.totori.global.response.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse<T> {
    private int status;
    private String code;
    private String message;
    private T data;

    // 200 OK
    public static <T> BaseResponse<T> ok(T data) {
        return new BaseResponse<>(
                SuccessCode.SUCCESS.getHttpStatus().value(),
                SuccessCode.SUCCESS.getCode(),
                SuccessCode.SUCCESS.getMessage(),
                data
        );
    }

    public static <T> BaseResponse<T> ok() {
        return new BaseResponse<>(
                SuccessCode.SUCCESS.getHttpStatus().value(),
                SuccessCode.SUCCESS.getCode(),
                SuccessCode.SUCCESS.getMessage(),
                null
        );
    }

    // 201 Created
    public static <T> BaseResponse<T> created() {
        return new BaseResponse<>(
                SuccessCode.CREATED.getHttpStatus().value(),
                SuccessCode.CREATED.getCode(),
                SuccessCode.CREATED.getMessage(),
                null
        );
    }
}