package io.github.vectr.community.community.entity.comment;

import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.board.BoardCategory;
import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.enums.PostType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class CommentTest {

    @Test
    @DisplayName("댓글을 정상적으로 생성한다")
    void createComment() {
        // given
        Long memberId = 1L;
        Post post = createDefaultPost();
        Comment parent = null;
        String content = "댓글 내용";

        // when
        Comment comment = new Comment(memberId, post, parent, content);

        // then
        assertThat(comment.getMemberId()).isEqualTo(memberId);
        assertThat(comment.getPost()).isEqualTo(post);
        assertThat(comment.getParent()).isNull();
        assertThat(comment.getContent()).isEqualTo(content);
        assertThat(comment.getLikeCount()).isZero();
        assertThat(comment.getDislikeCount()).isZero();
        assertThat(comment.isPopular()).isFalse();
        assertThat(comment.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("부모 댓글이 있는 대댓글을 생성한다")
    void createReply() {
        // given
        Comment parent = createDefaultComment();
        Long memberId = 2L;
        Post post = parent.getPost();
        String content = "대댓글 내용";

        // when
        Comment reply = new Comment(memberId, post, parent, content);

        // then
        assertThat(reply.getMemberId()).isEqualTo(memberId);
        assertThat(reply.getPost()).isEqualTo(post);
        assertThat(reply.getParent()).isEqualTo(parent);
        assertThat(reply.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("회원 ID가 null이면 예외가 발생한다")
    void createWithNullMemberId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Comment(
                        null,
                        createDefaultPost(),
                        null,
                        "댓글 내용"
                ));
    }

    @Test
    @DisplayName("게시글이 null이면 예외가 발생한다")
    void createWithNullPost() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Comment(
                        1L,
                        null,
                        null,
                        "댓글 내용"
                ));
    }

    @Test
    @DisplayName("댓글 내용이 null이면 예외가 발생한다")
    void createWithNullContent() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Comment(
                        1L,
                        createDefaultPost(),
                        null,
                        null
                ));
    }

    @Test
    @DisplayName("댓글 내용을 변경한다")
    void changeContent() {
        // given
        Comment comment = createDefaultComment();
        String newContent = "수정된 댓글 내용";

        // when
        comment.changeContent(newContent);

        // then
        assertThat(comment.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("댓글 내용을 null로 변경하면 예외가 발생한다")
    void changeContentWithNull() {
        // given
        Comment comment = createDefaultComment();

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> comment.changeContent(null));
    }

    @Test
    @DisplayName("인기 댓글 상태를 true로 변경한다")
    void changePopularToTrue() {
        // given
        Comment comment = createDefaultComment();

        // when
        comment.changePopular(true);

        // then
        assertThat(comment.isPopular()).isTrue();
    }

    @Test
    @DisplayName("인기 댓글 상태를 false로 변경한다")
    void changePopularToFalse() {
        // given
        Comment comment = createDefaultComment();
        comment.changePopular(true);

        // when
        comment.changePopular(false);

        // then
        assertThat(comment.isPopular()).isFalse();
    }

    @Test
    @DisplayName("댓글을 삭제한다")
    void deleteComment() {
        // given
        Comment comment = createDefaultComment();

        // when
        comment.delete();

        // then
        assertThat(comment.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("삭제된 댓글을 복구한다")
    void restoreComment() {
        // given
        Comment comment = createDefaultComment();
        comment.delete();

        // when
        comment.restore();

        // then
        assertThat(comment.getDeletedAt()).isNull();
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
                new BoardCategory("자유"),
                "테스트 게시판",
                "테스트 설명"
        );
    }
}