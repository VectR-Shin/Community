package io.github.vectr.community.member.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Objects;


@ConfigurationProperties(prefix = "community.member")
public record MemberPolicyProperties(
        Withdrawal withdrawal
) {

    public MemberPolicyProperties {
        Objects.requireNonNull(withdrawal, "회원 정책 설정이 필요합니다.");
    }

    public record Withdrawal(Duration rejoinRestrictionPeriod) {
        public Withdrawal {
            Objects.requireNonNull(rejoinRestrictionPeriod, "재가입 제한 기간 설정이 필요합니다.");

            if (rejoinRestrictionPeriod.isNegative()) {
                throw new IllegalArgumentException("재가입 제한 시간은 음수일 수 없습니다.");
            }
        }
    }
}
