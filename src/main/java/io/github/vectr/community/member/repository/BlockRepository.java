package io.github.vectr.community.member.repository;

import io.github.vectr.community.member.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {
    // 회원 식별자를 기반으로 차단 관계 전체 삭제 (차단한 사용자 또는 차단당한 사용자)
    void deleteAllByBlocker_IdOrBlocked_Id(Long blockerId, Long blockedId);
}
