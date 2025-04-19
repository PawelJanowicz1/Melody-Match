package org.example.melodymatch.service;

import jakarta.annotation.PostConstruct;
import org.example.melodymatch.spotify.SpotifyTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Base64;

@Service
public class SpotifyAuthService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    private String accessToken;
    private long tokenExpirationTime = 0;

    private final WebClient webClient;

    public SpotifyAuthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://accounts.spotify.com")
                .build();
    }

    @PostConstruct
    public void init() {
        // Prefetch token przy starcie
        getAccessToken().subscribe();
    }

    private Mono<SpotifyTokenResponse> fetchAccessToken() {
        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        return webClient.post()
                .uri("/api/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(SpotifyTokenResponse.class);
    }

    public Mono<String> getAccessToken() {
        if (accessToken == null || System.currentTimeMillis() >= tokenExpirationTime) {
            return fetchAccessToken()
                    .doOnNext(resp -> {
                        accessToken = resp.getAccessToken();
                        tokenExpirationTime = System.currentTimeMillis() + resp.getExpiresIn() * 1000;
                    })
                    .map(SpotifyTokenResponse::getAccessToken);
        }
        return Mono.just(accessToken);
    }
}