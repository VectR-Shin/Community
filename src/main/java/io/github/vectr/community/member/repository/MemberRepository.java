package io.github.vectr.community.member.repository;

import io.github.vectr.community.member.dto.user.AuthorInfoDTO;
import io.github.vectr.community.member.entity.Member;
import io.github.vectr.community.member.enums.MemberProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // keycloakSub 값을 기반으로 사용자 조회
    Optional<Member> findByKeycloakSub(String keycloakSub);

    // 탈퇴하지 않은 사용자를 식별자로 조회
    Optional<Member> findByIdAndDeletedAtIsNull(Long id);

    // 탈퇴하지 않은 회원인지 여부 확인
    boolean existsByIdAndDeletedAtIsNull(Long memberId);

    // keycloakSub 으로 사용자 유무 조회
    boolean existsByKeycloakSub(String keycloakSub);

    // 사용자 식별자를 기반으로 리소스 작성자 정보 단건 조회
    @Query("""
            SELECT new io.github.vectr.community.member.dto.user.AuthorInfoDTO(
                m.id,
                p.nickname,
                CASE WHEN m.deletedAt IS NOT NULL THEN true ELSE false END
            )
            FROM Member m
            LEFT JOIN Profile p ON m.id = p.member.id
            WHERE m.id = :memberId
            """)
    Optional<AuthorInfoDTO> findAuthorInfoById(@Param("memberId") Long memberId);

    // 사용자 식별자를 기반으로 리소스 작정사 정보 벌크 조회
    @Query("""
        SELECT new io.github.vectr.community.member.dto.user.AuthorInfoDTO(
            m.id,
            p.nickname,
            CASE WHEN m.deletedAt IS NOT NULL THEN true ELSE false END
        )
        FROM Member m
        LEFT JOIN Profile p ON m.id = p.member.id
        WHERE m.id IN :memberIds
        """)
    List<AuthorInfoDTO> findAuthorInfosByIds(
            @Param("memberIds") Collection<Long> memberIds
    );
}
