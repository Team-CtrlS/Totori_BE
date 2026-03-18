package ctrlS.totori.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConnectCodeResponse {
    private String code;
    private int validTime;
}
