package io.github.vectr.community.community.repository.post;

import io.github.vectr.community.community.entity.post.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    // 사용자가 특정 게시글을 신고한 적이 있는지 반환
    boolean existsByMemberIdAndPost_Id(Long memberId, Long postId);
}
