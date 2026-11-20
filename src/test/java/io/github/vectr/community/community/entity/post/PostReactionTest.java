package io.github.vectr.community.community.entity.post;

import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.board.BoardCategory;
import io.github.vectr.community.community.enums.PostType;
import io.github.vectr.community.community.enums.ReactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class PostReactionTest {

    @Test
    @DisplayName("게시글 반응을 정상적으로 생성한다")
    void createPostReaction() {
        // given
        Long memberId = 1L;
        Post post = createDefaultPost();
        ReactionType reactionType = ReactionType.LIKE;

        // when
        PostReaction reaction = new PostReaction(
                memberId,
                post,
                reactionType
        );

        // then
        assertThat(reaction.getMemberId()).isEqualTo(memberId);
        assertThat(reaction.getPost()).isEqualTo(post);
        assertThat(reaction.getReactionType()).isEqualTo(reactionType);
    }

    @Test
    @DisplayName("회원 ID가 null이면 예외가 발생한다")
    void createWithNullMemberId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PostReaction(
                        null,
                        createDefaultPost(),
                        ReactionType.LIKE
                ));
    }

    @Test
    @DisplayName("게시글이 null이면 예외가 발생한다")
    void createWithNullPost() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PostReaction(
                        1L,
                        null,
                        ReactionType.LIKE
                ));
    }

    @Test
    @DisplayName("반응 유형이 null이면 예외가 발생한다")
    void createWithNullReactionType() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PostReaction(
                        1L,
                        createDefaultPost(),
                        null
                ));
    }

    @Test
    @DisplayName("좋아요 반응을 정상적으로 생성한다")
    void createLikeReaction() {
        // given
        Post post = createDefaultPost();

        // when
        PostReaction reaction = new PostReaction(
                1L,
                post,
                ReactionType.LIKE
        );

        // then
        assertThat(reaction.getReactionType()).isEqualTo(ReactionType.LIKE);
    }

    @Test
    @DisplayName("싫어요 반응을 정상적으로 생성한다")
    void createDislikeReaction() {
        // given
        Post post = createDefaultPost();

        // when
        PostReaction reaction = new PostReaction(
                1L,
                post,
                ReactionType.DISLIKE
        );

        // then
        assertThat(reaction.getReactionType()).isEqualTo(ReactionType.DISLIKE);
    }

    private Post createDefaultPost() {
        return new Post(
                1L,
                createDefaultBoard(),
                "게시글 제목",
                "게시글 내용",
                PostType.POST
        );
    }

    private Board createDefaultBoard() {
        return new Board(
                new BoardCategory("자유게시판"),
                "테스트 게시판",
                "테스트 게시판 설명"
        );
    }
}