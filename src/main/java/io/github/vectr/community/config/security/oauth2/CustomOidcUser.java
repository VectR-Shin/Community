package io.github.vectr.community.config.security.oauth2;

import io.github.vectr.community.member.enums.MemberProvider;
import io.github.vectr.community.member.enums.MemberRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Set;

public class CustomOidcUser extends DefaultOidcUser {

    private final Long memberId;
    private final MemberProvider provider;
    private final String providerSub;

    public CustomOidcUser(
            OidcUser oidcUser,
            Long memberId,
            MemberRole role,
            MemberProvider provider,
            String providerSub
    ) {
        super(
                Set.of(new SimpleGrantedAuthority("ROLE_" + role.name())),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "sub"
        );

        this.memberId = memberId;
        this.provider = provider;
        this.providerSub = providerSub;
    }

    public Long getMemberId() {
        return memberId;
    }

    public MemberProvider getProvider() {
        return provider;
    }

    public String getProviderSub() {
        return providerSub;
    }
}