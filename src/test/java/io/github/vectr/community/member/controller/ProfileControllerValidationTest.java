package io.github.vectr.community.member.controller;

import io.github.vectr.community.member.api.profile.ProfileCommandService;
import io.github.vectr.community.member.api.profile.ProfileQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ProfileController.class,
        excludeAutoConfiguration = OAuth2ClientAutoConfiguration.class
)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileCommandService profileCommandService;

    @MockitoBean
    private ProfileQueryService profileQueryService;


    @Test
    @DisplayName("userId가 0이면 프로필 조회에 실패한다")
    void getProfileWhenUserIdIsZero() throws Exception {
        // when & then
        mockMvc.perform(get("/profiles/{userId}", 0L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(profileQueryService);
    }


    @Test
    @DisplayName("userId가 음수이면 프로필 조회에 실패한다")
    void getProfileWhenUserIdIsNegative() throws Exception {
        // when & then
        mockMvc.perform(get("/profiles/{userId}", -1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(profileQueryService);
    }


    @Test
    @DisplayName("userId가 숫자가 아니면 프로필 조회에 실패한다")
    void getProfileWhenUserIdIsNotNumeric() throws Exception {
        // when & then
        mockMvc.perform(get("/profiles/{userId}", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(profileQueryService);
    }
}