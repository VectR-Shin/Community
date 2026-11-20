package io.github.vectr.community.config.security;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.exception.ProfileErrorCode;
import io.github.vectr.community.member.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class NicknameGenerator {

    private static final String PREFIX = "신규회원";
    private static final int MAX_ATTEMPTS = 10;

    private final ProfileRepository profileRepository;
    private final SecureRandom random = new SecureRandom();

    // 신규 회원을 위한 랜덤 닉네임 생성
    // 중복시 최대 10회까지 생성
    public String generate() {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            String nickname = PREFIX + random.nextInt(1_000_000_000);

            if (!profileRepository.existsByNickname(nickname)) {
                return nickname;
            }
        }

        throw new BusinessException(ProfileErrorCode.PROFILE_GENERATION_FAILED);
    }
}
