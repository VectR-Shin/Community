package io.github.vectr.community.community.entity.post;

import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.board.BoardCategory;
import io.github.vectr.community.community.enums.PostType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class PostTest {

    @Test
    @DisplayName("게시글을 정상적으로 생성한다")
    void createPost() {
        // given
        Long memberId = 1L;
        Board board = createDefaultBoard();
        String title = "게시글 제목";
        String content = "게시글 내용";
        PostType postType = PostType.POST;

        // when
        Post post = new Post(
                memberId,
                board,
                title,
                content,
                postType
        );

        // then
        assertThat(post.getMemberId()).isEqualTo(memberId);
        assertThat(post.getBoard()).isEqualTo(board);
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getPostType()).isEqualTo(postType);

        assertThat(post.getCommentCount()).isZero();
        assertThat(post.getViewCount()).isZero();
        assertThat(post.getLikeCount()).isZero();
        assertThat(post.getDislikeCount()).isZero();

        assertThat(post.isPopular()).isFalse();
        assertThat(post.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("회원 ID가 null이면 예외가 발생한다")
    void createWithNullMemberId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Post(
                        null,
                        createDefaultBoard(),
                        "게시글 제목",
                        "게시글 내용",
                        PostType.POST
                ));
    }

    @Test
    @DisplayName("게시판이 null이면 예외가 발생한다")
    void createWithNullBoard() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Post(
                        1L,
                        null,
                        "게시글 제목",
                        "게시글 내용",
                        PostType.POST
                ));
    }

    @Test
    @DisplayName("제목이 null이면 예외가 발생한다")
    void createWithNullTitle() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Post(
                        1L,
                        createDefaultBoard(),
                        null,
                        "게시글 내용",
                        PostType.POST
                ));
    }

    @Test
    @DisplayName("내용이 null이면 예외가 발생한다")
    void createWithNullContent() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Post(
                        1L,
                        createDefaultBoard(),
                        "게시글 제목",
                        null,
                        PostType.POST
                ));
    }

    @Test
    @DisplayName("게시글 유형이 null이면 예외가 발생한다")
    void createWithNullPostType() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Post(
                        1L,
                        createDefaultBoard(),
                        "게시글 제목",
                        "게시글 내용",
                        null
                ));
    }

    @Test
    @DisplayName("게시판을 변경한다")
    void changeBoard() {
        // given
        Post post = createDefaultPost();
        Board newBoard = new Board(
                new BoardCategory("공지사항"),
                "새로운 게시판",
                "새로운 게시판 설명"
        );

        // when
        post.changeBoard(newBoard);

        // then
        assertThat(post.getBoard()).isEqualTo(newBoard);
    }

    @Test
    @DisplayName("게시판을 null로 변경하면 예외가 발생한다")
    void changeBoardWithNull() {
        // given
        Post post = createDefaultPost();

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> post.changeBoard(null));
    }

    @Test
    @DisplayName("게시글 제목을 변경한다")
    void changeTitle() {
        // given
        Post post = createDefaultPost();
        String newTitle = "수정된 게시글 제목";

        // when
        post.changeTitle(newTitle);

        // then
        assertThat(post.getTitle()).isEqualTo(newTitle);
    }

    @Test
    @DisplayName("게시글 제목을 null로 변경하면 예외가 발생한다")
    void changeTitleWithNull() {
        // given
        Post post = createDefaultPost();

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> post.changeTitle(null));
    }

    @Test
    @DisplayName("게시글 내용을 변경한다")
    void changeContent() {
        // given
        Post post = createDefaultPost();
        String newContent = "수정된 게시글 내용";

        // when
        post.changeContent(newContent);

        // then
        assertThat(post.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("게시글 내용을 null로 변경하면 예외가 발생한다")
    void changeContentWithNull() {
        // given
        Post post = createDefaultPost();

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> post.changeContent(null));
    }

    @Test
    @DisplayName("게시글 유형을 변경한다")
    void changePostType() {
        // given
        Post post = createDefaultPost();

        // when
        post.changePostType(PostType.NOTICE);

        // then
        assertThat(post.getPostType()).isEqualTo(PostType.NOTICE);
    }

    @Test
    @DisplayName("게시글 유형을 null로 변경하면 예외가 발생한다")
    void changePostTypeWithNull() {
        // given
        Post post = createDefaultPost();

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> post.changePostType(null));
    }

    @Test
    @DisplayName("인기 게시글 상태를 true로 변경한다")
    void changePopularToTrue() {
        // given
        Post post = createDefaultPost();

        // when
        post.changePopular(true);

        // then
        assertThat(post.isPopular()).isTrue();
    }

    @Test
    @DisplayName("인기 게시글 상태를 false로 변경한다")
    void changePopularToFalse() {
        // given
        Post post = createDefaultPost();
        post.changePopular(true);

        // when
        post.changePopular(false);

        // then
        assertThat(post.isPopular()).isFalse();
    }

    @Test
    @DisplayName("게시글을 삭제한다")
    void deletePost() {
        // given
        Post post = createDefaultPost();

        // when
        post.delete();

        // then
        assertThat(post.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("삭제된 게시글을 복구한다")
    void restorePost() {
        // given
        Post post = createDefaultPost();
        post.delete();

        // when
        post.restore();

        // then
        assertThat(post.getDeletedAt()).isNull();
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