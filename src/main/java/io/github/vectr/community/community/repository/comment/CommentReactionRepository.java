package io.github.vectr.community.community.repository.comment;

import io.github.vectr.community.community.entity.comment.CommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

}
