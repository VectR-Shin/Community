package io.github.vectr.community.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;

@Component
public class HmacService {

    private static final String ALGORITHM = "HmacSHA256";
    private static final String PREFIX = "hmac-sha256:v1:";

    private final SecretKeySpec secretKey;

    public HmacService(
            @Value("${community.security.hmac.key}") String secret
    ) {
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                ALGORITHM
        );
    }

    public String hash(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("해싱할 값이 비어 있습니다.");
        }

        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(secretKey);

            byte[] hash = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));

            return PREFIX + HexFormat.of().formatHex(hash);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HMAC 해싱에 실패했습니다.", e);
        }
    }
}
