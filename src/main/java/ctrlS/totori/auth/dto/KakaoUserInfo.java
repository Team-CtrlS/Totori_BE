package ctrlS.totori.auth.dto;

import java.util.Map;

public record KakaoUserInfo(Map<String, Object> attributes) {

    // 카카오 ID
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    // 닉네임
    public String getNickname() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return properties != null ? (String) properties.get("nickname") : null;
    }
}
