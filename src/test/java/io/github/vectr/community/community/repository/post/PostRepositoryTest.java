
package io.github.vectr.community.community.repository.post;

import io.github.vectr.community.common.config.JpaAuditingTestConfig;
import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.board.BoardCategory;
import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.enums.PostType;
import io.github.vectr.community.member.entity.Member;
import io.github.vectr.community.member.enums.MemberProvider;
import io.github.vectr.community.member.enums.MemberRole;
import io.github.vectr.community.member.repository.MemberRepository;
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
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingTestConfig.class)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager entityManager;

    private Board normalBoard;
    private Board deletedBoard;

    private Long memberId;
    private Long otherMemberId;

    @BeforeEach
    void setUp() {
        // Member 생성
        Member member = new Member(
                "test-keycloak-sub",
                "test@example.com",
                MemberRole.USER
        );

        Member otherMember = new Member(
                "other-keycloak-sub",
                "other@example.com",
                MemberRole.USER
        );

        // Member 저장 후 실제 ID 할당
        memberId = memberRepository.save(member).getId();
        otherMemberId = memberRepository.save(otherMember).getId();

        // BoardCategory 생성
        BoardCategory category = new BoardCategory("일반");

        entityManager.persist(category);

        normalBoard = new Board(
                category,
                "자유게시판",
                "자유롭게 이야기를 나누는 게시판"
        );

        deletedBoard = new Board(
                category,
                "삭제된 게시판",
                "삭제된 게시판"
        );

        entityManager.persist(normalBoard);
        entityManager.persist(deletedBoard);

        entityManager.flush();
        entityManager.clear();
    }

    // 회원이 작성한 게시글 조회
    @Test
    @DisplayName("회원이 작성한 게시글만 조회한다.")
    void findAllByMemberIdAndBoardNotDeleted_success() {
        // given
        Post myPost = new Post(
                memberId,
                normalBoard,
                "내 게시글",
                "게시글 내용",
                PostType.POST
        );

        Post otherPost = new Post(
                otherMemberId,
                normalBoard,
                "다른 회원 게시글",
                "게시글 내용",
                PostType.POST
        );

        entityManager.persist(myPost);
        entityManager.persist(otherPost);
        entityManager.flush();

        // when
        Page<Post> result =
                postRepository.findAllByMemberIdAndBoardNotDeleted(
                        memberId,
                        PageRequest.of(0, 10)
                );

        // then
        assertThat(result.getContent())
                .hasSize(1)
                .extracting(Post::getTitle)
                .containsExactly("내 게시글");

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("삭제된 게시판에 속한 게시글은 조회하지 않는다.")
    void findAllByMemberIdAndBoardNotDeleted_excludeDeletedBoard() {
        // given
        Post normalPost = new Post(
                memberId,
                normalBoard,
                "정상 게시판 게시글",
                "게시글 내용",
                PostType.POST
        );

        Post deletedBoardPost = new Post(
                memberId,
                deletedBoard,
                "삭제된 게시판 게시글",
                "게시글 내용",
                PostType.POST
        );

        entityManager.persist(normalPost);
        entityManager.persist(deletedBoardPost);

        // 삭제된 게시판을 영속 상태로 다시 조회
        Board boardToDelete = entityManager.find(Board.class, deletedBoard.getId());
        boardToDelete.delete();

        entityManager.flush();
        entityManager.clear();

        // when
        Page<Post> result =
                postRepository.findAllByMemberIdAndBoardNotDeleted(
                        memberId,
                        PageRequest.of(0, 10)
                );

        // then
        assertThat(result.getContent())
                .hasSize(1)
                .extracting(Post::getTitle)
                .containsExactly("정상 게시판 게시글");

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    // 공지글 포함
    @Test
    @DisplayName("공지글도 게시글 목록에 포함한다.")
    void findAllByMemberIdAndBoardNotDeleted_includeNotice() {
        // given
        Post normalPost = new Post(
                memberId,
                normalBoard,
                "일반 게시글",
                "게시글 내용",
                PostType.POST
        );

        Post noticePost = new Post(
                memberId,
                normalBoard,
                "공지글",
                "공지 내용",
                PostType.NOTICE
        );

        entityManager.persist(normalPost);
        entityManager.persist(noticePost);
        entityManager.flush();

        // when
        Page<Post> result =
                postRepository.findAllByMemberIdAndBoardNotDeleted(
                        memberId,
                        PageRequest.of(0, 10)
                );

        // then
        assertThat(result.getContent())
                .hasSize(2)
                .extracting(Post::getPostType)
                .containsExactlyInAnyOrder(
                        PostType.POST,
                        PostType.NOTICE
                );

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    // 페이징
    @Test
    @DisplayName("게시글 목록을 페이지 단위로 조회한다.")
    void findAllByMemberIdAndBoardNotDeleted_paging() {
        // given
        for (int i = 1; i <= 5; i++) {
            entityManager.persist(
                    new Post(
                            memberId,
                            normalBoard,
                            "게시글 " + i,
                            "게시글 내용",
                            PostType.POST
                    )
            );
        }

        entityManager.flush();

        // when
        Page<Post> firstPage =
                postRepository.findAllByMemberIdAndBoardNotDeleted(
                        memberId,
                        PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"))
                );

        Page<Post> secondPage =
                postRepository.findAllByMemberIdAndBoardNotDeleted(
                        memberId,
                        PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "id"))
                );

        // then
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(secondPage.getContent()).hasSize(2);

        assertThat(firstPage.getTotalElements()).isEqualTo(5);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);

        assertThat(firstPage.getContent().get(0).getTitle())
                .isEqualTo("게시글 5");

        assertThat(secondPage.getContent().get(0).getTitle())
                .isEqualTo("게시글 3");
    }

    // 조회 결과 없음
    @Test
    @DisplayName("조회할 게시글이 없으면 빈 페이지를 반환한다.")
    void findAllByMemberIdAndBoardNotDeleted_empty() {
        // when
        Page<Post> result =
                postRepository.findAllByMemberIdAndBoardNotDeleted(
                        memberId,
                        PageRequest.of(0, 10)
                );

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getTotalPages()).isZero();
    }
}
