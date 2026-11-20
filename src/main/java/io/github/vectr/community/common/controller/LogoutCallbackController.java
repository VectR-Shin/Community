package io.github.vectr.community.common.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Set;

@RestController
public class LogoutCallbackController {

    @GetMapping("/logout/callback/{errorCode}")
    public void logoutCallback(@PathVariable String errorCode, HttpServletResponse response) throws IOException {

        Set<String> allowedErrorCodes = Set.of(
                "LOGIN_FAILED",
                "INVALID_MEMBER_INFO",
                "INVALID_ROLE",
                "PROFILE_GENERATION_FAILED",
                "REJOIN_RESTRICTED"
        );

        if (!allowedErrorCodes.contains(errorCode)) {
            errorCode = "LOGIN_FAILED";
        }

        response.sendRedirect("/?error=" + errorCode);
    }
}