package io.github.vectr.community.config.security.oauth2.logout;

import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomOidcLogoutSuccessHandler extends OidcClientInitiatedLogoutSuccessHandler {

    public CustomOidcLogoutSuccessHandler(
            ClientRegistrationRepository clientRegistrationRepository) {

        super(clientRegistrationRepository);
        setPostLogoutRedirectUri("{baseUrl}");
    }
}
