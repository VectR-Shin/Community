
package io.github.vectr.community.community.controller.post;

import io.github.vectr.community.common.dto.PageResponseDTO;
import io.github.vectr.community.community.api.post.PostQueryService;
import io.github.vectr.community.community.controller.PostController;
import io.github.vectr.community.community.dto.post.PostSummaryResponseDTO;
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
class PostControllerTest {

    @Mock
    private PostQueryService postQueryService;

    @Mock
    private CustomOidcUser principal;

    @InjectMocks
    private PostController postController;

    @Test
    @DisplayName("현재 사용자가 작성한 게시글 목록을 조회한다.")
    void getMyPosts_success() {
        // given
        Long memberId = 1L;
        int page = 0;
        int size = 20;

        List<PostSummaryResponseDTO> content = List.of(
                new PostSummaryResponseDTO(
                        1L, 1L, "자유게시판",
                        memberId, "테스트회원",
                        "첫 번째 게시글",
                        10L, 2L, 5L, 0L,
                        false, null
                ),
                new PostSummaryResponseDTO(
                        2L, 1L, "자유게시판",
                        memberId, "테스트회원",
                        "두 번째 게시글",
                        20L, 3L, 8L, 1L,
                        true, null
                )
        );

        PageResponseDTO<PostSummaryResponseDTO> serviceResponse =
                new PageResponseDTO<>(content, page, size, 2L, 1);

        when(principal.getMemberId()).thenReturn(memberId);

        when(postQueryService.getUserPosts(memberId, page, size))
                .thenReturn(serviceResponse);

        // when
        PageResponseDTO<PostSummaryResponseDTO> response =
                postController.getMyPosts(principal, page, size);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(2);

        assertThat(response.content().get(0).postId())
                .isEqualTo(1L);
        assertThat(response.content().get(0).title())
                .isEqualTo("첫 번째 게시글");

        assertThat(response.content().get(1).postId())
                .isEqualTo(2L);
        assertThat(response.content().get(1).title())
                .isEqualTo("두 번째 게시글");

        assertThat(response.page()).isEqualTo(page);
        assertThat(response.size()).isEqualTo(size);
        assertThat(response.totalElements()).isEqualTo(2L);
        assertThat(response.totalPages()).isEqualTo(1);

        verify(postQueryService).getUserPosts(memberId, page, size);
    }

    @Test
    @DisplayName("작성한 게시글이 없으면 빈 목록을 반환한다.")
    void getMyPosts_empty() {
        // given
        Long memberId = 1L;
        int page = 0;
        int size = 20;

        PageResponseDTO<PostSummaryResponseDTO> serviceResponse =
                new PageResponseDTO<>(List.of(), page, size, 0L, 0);

        when(principal.getMemberId()).thenReturn(memberId);

        when(postQueryService.getUserPosts(memberId, page, size))
                .thenReturn(serviceResponse);

        // when
        PageResponseDTO<PostSummaryResponseDTO> response =
                postController.getMyPosts(principal, page, size);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).isEmpty();
        assertThat(response.totalElements()).isZero();
        assertThat(response.totalPages()).isZero();

        verify(postQueryService).getUserPosts(memberId, page, size);
    }
}
