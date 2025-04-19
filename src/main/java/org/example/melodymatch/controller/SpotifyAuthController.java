package org.example.melodymatch.controller;

import org.example.melodymatch.spotify.SpotifyTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
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

    private final WebClient webClient;

    public SpotifyAuthController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

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
    public Mono<String> callback(@RequestParam("code") String code) {
        String tokenUrl = "https://accounts.spotify.com/api/token";
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        return webClient.post()
                .uri(tokenUrl)
                .header("Authorization", "Basic " + encodedCredentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("code", code)
                        .with("redirect_uri", redirectUri))
                .retrieve()
                .bodyToMono(SpotifyTokenResponse.class)
                .map(response -> {
                    accessToken = response.getAccessToken();
                    return "Access token received: " + accessToken;
                })
                .onErrorResume(e -> Mono.just("Error retrieving access token: " + e.getMessage()));
    }
}