package io.github.vectr.community.community.repository.post;

import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.enums.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    // postType(POST, NOTICE)에 해당하는 게시글을 id 로 찾기
    Optional<Post> findByIdAndPostTypeAndDeletedAtIsNull(Long postId, PostType postType);

    // 모든 종류(POST, NOTICE)의 게시글을 id 로 찾기
    Optional<Post> findByIdAndDeletedAtIsNull(Long postId);

    /*
        특정 사용자가 작성한 게시글 목록 조회
        삭제된 게시판의 게시글은 조회 안 함
        공지글도 포함
        paging 기능 포함
    */
    @Query(
            value = """
                SELECT p
                FROM Post p
                JOIN FETCH p.board b
                WHERE p.memberId = :memberId
                  AND b.deletedAt IS NULL
                """,
            countQuery = """
                SELECT COUNT(p)
                FROM Post p
                JOIN p.board b
                WHERE p.memberId = :memberId
                  AND b.deletedAt IS NULL
                """
    )

    Page<Post> findAllByMemberIdAndBoardNotDeleted(@Param("memberId") Long memberId, Pageable pageable);
}
