
package io.github.vectr.keycloak;

import org.keycloak.broker.provider.AbstractIdentityProviderMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityProviderMapper;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class NaverProviderSubMapper
        extends AbstractIdentityProviderMapper {

    private static final String PROVIDER_ALIAS = "naver";
    private static final String USER_ATTRIBUTE = "provider_sub";

    @Override
    public String[] getCompatibleProviders() {
        return new String[]{IdentityProviderMapper.ANY_PROVIDER};
    }

    @Override
    public String getDisplayCategory() {
        return "Attribute Importer";
    }

    @Override
    public String getDisplayType() {
        return "NAVER Provider Sub Importer";
    }

    @Override
    public String getHelpText() {
        return "Imports NAVER's external user ID into provider_sub.";
    }

    @Override
    public String getId() {
        return "naver-provider-sub-importer";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of();
    }

    /**
     * 최초 Broker 로그인 시 First Broker Login Flow 실행 전 호출.
     * First Broker Login 과정에서도 provider_sub를 사용할 수 있도록 전달한다.
     */
    @Override
    public void preprocessFederatedIdentity(
            KeycloakSession session,
            RealmModel realm,
            IdentityProviderMapperModel mapperModel,
            BrokeredIdentityContext context
    ) {
        String providerSub = getProviderSub(context);

        if (providerSub != null) {
            context.setUserAttribute(USER_ATTRIBUTE, providerSub);
        }
    }

    /**
     * 최초 Keycloak 사용자 생성 후 호출.
     */
    @Override
    public void importNewUser(
            KeycloakSession session,
            RealmModel realm,
            UserModel user,
            IdentityProviderMapperModel mapperModel,
            BrokeredIdentityContext context
    ) {
        saveProviderSub(user, context);
    }

    /**
     * 기존 사용자가 다시 로그인할 때 호출.
     */
    @Override
    public void updateBrokeredUser(
            KeycloakSession session,
            RealmModel realm,
            UserModel user,
            IdentityProviderMapperModel mapperModel,
            BrokeredIdentityContext context
    ) {
        saveProviderSub(user, context);
    }

    private void saveProviderSub(
            UserModel user,
            BrokeredIdentityContext context
    ) {
        String providerSub = getProviderSub(context);

        if (providerSub != null) {
            user.setSingleAttribute(USER_ATTRIBUTE, providerSub);
        }
    }

    private String getProviderSub(BrokeredIdentityContext context) {
        if (!PROVIDER_ALIAS.equals(context.getIdpConfig().getAlias())) {
            return null;
        }

        String providerSub = context.getId();

        if (providerSub == null || providerSub.isBlank()) {
            throw new IllegalStateException(
                    "NAVER provider ID is missing."
            );
        }

        return providerSub;
    }
}
