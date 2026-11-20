package io.github.vectr.community.member.repository;

import io.github.vectr.community.member.entity.WithdrawalRecord;
import io.github.vectr.community.member.enums.MemberProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface WithdrawalRecordRepository extends JpaRepository<WithdrawalRecord, Long> {
    // Email_hash 가 일치하는 유효한 재가입 방지 이력 유무 조회
    boolean existsByEmailHashAndBlockedUntilAfter(String emailHash, Instant now);

    // Provider, Provider_sub_hash 가 일치하는 유효한 재가입 방지 이력 유무 조회
    boolean existsByProviderAndProviderSubHashAndBlockedUntilAfter(MemberProvider provider, String providerSubHash, Instant now);

    // 현재 시점을 기준으로 expire 된 withdrawal_record 기록 제거
    @Modifying(clearAutomatically = true)
    @Query("""
        DELETE FROM WithdrawalRecord wr
        WHERE wr.blockedUntil <= :now
    """)
    int deleteExpiredRecords(@Param("now") Instant now);
}
