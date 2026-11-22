
package io.github.vectr.community.community.entity.post;

import io.github.vectr.community.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "system_notice")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SystemNotice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public SystemNotice(Long memberId, String title, String content) {
        this.memberId = Objects.requireNonNull(memberId);
        this.title = Objects.requireNonNull(title);
        this.content = Objects.requireNonNull(content);
    }

    public void changeTitle(String title) {
        this.title = Objects.requireNonNull(title);
    }

    public void changeContent(String content) {
        this.content = Objects.requireNonNull(content);
    }

    public void delete() {
        this.deletedAt = Instant.now();
    }

    public void restore() {
        this.deletedAt = null;
    }
}
