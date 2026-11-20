package io.github.vectr.community.community.entity.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardCategoryTest {

    @Test
    @DisplayName("BoardCategory 객체를 정상적으로 생성한다.")
    void createBoardCategory() {
        // given
        String name = "자유";

        // when
        BoardCategory category = new BoardCategory(name);

        // then
        assertThat(category.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Name이 null이면 예외가 발생한다.")
    void createBoardCategoryWithNullName() {
        // when & then
        assertThatThrownBy(() -> new BoardCategory(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("카테고리 이름을 정상적으로 변경한다.")
    void changeName() {
        // given
        BoardCategory category = new BoardCategory("자유");
        String newName = "공지사항";

        // when
        category.changeName(newName);

        // then
        assertThat(category.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("변경할 이름이 null이면 예외가 발생한다.")
    void changeNameWithNull() {
        // given
        BoardCategory category = new BoardCategory("자유");

        // when & then
        assertThatThrownBy(() -> category.changeName(null))
                .isInstanceOf(NullPointerException.class);
    }
}