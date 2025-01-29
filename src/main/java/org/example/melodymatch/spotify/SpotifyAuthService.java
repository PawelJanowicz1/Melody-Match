package org.example.melodymatch.spotify;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
public class SpotifyAuthService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    private String accessToken;
    private long tokenExpirationTime = 0;

    @PostConstruct
    public void init() {
        fetchAccessToken();
    }

    private void fetchAccessToken() {
        try {
            String credentials = clientId + ":" + clientSecret;
            System.out.println("🔐 client_id i client_secret: " + credentials);
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            System.out.println("🔐 Encoded credentials (Base64): " + encodedCredentials);

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    public String getAccessToken() {
        if (accessToken == null || System.currentTimeMillis() >= tokenExpirationTime) {
            fetchAccessToken();
        }
        return accessToken;
    }
}