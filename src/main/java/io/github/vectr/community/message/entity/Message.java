package io.github.vectr.community.message.entity;

import io.github.vectr.community.common.entity.BaseCreatedAtEntity;
import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.message.exception.MessageErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    public Message(Long senderId, Long receiverId, String content) {
        this.senderId = Objects.requireNonNull(senderId);
        this.receiverId = Objects.requireNonNull(receiverId);
        this.content = Objects.requireNonNull(content);

        if (senderId.equals(receiverId)) {
            throw new BusinessException(MessageErrorCode.SELF_MESSAGE_NOT_ALLOWED);
        }
    }

    public void readMessage() {
        this.isRead = true;
    }
}
