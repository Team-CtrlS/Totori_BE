package ctrlS.totori.connect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConnectRequest {
    @NotBlank(message = "연결 코드는 필수입니다.")
    private String code;
}
