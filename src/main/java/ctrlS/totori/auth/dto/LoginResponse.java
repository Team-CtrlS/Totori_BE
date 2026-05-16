package ctrlS.totori.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String role,
        Boolean hasConnected
) {

}
