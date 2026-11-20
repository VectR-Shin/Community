package io.github.vectr.community.member.entity;

import io.github.vectr.community.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Entity
@Table(
        name = "profile",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_profile_member_id",
                        columnNames = "member_id"
                ),
                @UniqueConstraint(
                        name = "uq_profile_nickname",
                        columnNames = "nickname"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_profile_member")
    )
    private Member member;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(name = "is_public", nullable = false)
    private boolean isPublicProfile;

    public Profile(
            Member member,
            String nickname
    ) {
        this.member = Objects.requireNonNull(member);
        this.nickname = Objects.requireNonNull(nickname);
        this.isPublicProfile = true;
    }

    public void updateNickname(String nickname) {
        this.nickname = Objects.requireNonNull(nickname);
    }

    public void updatePublic(boolean isPublicProfile) {
        this.isPublicProfile = isPublicProfile;
    }
}