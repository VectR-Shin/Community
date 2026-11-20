package io.github.vectr.community.community.entity.comment;

import io.github.vectr.community.common.entity.BaseCreatedAtEntity;
import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.community.enums.ReportProcessStatus;
import io.github.vectr.community.community.enums.ReportReason;
import io.github.vectr.community.community.exception.ReportErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(
        name = "comment_report",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_comment_report_comment_member",
                        columnNames = {"comment_id", "member_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentReport extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(length = 100)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportReason reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "process_status", nullable = false, length = 20)
    private ReportProcessStatus processStatus = ReportProcessStatus.PENDING;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "processed_by")
    private Long processedBy;

    public CommentReport(
            Comment comment,
            Long memberId,
            String content,
            ReportReason reason
    ) {
        this.comment = Objects.requireNonNull(comment);
        this.memberId = Objects.requireNonNull(memberId);
        this.content = content;
        this.reason = Objects.requireNonNull(reason);
    }

    public void process(Long processedBy) {
        this.processedBy = Objects.requireNonNull(processedBy);

        if (this.processStatus != ReportProcessStatus.PENDING) {
            throw new BusinessException(ReportErrorCode.REPORT_ALREADY_PROCESSED);
        }

        this.processedAt = Instant.now();
        this.processStatus = ReportProcessStatus.PROCESSED;
    }

    public void reject(Long processedBy) {
        this.processedBy = Objects.requireNonNull(processedBy);

        if (this.processStatus != ReportProcessStatus.PENDING) {
            throw new BusinessException(ReportErrorCode.REPORT_ALREADY_PROCESSED);
        }

        this.processedAt = Instant.now();
        this.processStatus = ReportProcessStatus.REJECTED;
    }
}
