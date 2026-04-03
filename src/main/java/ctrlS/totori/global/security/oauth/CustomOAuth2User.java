package ctrlS.totori.global.security.oauth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {
    private final Long memberId;
    private final String role;
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;

    public CustomOAuth2User(Long memberId, String role, Map<String, Object> attributes, String nameAttributeKey) {
        this.memberId = memberId;
        this.role = role;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getName() {
        return String.valueOf(memberId);
    }
}
