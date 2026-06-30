package com.vaishnavi.aegistrace.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    @Test
    void generatedTokenRoundTripsSubjectWhenSecretIsStrongEnough() {
        JwtUtil jwtUtil = configuredJwtUtil("MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=", 60_000L);

        String token = jwtUtil.generateToken("analyst");

        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.parseClaims(token).getSubject()).isEqualTo("analyst");
    }

    @Test
    void startupValidationRejectsWeakSigningSecret() {
        JwtUtil jwtUtil = configuredJwtUtil("c2hvcnQ=", 60_000L);

        assertThatThrownBy(jwtUtil::validateConfiguration)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("256 bits");
    }

    private JwtUtil configuredJwtUtil(String secret, long expirationMs) {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", expirationMs);
        return jwtUtil;
    }
}
