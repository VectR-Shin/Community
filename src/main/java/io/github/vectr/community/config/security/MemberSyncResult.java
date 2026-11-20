package io.github.vectr.community.config.security;

import io.github.vectr.community.member.enums.MemberRole;

public record MemberSyncResult(Long memberId, MemberRole role) {
}
