package io.github.vectr.community.community.service.post;

import io.github.vectr.community.common.dto.PageResponseDTO;
import io.github.vectr.community.community.dto.post.PostSummaryResponseDTO;
import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.repository.post.PostRepository;
import io.github.vectr.community.member.api.user.UserQueryService;
import io.github.vectr.community.member.dto.user.AuthorInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostQueryServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private PostQueryServiceImpl postQueryService;

    private Long memberId;
    private AuthorInfoDTO authorInfoDTO;

    @BeforeEach
    void setUp() {
        memberId = 1L;

        authorInfoDTO = new AuthorInfoDTO(
                memberId,
                "테스트닉네임",
                false
        );
    }

    // 게시글 목록 조회 성공
    @Test
    @DisplayName("사용자가 작성한 게시글 목록을 조회한다.")
    void getUserPosts_success() {
        // given
        int page = 0;
        int size = 10;

        Post post = createPostMock(memberId);

        Page<Post> postPage = new PageImpl<>(
                List.of(post),
                PageRequest.of(page, size),
                1
        );

        given(postRepository.findAllByMemberIdAndBoardNotDeleted(
                eq(memberId),
                any(Pageable.class)
        )).willReturn(postPage);

        given(userQueryService.getAuthorInfo(memberId))
                .willReturn(authorInfoDTO);

        // when
        PageResponseDTO<PostSummaryResponseDTO> result =
                postQueryService.getUserPosts(memberId, page, size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);

        PostSummaryResponseDTO response = result.content().get(0);

        assertThat(response.postId()).isEqualTo(1L);
        assertThat(response.boardId()).isEqualTo(10L);
        assertThat(response.boardName()).isEqualTo("자유게시판");
        assertThat(response.authorId()).isEqualTo(memberId);
        assertThat(response.authorNickname()).isEqualTo("테스트닉네임");
        assertThat(response.title()).isEqualTo("테스트 게시글");
        assertThat(response.viewCount()).isEqualTo(100L);
        assertThat(response.commentCount()).isEqualTo(5L);
        assertThat(response.likeCount()).isEqualTo(10L);
        assertThat(response.dislikeCount()).isEqualTo(2L);
        assertThat(response.isPopular()).isTrue();

        verify(postRepository).findAllByMemberIdAndBoardNotDeleted(
                eq(memberId),
                any(Pageable.class)
        );

        verify(userQueryService).getAuthorInfo(memberId);
    }

    // 탈퇴한 사용자
    @Test
    @DisplayName("탈퇴한 사용자의 닉네임은 '탈퇴한 사용자'로 표시한다.")
    void getUserPosts_withdrawn() {
        // given
        Post post = createPostMock(memberId);

        AuthorInfoDTO withdrawnAuthor = new AuthorInfoDTO(
                memberId,
                "기존닉네임",
                true
        );

        given(postRepository.findAllByMemberIdAndBoardNotDeleted(
                eq(memberId),
                any(Pageable.class)
        )).willReturn(new PageImpl<>(List.of(post)));

        given(userQueryService.getAuthorInfo(memberId))
                .willReturn(withdrawnAuthor);

        // when
        PageResponseDTO<PostSummaryResponseDTO> result =
                postQueryService.getUserPosts(memberId, 0, 10);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).authorNickname())
                .isEqualTo("탈퇴한 사용자");
    }

    // 페이징 및 정렬
    @Test
    @DisplayName("게시글 목록을 생성일과 ID 내림차순으로 페이징 조회한다.")
    void getUserPosts_pagingAndSorting() {
        // given
        int page = 1;
        int size = 5;

        given(postRepository.findAllByMemberIdAndBoardNotDeleted(
                eq(memberId),
                any(Pageable.class)
        )).willReturn(new PageImpl<>(
                List.of(),
                PageRequest.of(page, size),
                12
        ));

        given(userQueryService.getAuthorInfo(memberId))
                .willReturn(authorInfoDTO);

        // when
        postQueryService.getUserPosts(memberId, page, size);

        // then
        verify(postRepository).findAllByMemberIdAndBoardNotDeleted(
                eq(memberId),
                argThat(pageable -> {
                    Sort sort = pageable.getSort();

                    return pageable.getPageNumber() == 1
                            && pageable.getPageSize() == 5
                            && sort.getOrderFor("createdAt") != null
                            && sort.getOrderFor("createdAt").getDirection()
                            == Sort.Direction.DESC
                            && sort.getOrderFor("id") != null
                            && sort.getOrderFor("id").getDirection()
                            == Sort.Direction.DESC;
                })
        );
    }

    // 빈 목록
    @Test
    @DisplayName("조회할 게시글이 없으면 빈 페이지를 반환한다.")
    void getUserPosts_empty() {
        // given
        int page = 0;
        int size = 10;

        given(postRepository.findAllByMemberIdAndBoardNotDeleted(
                eq(memberId),
                any(Pageable.class)
        )).willReturn(new PageImpl<>(List.of()));

        given(userQueryService.getAuthorInfo(memberId))
                .willReturn(authorInfoDTO);

        // when
        PageResponseDTO<PostSummaryResponseDTO> result =
                postQueryService.getUserPosts(memberId, page, size);

        // then
        assertThat(result.content()).isEmpty();
    }

    // 게시글 Mock 생성
    private Post createPostMock(Long memberId) {
        Post post = mock(Post.class);
        Board board = mock(Board.class);

        given(post.getId()).willReturn(1L);
        given(post.getBoard()).willReturn(board);
        given(board.getId()).willReturn(10L);
        given(board.getName()).willReturn("자유게시판");

        given(post.getMemberId()).willReturn(memberId);
        given(post.getTitle()).willReturn("테스트 게시글");
        given(post.getViewCount()).willReturn(100L);
        given(post.getCommentCount()).willReturn(5L);
        given(post.getLikeCount()).willReturn(10L);
        given(post.getDislikeCount()).willReturn(2L);
        given(post.isPopular()).willReturn(true);
        given(post.getCreatedAt()).willReturn(
                Instant.parse("2026-11-07T09:00:00Z")
        );

        return post;
    }
}