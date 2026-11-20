
package io.github.vectr.community.member.entity;

import io.github.vectr.community.common.entity.BaseCreatedAtEntity;
import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.enums.MemberRole;
import io.github.vectr.community.member.exception.MemberErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Getter
@Entity
@Table(
        name = "member",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_member_keycloak_sub",
                        columnNames = "keycloak_sub"
                ),
                @UniqueConstraint(
                        name = "uq_member_email",
                        columnNames = "email"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_sub", length = 255)
    private String keycloakSub;

    @Column(length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Member(
            String keycloakSub,
            String email,
            MemberRole role
    ) {
        this.keycloakSub = Objects.requireNonNull(keycloakSub);
        this.email = Objects.requireNonNull(email);
        this.role = Objects.requireNonNull(role);
    }

    public void withdraw() {
        if (isDeleted()) {
            throw new BusinessException(MemberErrorCode.MEMBER_ALREADY_WITHDRAWN);
        }

        this.deletedAt = Instant.now();
        this.keycloakSub = null;
        this.email = null;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void updateRole(MemberRole role) {
        this.role = Objects.requireNonNull(role);
    }
}
