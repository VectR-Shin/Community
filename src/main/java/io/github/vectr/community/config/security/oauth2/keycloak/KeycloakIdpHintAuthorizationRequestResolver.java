package io.github.vectr.community.config.security.oauth2.keycloak;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.Map;
import java.util.Set;

public class KeycloakIdpHintAuthorizationRequestResolver
        implements OAuth2AuthorizationRequestResolver {

    private static final String REGISTRATION_ID = "keycloak";
    private static final String IDP_HINT_PARAMETER = "kc_idp_hint";

    private static final Set<String> ALLOWED_IDP_ALIASES =
            Set.of("google", "naver", "kakao");

    private final OAuth2AuthorizationRequestResolver delegate;

    public KeycloakIdpHintAuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository
    ) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository,
                "/oauth2/authorization"
        );
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest =
                delegate.resolve(request);

        if (authorizationRequest == null) {
            return null;
        }

        String idpHint = request.getParameter(IDP_HINT_PARAMETER);

        if (idpHint == null || !ALLOWED_IDP_ALIASES.contains(idpHint)) {
            return authorizationRequest;
        }

        return OAuth2AuthorizationRequest
                .from(authorizationRequest)
                .additionalParameters(parameters ->
                        parameters.put(IDP_HINT_PARAMETER, idpHint)
                )
                .build();
    }

    @Override
    public OAuth2AuthorizationRequest resolve(
            HttpServletRequest request,
            String registrationId
    ) {
        OAuth2AuthorizationRequest authorizationRequest =
                delegate.resolve(request, registrationId);

        if (!REGISTRATION_ID.equals(registrationId)) {
            return authorizationRequest;
        }

        return applyIdpHint(request, authorizationRequest);
    }

    private OAuth2AuthorizationRequest applyIdpHint(
            HttpServletRequest request,
            OAuth2AuthorizationRequest authorizationRequest
    ) {
        if (authorizationRequest == null) {
            return null;
        }

        if (!REGISTRATION_ID.equals(
                authorizationRequest.getAttribute(
                        "registration_id"
                )
        )) {
            // 아래 registrationId 검사는 별도 방식으로 처리한다.
        }

        String idpHint = request.getParameter(IDP_HINT_PARAMETER);

        if (idpHint == null || !ALLOWED_IDP_ALIASES.contains(idpHint)) {
            return authorizationRequest;
        }

        return OAuth2AuthorizationRequest
                .from(authorizationRequest)
                .additionalParameters(parameters ->
                        parameters.put(IDP_HINT_PARAMETER, idpHint)
                )
                .build();
    }
}