package io.github.vectr.community.config.security;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.entity.Member;
import io.github.vectr.community.member.entity.Profile;
import io.github.vectr.community.member.enums.MemberProvider;
import io.github.vectr.community.member.enums.MemberRole;
import io.github.vectr.community.member.exception.MemberErrorCode;
import io.github.vectr.community.member.repository.MemberRepository;
import io.github.vectr.community.member.repository.ProfileRepository;
import io.github.vectr.community.member.repository.WithdrawalRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberSyncService {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final WithdrawalRecordRepository withdrawalRecordRepository;
    private final NicknameGenerator nicknameGenerator;
    private final HmacService hmacService;

    @Transactional
    public MemberSyncResult synchronize(
            String keycloakSub,
            String email,
            MemberRole role,
            MemberProvider provider,
            String providerSub
    ) {
        // 1. 기존 Keycloak 회원 조회
        Optional<Member> existingMember =
                memberRepository.findByKeycloakSub(keycloakSub);

        // 2. 기존 회원이 존재하는 경우
        if (existingMember.isPresent()) {
            Member member = existingMember.get();

            if (member.isDeleted()) {
                throw new BusinessException(MemberErrorCode.REJOIN_RESTRICTED);
            }

            // Keycloak Role 과 DB Role 동기화
            if (member.getRole() != role) {
                member.updateRole(role);
            }

            return new MemberSyncResult(member.getId(), member.getRole());
        }

        // 3. 재가입 방지에 사용할 식별 정보 해싱
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        String emailHash = hmacService.hash(
                "email:" + normalizedEmail
        );

        String normalizedProviderSub = provider == MemberProvider.LOCAL
                ? providerSub.trim().toLowerCase(Locale.ROOT)
                : providerSub;

        String providerSubHash = hmacService.hash(
                "provider-sub:" + normalizedProviderSub
        );

        Instant now = Instant.now();

        // 4. 이메일 기준 재가입 제한 확인
        boolean emailRestricted =
                withdrawalRecordRepository
                        .existsByEmailHashAndBlockedUntilAfter(emailHash, now);

        if (emailRestricted) {
            log.info("이메일 재가입 제한");
            throw new BusinessException(MemberErrorCode.REJOIN_RESTRICTED);
        }

        // 5. Provider 계정 기준 재가입 제한 확인
        boolean providerRestricted =
                withdrawalRecordRepository
                        .existsByProviderAndProviderSubHashAndBlockedUntilAfter(
                                provider,
                                providerSubHash,
                                now
                        );

        if (providerRestricted) {
            log.info("Provider, Provider_sub 재가입 제한");
            throw new BusinessException(MemberErrorCode.REJOIN_RESTRICTED);
        }

        // 6. 신규 닉네임 생성
        String newNickname = nicknameGenerator.generate();

        // 7. 신규 회원 생성 및 저장
        Member member = new Member(
                keycloakSub,
                email,
                role
        );

        Member savedMember = memberRepository.save(member);

        // 8. 신규 회원 프로필 생성 및 저장
        Profile profile = new Profile(savedMember, newNickname);
        profileRepository.save(profile);

        return new MemberSyncResult(
                savedMember.getId(),
                savedMember.getRole()
        );
    }

    public boolean existsByKeycloakSub(String keycloakSub) {
        return memberRepository.existsByKeycloakSub(keycloakSub);
    }
}