package io.github.vectr.community.community.entity.post;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class SystemNoticeTest {

    @Test
    void 생성자_호출시_필드가_정상적으로_초기화된다() {
        // given
        Long memberId = 1L;
        String title = "공지 제목";
        String content = "공지 내용";

        // when
        SystemNotice systemNotice = new SystemNotice(memberId, title, content);

        // then
        assertThat(systemNotice.getMemberId()).isEqualTo(memberId);
        assertThat(systemNotice.getTitle()).isEqualTo(title);
        assertThat(systemNotice.getContent()).isEqualTo(content);
        assertThat(systemNotice.getViewCount()).isEqualTo(0L);
        assertThat(systemNotice.getDeletedAt()).isNull();
    }

    @Test
    void memberId가_null이면_NullPointerException이_발생한다() {
        // given
        String title = "공지 제목";
        String content = "공지 내용";

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new SystemNotice(null, title, content));
    }

    @Test
    void title이_null이면_NullPointerException이_발생한다() {
        // given
        Long memberId = 1L;
        String content = "공지 내용";

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new SystemNotice(memberId, null, content));
    }

    @Test
    void content가_null이면_NullPointerException이_발생한다() {
        // given
        Long memberId = 1L;
        String title = "공지 제목";

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new SystemNotice(memberId, title, null));
    }

    @Test
    void 제목을_변경할_수_있다() {
        // given
        SystemNotice systemNotice = new SystemNotice(1L, "기존 제목", "공지 내용");
        String newTitle = "변경된 제목";

        // when
        systemNotice.changeTitle(newTitle);

        // then
        assertThat(systemNotice.getTitle()).isEqualTo(newTitle);
    }

    @Test
    void changeTitle에_null을_전달하면_NullPointerException이_발생한다() {
        // given
        SystemNotice systemNotice = new SystemNotice(1L, "기존 제목", "공지 내용");

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> systemNotice.changeTitle(null));
    }

    @Test
    void 내용을_변경할_수_있다() {
        // given
        SystemNotice systemNotice = new SystemNotice(1L, "공지 제목", "기존 내용");
        String newContent = "변경된 내용";

        // when
        systemNotice.changeContent(newContent);

        // then
        assertThat(systemNotice.getContent()).isEqualTo(newContent);
    }

    @Test
    void changeContent에_null을_전달하면_NullPointerException이_발생한다() {
        // given
        SystemNotice systemNotice = new SystemNotice(1L, "공지 제목", "기존 내용");

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> systemNotice.changeContent(null));
    }

    @Test
    void 삭제하면_deletedAt이_설정된다() {
        // given
        SystemNotice systemNotice = new SystemNotice(1L, "공지 제목", "공지 내용");

        // when
        systemNotice.delete();

        // then
        assertThat(systemNotice.getDeletedAt()).isNotNull();
        assertThat(systemNotice.getDeletedAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void 복구하면_deletedAt이_null이_된다() {
        // given
        SystemNotice systemNotice = new SystemNotice(1L, "공지 제목", "공지 내용");
        systemNotice.delete();

        // when
        systemNotice.restore();

        // then
        assertThat(systemNotice.getDeletedAt()).isNull();
    }
}