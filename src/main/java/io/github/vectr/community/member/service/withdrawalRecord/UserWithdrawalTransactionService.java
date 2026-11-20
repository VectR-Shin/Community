package io.github.vectr.community.member.service.withdrawalRecord;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.config.security.HmacService;
import io.github.vectr.community.community.api.board.BoardFavoriteCommandService;
import io.github.vectr.community.community.api.board.BoardManagerCommandService;
import io.github.vectr.community.member.config.MemberPolicyProperties;
import io.github.vectr.community.member.entity.Member;
import io.github.vectr.community.member.entity.WithdrawalRecord;
import io.github.vectr.community.member.enums.MemberProvider;
import io.github.vectr.community.member.exception.MemberErrorCode;
import io.github.vectr.community.member.repository.BlockRepository;
import io.github.vectr.community.member.repository.MemberRepository;
import io.github.vectr.community.member.repository.ProfileRepository;
import io.github.vectr.community.member.repository.WithdrawalRecordRepository;
import io.github.vectr.community.notification.api.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserWithdrawalTransactionService {

    private final MemberRepository memberRepository;
    private final WithdrawalRecordRepository withdrawalRecordRepository;
    private final ProfileRepository profileRepository;
    private final BlockRepository blockRepository;

    private final BoardManagerCommandService boardManagerCommandService;
    private final BoardFavoriteCommandService boardFavoriteCommandService;
    private final NotificationCommandService notificationCommandService;
    private final HmacService hmacService;

    private final MemberPolicyProperties memberPolicyProperties;

    @Transactional
    public String withdraw(Long memberId, MemberProvider provider, String providerSub) {
        // 1. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        // 1.1. 존재하지 않는 회원 처리
                        new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND)
                );

        // 1.2. 이미 탈퇴한 회원 처리
        if (member.isDeleted()) {
            throw new BusinessException(MemberErrorCode.MEMBER_ALREADY_WITHDRAWN);
        }

        // 1.3. keycloakSub 조회
        String keycloakSub = member.getKeycloakSub();

        // 2. 재가입 제한 기록 생성
        String normalizedEmail = member.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        String emailHash = hmacService.hash("email:" + normalizedEmail);
        String providerSubHash = hmacService.hash("provider-sub:" + providerSub);

        Instant blockedUntil = Instant.now()
                .plus(memberPolicyProperties.withdrawal()
                        .rejoinRestrictionPeriod());

        WithdrawalRecord withdrawalRecord = new WithdrawalRecord(
                emailHash,
                provider,
                providerSubHash,
                blockedUntil
        );

        withdrawalRecordRepository.save(withdrawalRecord);

        // 3. Member 정보 soft delete
        member.withdraw();

        // 4. 관련 정보 삭제
        // 4.1. BoardManager 정보 삭제
        boardManagerCommandService.deleteAllBoardManagerByMemberId(memberId);

        // 4.2. BoardFavorite 정보 삭제
        boardFavoriteCommandService.deleteAllBoardFavoriteByMemberId(memberId);

        // 4.3. Block 정보 삭제(지정자, 대상자인 모든 경우에 대해)
        blockRepository.deleteAllByBlocker_IdOrBlocked_Id(memberId, memberId);

        // 4.4. Notification 삭제
        notificationCommandService.deleteAllNotificationByReceiverId(memberId);

        // 4.5. Profile 삭제
        profileRepository.deleteByMember_Id(memberId);

        // 5. keycloakSub 반환
        return keycloakSub;
    }
}
