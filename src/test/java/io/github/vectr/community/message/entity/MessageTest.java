package io.github.vectr.community.message.entity;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.message.exception.MessageErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MessageTest {

    @Test
    @DisplayName("정상적으로 메시지를 생성한다.")
    void createMessage() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        String content = "안녕하세요.";

        // when
        Message message = new Message(senderId, receiverId, content);

        // then
        assertThat(message.getSenderId()).isEqualTo(senderId);
        assertThat(message.getReceiverId()).isEqualTo(receiverId);
        assertThat(message.getContent()).isEqualTo(content);
        assertThat(message.isRead()).isFalse();
    }

    @Test
    @DisplayName("발신자 ID가 null이면 예외가 발생한다.")
    void createMessageWithNullSenderId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Message(
                        null,
                        2L,
                        "안녕하세요."
                ));
    }

    @Test
    @DisplayName("수신자 ID가 null이면 예외가 발생한다.")
    void createMessageWithNullReceiverId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Message(
                        1L,
                        null,
                        "안녕하세요."
                ));
    }

    @Test
    @DisplayName("메시지 내용이 null이면 예외가 발생한다.")
    void createMessageWithNullContent() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Message(
                        1L,
                        2L,
                        null
                ));
    }

    @Test
    @DisplayName("자기 자신에게 메시지를 보내면 예외가 발생한다.")
    void createSelfMessage() {
        // given
        Long memberId = 1L;

        // when & then
        assertThatThrownBy(() ->
                new Message(memberId, memberId, "안녕하세요.")
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(MessageErrorCode.SELF_MESSAGE_NOT_ALLOWED);
    }

    @Test
    @DisplayName("메시지를 읽으면 읽음 상태로 변경된다.")
    void readMessage() {
        // given
        Message message = new Message(
                1L,
                2L,
                "안녕하세요."
        );

        // when
        message.readMessage();

        // then
        assertThat(message.isRead()).isTrue();
    }

    @Test
    @DisplayName("이미 읽은 메시지를 다시 읽어도 읽음 상태가 유지된다.")
    void readAlreadyReadMessage() {
        // given
        Message message = new Message(
                1L,
                2L,
                "안녕하세요."
        );

        message.readMessage();

        // when
        message.readMessage();

        // then
        assertThat(message.isRead()).isTrue();
    }
}