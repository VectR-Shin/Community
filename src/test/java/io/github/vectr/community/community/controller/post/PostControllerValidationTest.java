package io.github.vectr.community.community.controller.post;

import io.github.vectr.community.community.api.post.PostQueryService;
import io.github.vectr.community.community.controller.PostController;
import io.github.vectr.community.config.ValidationTestSecurityConfig;
import io.github.vectr.community.config.security.oauth2.CustomOidcUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@Import(ValidationTestSecurityConfig.class)
class PostControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostQueryService postQueryService;

    private UsernamePasswordAuthenticationToken createAuthentication() {
        CustomOidcUser principal = mock(CustomOidcUser.class);
        when(principal.getMemberId()).thenReturn(1L);

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    @DisplayName("page가 0 미만이면 400을 반환한다.")
    void getMyPosts_pageNegative() throws Exception {
        mockMvc.perform(get("/users/me/posts")
                        .with(authentication(createAuthentication()))
                        .param("page", "-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(postQueryService);
    }

    @Test
    @DisplayName("page가 0이면 검증을 통과한다.")
    void getMyPosts_pageZero() throws Exception {
        mockMvc.perform(get("/users/me/posts")
                        .with(authentication(createAuthentication()))
                        .param("page", "0"))
                .andExpect(status().isOk());

        verify(postQueryService).getUserPosts(1L, 0, 20);
    }

    @Test
    @DisplayName("size가 1 미만이면 400을 반환한다.")
    void getMyPosts_sizeTooSmall() throws Exception {
        mockMvc.perform(get("/users/me/posts")
                        .with(authentication(createAuthentication()))
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(postQueryService);
    }

    @Test
    @DisplayName("size가 100을 초과하면 400을 반환한다.")
    void getMyPosts_sizeTooLarge() throws Exception {
        mockMvc.perform(get("/users/me/posts")
                        .with(authentication(createAuthentication()))
                        .param("size", "101"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(postQueryService);
    }

    @Test
    @DisplayName("size가 1이면 검증을 통과한다.")
    void getMyPosts_sizeMin() throws Exception {
        mockMvc.perform(get("/users/me/posts")
                        .with(authentication(createAuthentication()))
                        .param("size", "1"))
                .andExpect(status().isOk());

        verify(postQueryService).getUserPosts(1L, 0, 1);
    }

    @Test
    @DisplayName("size가 100이면 검증을 통과한다.")
    void getMyPosts_sizeMax() throws Exception {
        mockMvc.perform(get("/users/me/posts")
                        .with(authentication(createAuthentication()))
                        .param("size", "100"))
                .andExpect(status().isOk());

        verify(postQueryService).getUserPosts(1L, 0, 100);
    }
}