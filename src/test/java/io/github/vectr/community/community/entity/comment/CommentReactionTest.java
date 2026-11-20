package io.github.vectr.community.community.entity.comment;

import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.board.BoardCategory;
import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.enums.PostType;
import io.github.vectr.community.community.enums.ReactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class CommentReactionTest {

    @Test
    @DisplayName("댓글 반응을 정상적으로 생성한다")
    void createCommentReaction() {
        // given
        Long memberId = 1L;
        Comment comment = createDefaultComment();
        ReactionType reactionType = ReactionType.LIKE;

        // when
        CommentReaction reaction = new CommentReaction(
                memberId,
                comment,
                reactionType
        );

        // then
        assertThat(reaction.getMemberId()).isEqualTo(memberId);
        assertThat(reaction.getComment()).isEqualTo(comment);
        assertThat(reaction.getReactionType()).isEqualTo(reactionType);
    }

    @Test
    @DisplayName("회원 ID가 null이면 예외가 발생한다")
    void createWithNullMemberId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new CommentReaction(
                        null,
                        createDefaultComment(),
                        ReactionType.LIKE
                ));
    }

    @Test
    @DisplayName("댓글이 null이면 예외가 발생한다")
    void createWithNullComment() {
        assertThatNullPointerException()
                .isThrownBy(() -> new CommentReaction(
                        1L,
                        null,
                        ReactionType.LIKE
                ));
    }

    @Test
    @DisplayName("반응 유형이 null이면 예외가 발생한다")
    void createWithNullReactionType() {
        assertThatNullPointerException()
                .isThrownBy(() -> new CommentReaction(
                        1L,
                        createDefaultComment(),
                        null
                ));
    }

    @Test
    @DisplayName("비추천을 정상적으로 생성한다")
    void createDislikeReaction() {
        // given
        Comment comment = createDefaultComment();

        // when
        CommentReaction reaction = new CommentReaction(
                1L,
                comment,
                ReactionType.DISLIKE
        );

        // then
        assertThat(reaction.getReactionType()).isEqualTo(ReactionType.DISLIKE);
    }

    private Comment createDefaultComment() {
        return new Comment(
                1L,
                createDefaultPost(),
                null,
                "댓글 내용"
        );
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
                "테스트 설명"
        );
    }
}
