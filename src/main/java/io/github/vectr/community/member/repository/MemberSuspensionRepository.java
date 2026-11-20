package io.github.vectr.community.member.repository;

import io.github.vectr.community.member.dto.suspension.SuspensionResponseDTO;
import io.github.vectr.community.member.entity.MemberSuspension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface MemberSuspensionRepository extends JpaRepository<MemberSuspension, Long> {
    // 특정 사용자의 현재 적용 중인 전역 밴 이력 조회
    @Query("""
            SELECT new io.github.vectr.community.member.dto.suspension.SuspensionResponseDTO(
                ms.id,
                ms.reason,
                ms.createdAt,
                ms.endsAt
            )
            FROM MemberSuspension ms
            WHERE ms.member.id = :memberId
              AND ms.createdAt <= :now
              AND ms.liftedAt IS NULL
              AND (ms.endsAt IS NULL OR ms.endsAt > :now)
            """)
    Optional<SuspensionResponseDTO> findCurrentSuspension(
            @Param("memberId") Long memberId,
            @Param("now") Instant now
    );

    // 특정 사용자의 현재 전역 밴 적용 여부 확인
    @Query("""
        SELECT CASE WHEN COUNT(ms) > 0 THEN true ELSE false END
        FROM MemberSuspension ms
        WHERE ms.member.id = :memberId
          AND ms.createdAt <= :now
          AND ms.liftedAt IS NULL
          AND (ms.endsAt IS NULL OR ms.endsAt > :now)
        """)
    boolean existsCurrentSuspension(
            @Param("memberId") Long memberId,
            @Param("now") Instant now
    );
}
