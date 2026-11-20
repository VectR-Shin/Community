package io.github.vectr.community.community.controller.board;

import io.github.vectr.community.community.api.board.BoardFavoriteQueryService;
import io.github.vectr.community.community.controller.BoardController;
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

@WebMvcTest(BoardController.class)
@Import(ValidationTestSecurityConfig.class)
class BoardControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BoardFavoriteQueryService boardFavoriteQueryService;

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
    void getMyFavoriteBoards_pageNegative() throws Exception {
        mockMvc.perform(get("/users/me/favorite-boards")
                        .with(authentication(createAuthentication()))
                        .param("page", "-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(boardFavoriteQueryService);
    }

    @Test
    @DisplayName("page가 0이면 검증을 통과한다.")
    void getMyFavoriteBoards_pageZero() throws Exception {
        mockMvc.perform(get("/users/me/favorite-boards")
                        .with(authentication(createAuthentication()))
                        .param("page", "0"))
                .andExpect(status().isOk());

        verify(boardFavoriteQueryService).getFavoriteBoards(1L, 0, 20);
    }

    @Test
    @DisplayName("size가 1 미만이면 400을 반환한다.")
    void getMyFavoriteBoards_sizeTooSmall() throws Exception {
        mockMvc.perform(get("/users/me/favorite-boards")
                        .with(authentication(createAuthentication()))
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(boardFavoriteQueryService);
    }

    @Test
    @DisplayName("size가 100을 초과하면 400을 반환한다.")
    void getMyFavoriteBoards_sizeTooLarge() throws Exception {
        mockMvc.perform(get("/users/me/favorite-boards")
                        .with(authentication(createAuthentication()))
                        .param("size", "101"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(boardFavoriteQueryService);
    }

    @Test
    @DisplayName("size가 1이면 검증을 통과한다.")
    void getMyFavoriteBoards_sizeMin() throws Exception {
        mockMvc.perform(get("/users/me/favorite-boards")
                        .with(authentication(createAuthentication()))
                        .param("size", "1"))
                .andExpect(status().isOk());

        verify(boardFavoriteQueryService).getFavoriteBoards(1L, 0, 1);
    }

    @Test
    @DisplayName("size가 100이면 검증을 통과한다.")
    void getMyFavoriteBoards_sizeMax() throws Exception {
        mockMvc.perform(get("/users/me/favorite-boards")
                        .with(authentication(createAuthentication()))
                        .param("size", "100"))
                .andExpect(status().isOk());

        verify(boardFavoriteQueryService).getFavoriteBoards(1L, 0, 100);
    }
}