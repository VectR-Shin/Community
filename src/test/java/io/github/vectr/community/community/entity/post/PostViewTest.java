package io.github.vectr.community.community.entity.post;

import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.board.BoardCategory;
import io.github.vectr.community.community.enums.PostType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class PostViewTest {

    @Test
    @DisplayName("게시글 조회 기록을 정상적으로 생성한다")
    void createPostView() {
        // given
        Post post = createDefaultPost();
        String uuid = "test-uuid";

        Instant before = Instant.now();

        // when
        PostView postView = new PostView(post, uuid);

        Instant after = Instant.now();

        // then
        assertThat(postView.getPost()).isEqualTo(post);
        assertThat(postView.getUuid()).isEqualTo(uuid);
        assertThat(postView.getLastViewedAt())
                .isBetween(before, after);
    }

    @Test
    @DisplayName("게시글이 null이면 예외가 발생한다")
    void createWithNullPost() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PostView(
                        null,
                        "test-uuid"
                ));
    }

    @Test
    @DisplayName("UUID가 null이면 예외가 발생한다")
    void createWithNullUuid() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PostView(
                        createDefaultPost(),
                        null
                ));
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