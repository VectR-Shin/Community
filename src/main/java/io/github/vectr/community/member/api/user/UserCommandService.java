package io.github.vectr.community.member.api.user;

import io.github.vectr.community.member.enums.MemberProvider;

public interface UserCommandService {

    // 현재 사용자 탈퇴 처리
    void withdrawUser(Long memberId, MemberProvider provider, String providerSub);
}
