package org.example.melodymatch.spotify;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
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
            String authUrl = "https://accounts.spotify.com/api/token";
            RestTemplate restTemplate = new RestTemplate();

            String credentials = clientId + ":" + clientSecret;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedCredentials);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<SpotifyTokenResponse> response = restTemplate.postForEntity(authUrl, request, SpotifyTokenResponse.class);

            if (response.getBody() != null) {
                accessToken = response.getBody().getAccessToken();
                tokenExpirationTime = System.currentTimeMillis() + (response.getBody().getExpiresIn() * 1000);
            }
        } catch (Exception e) {
            System.err.println("❌ Error fetching access token: " + e.getMessage());
        }
    }

    public String getAccessToken() {
        if (accessToken == null || System.currentTimeMillis() >= tokenExpirationTime) {
            fetchAccessToken();
        }
        return accessToken;
    }
}