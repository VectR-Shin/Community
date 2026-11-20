package io.github.vectr.community.community.repository.comment;

import io.github.vectr.community.community.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 삭제되지 않은 특정 댓글 반환
    Optional<Comment> findByIdAndDeletedAtIsNull(Long commentId);
}
