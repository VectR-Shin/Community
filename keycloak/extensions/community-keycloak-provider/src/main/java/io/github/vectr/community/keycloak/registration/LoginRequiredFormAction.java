
package io.github.vectr.community.keycloak.registration;

import jakarta.ws.rs.core.MultivaluedMap;

import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.forms.login.LoginFormsProvider;

import java.util.List;
import java.util.Locale;

public class LoginRequiredFormAction implements FormAction {

    private static final String EMAIL_REQUIRED_MESSAGE =
            "communityEmailRequired";

    @Override
    public void buildPage(
            FormContext context,
            LoginFormsProvider form
    ) {
        // 별도 화면 구성은 하지 않는다.
    }

    @Override
    public void validate(ValidationContext context) {
        context.success();
    }

    @Override
    public void success(FormContext context) {

        UserModel user = context.getUser();

        if (user == null) {
            return;
        }

        String email = user.getEmail();

        if (email == null || email.isBlank()) {
            throw new IllegalStateException(
                    "이메일이 유효하지 않습니다."
            );
        }

        String normalizedEmail = email
                .trim()
                .toLowerCase(Locale.ROOT);

        user.setSingleAttribute("provider", "LOCAL");
        user.setSingleAttribute("provider_sub", normalizedEmail);
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(
            KeycloakSession session,
            RealmModel realm,
            UserModel user
    ) {
        return false;
    }

    @Override
    public void setRequiredActions(
            KeycloakSession session,
            RealmModel realm,
            UserModel user
    ) {
        // 별도 Required Action은 없다.
    }

    @Override
    public void close() {
    }
}
