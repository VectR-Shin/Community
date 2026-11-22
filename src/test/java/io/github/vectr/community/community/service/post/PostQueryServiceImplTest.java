package io.github.vectr.community.community.service.post;

import io.github.vectr.community.common.dto.PageResponseDTO;
import io.github.vectr.community.community.dto.post.PostSummaryResponseDTO;
import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.enums.PostType;
import io.github.vectr.community.community.repository.post.PostRepository;
import io.github.vectr.community.member.api.user.UserQueryService;
import io.github.vectr.community.member.dto.user.AuthorInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostQueryServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private PostQueryServiceImpl postQueryService;

    private AuthorInfoDTO authorInfo;

    @BeforeEach
    void setUp() {
        authorInfo = new AuthorInfoDTO(10L, "테스트닉네임", false);
    }

    @Test
    void getUserPosts_게시글목록조회_성공() {
        // given
        long memberId = 10L;
        int page = 1;
        int size = 2;

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, size,
                Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("id")
                )
        );

        Post post1 = createPost(
                1L, 100L, "게시판1", memberId,
                "게시글 제목1", 100, 10, 5, 1, true,
                Instant.parse("2026-11-20T10:00:00Z")
        );

        Post post2 = createPost(
                2L, 200L, "게시판2", memberId,
                "게시글 제목2", 200, 20, 10, 2, false,
                Instant.parse("2026-11-19T10:00:00Z")
        );

        Page<Post> postPage = new PageImpl<>(
                List.of(post1, post2),
                pageable,
                5
        );

        when(postRepository.findAllByMemberIdAndPostTypeAndBoardNotDeleted(
                memberId, PostType.POST, pageable
        )).thenReturn(postPage);

        when(userQueryService.getAuthorInfo(memberId))
                .thenReturn(authorInfo);

        // when
        PageResponseDTO<PostSummaryResponseDTO> result =
                postQueryService.getUserPosts(memberId, page, size);

        // then
        assertThat(result.content()).hasSize(2);

        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.totalElements()).isEqualTo(5);
        assertThat(result.totalPages()).isEqualTo(3);

        PostSummaryResponseDTO dto1 = result.content().get(0);

        assertThat(dto1.postId()).isEqualTo(1L);
        assertThat(dto1.boardId()).isEqualTo(100L);
        assertThat(dto1.boardName()).isEqualTo("게시판1");
        assertThat(dto1.authorId()).isEqualTo(memberId);
        assertThat(dto1.authorNickname()).isEqualTo("테스트닉네임");
        assertThat(dto1.title()).isEqualTo("게시글 제목1");
        assertThat(dto1.viewCount()).isEqualTo(100);
        assertThat(dto1.commentCount()).isEqualTo(10);
        assertThat(dto1.likeCount()).isEqualTo(5);
        assertThat(dto1.dislikeCount()).isEqualTo(1);
        assertThat(dto1.isPopular()).isTrue();
        assertThat(dto1.createdAt())
                .isEqualTo(Instant.parse("2026-11-20T10:00:00Z"));

        PostSummaryResponseDTO dto2 = result.content().get(1);

        assertThat(dto2.postId()).isEqualTo(2L);
        assertThat(dto2.boardId()).isEqualTo(200L);
        assertThat(dto2.boardName()).isEqualTo("게시판2");
        assertThat(dto2.authorNickname()).isEqualTo("테스트닉네임");
        assertThat(dto2.title()).isEqualTo("게시글 제목2");
        assertThat(dto2.isPopular()).isFalse();

        verify(postRepository).findAllByMemberIdAndPostTypeAndBoardNotDeleted(
                memberId, PostType.POST, pageable
        );

        verify(userQueryService).getAuthorInfo(memberId);
    }

    @Test
    void getUserPosts_탈퇴한사용자_닉네임변환() {
        // given
        long memberId = 10L;
        int page = 0;
        int size = 10;

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, size,
                Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("id")
                )
        );

        AuthorInfoDTO withdrawnAuthor =
                new AuthorInfoDTO(memberId, "테스트닉네임", true);

        Post post = createPost(
                1L, 100L, "게시판1", memberId,
                "게시글 제목", 100, 10, 5, 1, false,
                Instant.parse("2026-11-20T10:00:00Z")
        );

        Page<Post> postPage = new PageImpl<>(
                List.of(post),
                pageable,
                1
        );

        when(postRepository.findAllByMemberIdAndPostTypeAndBoardNotDeleted(
                memberId, PostType.POST, pageable
        )).thenReturn(postPage);

        when(userQueryService.getAuthorInfo(memberId))
                .thenReturn(withdrawnAuthor);

        // when
        PageResponseDTO<PostSummaryResponseDTO> result =
                postQueryService.getUserPosts(memberId, page, size);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).authorNickname())
                .isEqualTo("탈퇴한 사용자");

        verify(userQueryService).getAuthorInfo(memberId);
    }

    @Test
    void getUserPosts_게시글이없는경우_빈목록반환() {
        // given
        long memberId = 10L;
        int page = 0;
        int size = 10;

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, size,
                Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("id")
                )
        );

        Page<Post> emptyPage = Page.empty(pageable);

        when(postRepository.findAllByMemberIdAndPostTypeAndBoardNotDeleted(
                memberId, PostType.POST, pageable
        )).thenReturn(emptyPage);

        when(userQueryService.getAuthorInfo(memberId))
                .thenReturn(authorInfo);

        // when
        PageResponseDTO<PostSummaryResponseDTO> result =
                postQueryService.getUserPosts(memberId, page, size);

        // then
        assertThat(result.content()).isEmpty();
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();

        verify(postRepository).findAllByMemberIdAndPostTypeAndBoardNotDeleted(
                memberId, PostType.POST, pageable
        );

        verify(userQueryService).getAuthorInfo(memberId);
    }

    @Test
    void getUserPosts_페이징및정렬조건_확인() {
        // given
        long memberId = 10L;
        int page = 2;
        int size = 5;

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, size,
                Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("id")
                )
        );

        when(postRepository.findAllByMemberIdAndPostTypeAndBoardNotDeleted(
                eq(memberId), eq(PostType.POST), any(Pageable.class)
        )).thenReturn(Page.empty(pageable));

        when(userQueryService.getAuthorInfo(memberId))
                .thenReturn(authorInfo);

        // when
        postQueryService.getUserPosts(memberId, page, size);

        // then
        verify(postRepository).findAllByMemberIdAndPostTypeAndBoardNotDeleted(
                eq(memberId),
                eq(PostType.POST),
                argThat(actual -> {
                    if (actual == null) {
                        return false;
                    }

                    return actual.getPageNumber() == 2
                            && actual.getPageSize() == 5
                            && actual.getSort().getOrderFor("createdAt") != null
                            && actual.getSort().getOrderFor("createdAt").getDirection()
                            == Sort.Direction.DESC
                            && actual.getSort().getOrderFor("id") != null
                            && actual.getSort().getOrderFor("id").getDirection()
                            == Sort.Direction.DESC;
                })
        );
    }

    private Post createPost(
            Long postId,
            Long boardId,
            String boardName,
            Long memberId,
            String title,
            int viewCount,
            int commentCount,
            int likeCount,
            int dislikeCount,
            boolean popular,
            Instant createdAt
    ) {
        Board board = mock(Board.class);

        when(board.getId()).thenReturn(boardId);
        when(board.getName()).thenReturn(boardName);

        Post post = mock(Post.class);

        when(post.getId()).thenReturn(postId);
        when(post.getBoard()).thenReturn(board);
        when(post.getMemberId()).thenReturn(memberId);
        when(post.getTitle()).thenReturn(title);
        when(post.getViewCount()).thenReturn((long) viewCount);
        when(post.getCommentCount()).thenReturn((long) commentCount);
        when(post.getLikeCount()).thenReturn((long) likeCount);
        when(post.getDislikeCount()).thenReturn((long) dislikeCount);
        when(post.isPopular()).thenReturn(popular);
        when(post.getCreatedAt()).thenReturn(createdAt);

        return post;
    }
}