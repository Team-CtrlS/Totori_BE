package ctrlS.totori.auth.dto;

import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;

import java.util.Map;

public record OAuth2Attribute(
        Map<String, Object> attributes,
        String name,
        String email,
        String providerId
        ) {
    public static OAuth2Attribute of(String registrationId, Map<String, Object> attributes) {
            if ("kakao".equals(registrationId)) {
                    return ofKakao(attributes);
            }
            throw new CustomException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
    }

    private static OAuth2Attribute ofKakao(Map<String, Object> attributes) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
            String nickname = (String) kakaoProfile.get("nickname");
            String email = (String) kakaoAccount.get("email");
            String providerId = String.valueOf(attributes.get("id"));

            return new OAuth2Attribute(
                    attributes,
                    nickname,
                    email,
                    providerId
            );
    }
}
