
package io.github.vectr.community.community.controller.board;

import io.github.vectr.community.common.dto.PageResponseDTO;
import io.github.vectr.community.community.api.board.BoardFavoriteQueryService;
import io.github.vectr.community.community.controller.BoardController;
import io.github.vectr.community.community.dto.board.BoardSummaryResponseDTO;
import io.github.vectr.community.config.security.oauth2.CustomOidcUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardControllerTest {

    @Mock
    private BoardFavoriteQueryService boardFavoriteQueryService;

    @Mock
    private CustomOidcUser principal;

    @InjectMocks
    private BoardController boardController;

    @Test
    @DisplayName("현재 사용자의 즐겨찾기 게시판 목록을 조회한다.")
    void getMyFavoriteBoards_success() {
        // given
        Long memberId = 1L;
        int page = 0;
        int size = 20;

        List<BoardSummaryResponseDTO> content = List.of(
                new BoardSummaryResponseDTO(1L, "자유게시판", 1L, "일반"),
                new BoardSummaryResponseDTO(2L, "공지사항", 1L, "일반")
        );

        PageResponseDTO<BoardSummaryResponseDTO> serviceResponse =
                new PageResponseDTO<>(content, page, size, 2L, 1);

        when(principal.getMemberId()).thenReturn(memberId);

        when(boardFavoriteQueryService.getFavoriteBoards(memberId, page, size))
                .thenReturn(serviceResponse);

        // when
        PageResponseDTO<BoardSummaryResponseDTO> response =
                boardController.getMyFavoriteBoards(principal, page, size);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(2);

        assertThat(response.content().get(0).boardName())
                .isEqualTo("자유게시판");
        assertThat(response.content().get(1).boardName())
                .isEqualTo("공지사항");

        assertThat(response.page()).isEqualTo(page);
        assertThat(response.size()).isEqualTo(size);
        assertThat(response.totalElements()).isEqualTo(2L);
        assertThat(response.totalPages()).isEqualTo(1);

        verify(boardFavoriteQueryService).getFavoriteBoards(memberId, page, size);
    }

    @Test
    @DisplayName("즐겨찾기한 게시판이 없으면 빈 목록을 반환한다.")
    void getMyFavoriteBoards_empty() {
        // given
        Long memberId = 1L;
        int page = 0;
        int size = 20;

        PageResponseDTO<BoardSummaryResponseDTO> serviceResponse =
                new PageResponseDTO<>(List.of(), page, size, 0L, 0);

        when(principal.getMemberId()).thenReturn(memberId);

        when(boardFavoriteQueryService.getFavoriteBoards(memberId, page, size))
                .thenReturn(serviceResponse);

        // when
        PageResponseDTO<BoardSummaryResponseDTO> response =
                boardController.getMyFavoriteBoards(principal, page, size);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).isEmpty();
        assertThat(response.totalElements()).isZero();
        assertThat(response.totalPages()).isZero();

        verify(boardFavoriteQueryService).getFavoriteBoards(memberId, page, size);
    }
}
