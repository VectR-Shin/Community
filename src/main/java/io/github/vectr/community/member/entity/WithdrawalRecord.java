
package io.github.vectr.community.member.entity;

import io.github.vectr.community.common.entity.BaseCreatedAtEntity;
import io.github.vectr.community.member.enums.MemberProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Getter
@Entity
@Table(name = "withdrawal_record")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WithdrawalRecord extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email_hash", nullable = false, length = 255)
    private String emailHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MemberProvider provider;

    @Column(name = "provider_sub_hash", nullable = false, length = 255)
    private String providerSubHash;

    @Column(name = "blocked_until", nullable = false)
    private Instant blockedUntil;

    public WithdrawalRecord(
            String emailHash,
            MemberProvider provider,
            String providerSubHash,
            Instant blockedUntil
    ) {
        this.emailHash = Objects.requireNonNull(emailHash);
        this.provider = Objects.requireNonNull(provider);
        this.providerSubHash = Objects.requireNonNull(providerSubHash);
        this.blockedUntil = Objects.requireNonNull(blockedUntil);
    }

    public boolean isRestrictionActive(Instant now) {
        return blockedUntil.isAfter(now);
    }
}
