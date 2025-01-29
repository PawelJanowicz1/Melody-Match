package org.example.melodymatch.spotify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@RestController
public class SpotifyAuthController {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    private String accessToken;

    @GetMapping("/login")
    public String login() {
        String authUrl = "https://accounts.spotify.com/authorize" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&scope=user-read-private user-read-email";
        return "Go to: <a href=\"" + authUrl + "\">Log in with Spotify</a>";
    }

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code) {
        String tokenUrl = "https://accounts.spotify.com/api/token";
        RestTemplate restTemplate = new RestTemplate();

        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedCredentials);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<SpotifyTokenResponse> response = restTemplate.postForEntity(tokenUrl, request, SpotifyTokenResponse.class);

        if (response.getBody() != null) {
            accessToken = response.getBody().getAccessToken();
            return "Access token received: " + accessToken;
        } else {
            return "Error retrieving access token!";
        }
    }
    public String getAccessToken() {
        return accessToken;
    }
}