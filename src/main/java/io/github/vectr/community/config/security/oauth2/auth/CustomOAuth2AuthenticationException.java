
package io.github.vectr.community.config.security.oauth2.auth;

import lombok.Getter;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

@Getter
public class CustomOAuth2AuthenticationException
        extends OAuth2AuthenticationException {

    private final String idToken;
    private final String keycloakSub;
    private final boolean deleteKeycloakAccount;

    public CustomOAuth2AuthenticationException(
            OAuth2Error error,
            String idToken
    ) {
        this(error, null, idToken, null, false);
    }

    public CustomOAuth2AuthenticationException(
            OAuth2Error error,
            Throwable cause,
            String idToken
    ) {
        this(error, cause, idToken, null, false);
    }

    public CustomOAuth2AuthenticationException(
            OAuth2Error error,
            Throwable cause,
            String idToken,
            String keycloakSub,
            boolean deleteKeycloakAccount
    ) {
        super(
                error,
                cause != null ? cause.getMessage() : error.getDescription(),
                cause
        );

        this.idToken = idToken;
        this.keycloakSub = keycloakSub;
        this.deleteKeycloakAccount = deleteKeycloakAccount;
    }
}
