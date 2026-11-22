package io.github.vectr.community.community.repository.post;

import io.github.vectr.community.common.config.JpaAuditingTestConfig;
import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.board.BoardCategory;
import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.enums.PostType;
import io.github.vectr.community.member.entity.Member;
import io.github.vectr.community.member.enums.MemberRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("PostRepository 테스트")
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingTestConfig.class)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager entityManager;

    private Long memberId;
    private Long otherMemberId;

    private Board board;
    private Board deletedBoard;

    @BeforeEach
    void setUp() {
        // 회원 생성
        Member member = new Member(
                "test-sub",
                "test@email.com",
                MemberRole.USER
        );

        Member otherMember = new Member(
                "other-sub",
                "other@email.com",
                MemberRole.USER
        );

        entityManager.persist(member);
        entityManager.persist(otherMember);

        entityManager.flush();

        memberId = member.getId();
        otherMemberId = otherMember.getId();

        // 게시판 카테고리 생성
        BoardCategory category = new BoardCategory("일반");
        entityManager.persist(category);

        // 게시판 생성
        board = new Board(category, "자유게시판", "자유게시판 설명");
        deletedBoard = new Board(category, "삭제된 게시판", "삭제된 게시판 설명");

        entityManager.persist(board);
        entityManager.persist(deletedBoard);

        entityManager.flush();

        // 삭제된 게시판 생성
        deletedBoard = entityManager.find(Board.class, deletedBoard.getId());
        deletedBoard.delete();

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("특정 사용자가 작성한 특정 유형의 게시글만 조회한다")
    void findAllByMemberIdAndPostTypeAndBoardNotDeleted() {
        // given
        Post post1 = new Post(
                memberId, board, "게시글 1", "내용 1", PostType.POST
        );

        Post post2 = new Post(
                memberId, board, "게시글 2", "내용 2", PostType.POST
        );

        Post notice = new Post(
                memberId, board, "공지글", "공지 내용", PostType.NOTICE
        );

        Post otherMemberPost = new Post(
                otherMemberId, board, "다른 회원 게시글", "내용", PostType.POST
        );

        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.persist(notice);
        entityManager.persist(otherMemberPost);

        entityManager.flush();
        entityManager.clear();

        // when
        Page<Post> result = postRepository
                .findAllByMemberIdAndPostTypeAndBoardNotDeleted(
                        memberId,
                        PostType.POST,
                        PageRequest.of(0, 10)
                );

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Post::getTitle)
                .containsExactlyInAnyOrder("게시글 1", "게시글 2");

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("삭제된 게시판의 게시글은 조회하지 않는다")
    void excludePostsFromDeletedBoard() {
        // given
        Post post = new Post(
                memberId, board, "정상 게시판 게시글", "내용", PostType.POST
        );

        Post deletedBoardPost = new Post(
                memberId, deletedBoard, "삭제된 게시판 게시글", "내용", PostType.POST
        );

        entityManager.persist(post);
        entityManager.persist(deletedBoardPost);

        entityManager.flush();
        entityManager.clear();

        // when
        Page<Post> result = postRepository
                .findAllByMemberIdAndPostTypeAndBoardNotDeleted(
                        memberId,
                        PostType.POST,
                        PageRequest.of(0, 10)
                );

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(Post::getTitle)
                .containsExactly("정상 게시판 게시글");

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("페이징이 정상적으로 동작한다")
    void paging() {
        // given
        for (int i = 1; i <= 5; i++) {
            Post post = new Post(
                    memberId,
                    board,
                    "게시글 " + i,
                    "내용 " + i,
                    PostType.POST
            );

            entityManager.persist(post);
        }

        entityManager.flush();
        entityManager.clear();

        PageRequest pageable = PageRequest.of(
                0,
                2,
                Sort.by(Sort.Direction.DESC, "id")
        );

        // when
        Page<Post> result = postRepository
                .findAllByMemberIdAndPostTypeAndBoardNotDeleted(
                        memberId,
                        PostType.POST,
                        pageable
                );

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("조회 결과가 없는 경우 빈 페이지를 반환한다")
    void emptyResult() {
        // given
        Post notice = new Post(
                memberId, board, "공지글", "공지 내용", PostType.NOTICE
        );

        entityManager.persist(notice);
        entityManager.flush();
        entityManager.clear();

        // when
        Page<Post> result = postRepository
                .findAllByMemberIdAndPostTypeAndBoardNotDeleted(
                        memberId,
                        PostType.POST,
                        PageRequest.of(0, 10)
                );

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getTotalPages()).isZero();
    }
}