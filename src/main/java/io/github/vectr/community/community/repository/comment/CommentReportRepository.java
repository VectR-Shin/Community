package io.github.vectr.community.community.repository.comment;

import io.github.vectr.community.community.entity.comment.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    // 사용자가 특정 댓글을 신고한 적이 있는지 반환
    boolean existsByMemberIdAndComment_Id(Long memberId, Long commentId);
}
