package ctrlS.totori.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    SUCCESS(HttpStatus.OK,"SUCCESS", "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "CREATED", "새 리소스가 생성되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
