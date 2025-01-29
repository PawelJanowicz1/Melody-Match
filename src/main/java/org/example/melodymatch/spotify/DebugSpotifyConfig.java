package org.example.melodymatch.spotify;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DebugSpotifyConfig {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @PostConstruct
    public void printConfig() {
        System.out.println("✅ SPOTIFY_CLIENT_ID: " + clientId);
        System.out.println("✅ SPOTIFY_CLIENT_SECRET: " + clientSecret);
    }
}
