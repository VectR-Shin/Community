package io.github.vectr.community.config.security.oauth2.keycloak;

import io.github.vectr.community.config.FrontendProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class KeycloakLogoutUrlProvider {

    private static final String REGISTRATION_ID = "keycloak";

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final FrontendProperties frontendProperties;

    public String createLogoutUrl(String idToken, String errorCode) {

        ClientRegistration clientRegistration =
                clientRegistrationRepository.findByRegistrationId(REGISTRATION_ID);

        if (clientRegistration == null) {
            throw new IllegalStateException(
                    "Keycloak ClientRegistration 을 찾을 수 없습니다."
            );
        }

        String endSessionEndpoint = (String) clientRegistration
                .getProviderDetails()
                .getConfigurationMetadata()
                .get("end_session_endpoint");

        if (endSessionEndpoint == null || endSessionEndpoint.isBlank()) {
            throw new IllegalStateException(
                    "Keycloak 로그아웃 엔드포인트를 찾을 수 없습니다."
            );
        }

        String postLogoutRedirectUri = UriComponentsBuilder
                .fromUriString(frontendProperties.url())
                .pathSegment("logout", "callback", errorCode)
                .build()
                .toUriString();

        return UriComponentsBuilder
                .fromUriString(endSessionEndpoint)
                .queryParam("id_token_hint", idToken)
                .queryParam("post_logout_redirect_uri", postLogoutRedirectUri)
                .build()
                .encode()
                .toUriString();
    }
}