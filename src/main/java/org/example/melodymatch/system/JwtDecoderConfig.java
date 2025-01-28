package org.example.melodymatch.system;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtDecoderConfig {
  private final String secretKey = "8Zz5tw0Ionm3XPZZfN0NOml3z9FMfmpg";

    @Bean
    public NimbusJwtDecoder jwtDecoder() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}