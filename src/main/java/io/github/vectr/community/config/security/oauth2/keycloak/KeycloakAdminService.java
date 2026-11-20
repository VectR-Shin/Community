
package io.github.vectr.community.config.security.oauth2.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class KeycloakAdminService {

    private final RestClient.Builder restClientBuilder;

    @Value("${keycloak.admin.base-url}")
    private String baseUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    /*
     * Keycloak Admin API 접근 토큰 발급
     */
    private String getAccessToken() {

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        TokenResponse response = restClientBuilder.build()
                .post()
                .uri(baseUrl + "/realms/" + realm
                        + "/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(TokenResponse.class);

        if (response == null || response.accessToken() == null) {
            throw new IllegalStateException(
                    "Keycloak Admin access token 발급에 실패했습니다."
            );
        }

        return response.accessToken();
    }

    /*
     * Keycloak 사용자 로그아웃 및 삭제
     */
    public void logoutAndDeleteUser(String userId) {

        String accessToken = getAccessToken();

        RestClient client = restClientBuilder.build();

        String userUrl = baseUrl
                + "/admin/realms/" + realm
                + "/users/" + userId;

        // 해당 사용자의 모든 Keycloak 세션 종료
        client.post()
                .uri(userUrl + "/logout")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken
                )
                .retrieve()
                .toBodilessEntity();

        // Keycloak 사용자 삭제
        client.delete()
                .uri(userUrl)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken
                )
                .retrieve()
                .toBodilessEntity();
    }

    private record TokenResponse(
            @JsonProperty("access_token")
            String accessToken
    ) {
    }
}
