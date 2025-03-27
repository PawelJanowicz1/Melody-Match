package org.example.melodymatch.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/music")
public class MusicController {

    private final SpotifyAuthService spotifyAuthService;
    private final WebClient webClient;

    @Autowired
    public MusicController(SpotifyAuthService spotifyAuthService, WebClient.Builder webClientBuilder) {
        this.spotifyAuthService = spotifyAuthService;
        this.webClient = webClientBuilder
                .baseUrl("https://api.spotify.com/v1")
                .build();
    }

    @GetMapping("/mood")
    public Mono<ResponseEntity<String>> findMusicByMood(@RequestParam String mood) {
        return spotifyAuthService.getAccessToken()
                .flatMap(token ->
                        webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/search")
                                        .queryParam("q", mood)
                                        .queryParam("type", "track")
                                        .queryParam("limit", 5)
                                        .build()
                                )
                                .headers(h -> h.setBearerAuth(token))
                                .retrieve()
                                .toEntity(String.class)
                )
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.internalServerError().body("Błąd: " + e.getMessage()))
                );
    }
}