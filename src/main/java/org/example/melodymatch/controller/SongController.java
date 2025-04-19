package org.example.melodymatch.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.melodymatch.dto.SongDto;
import org.example.melodymatch.model.Song;
import org.example.melodymatch.service.SongService;
import org.example.melodymatch.service.SpotifyAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/user/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;
    private final SpotifyAuthService spotifyAuthService;
    private final WebClient.Builder webClientBuilder;

    @Value("${spotify.base.uri}")
    private String baseUri;

    @GetMapping
    public ResponseEntity<List<Song>> getSongs(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(songService.getSongs(token));
    }

    @GetMapping("/by-mood")
    public ResponseEntity<SongDto> saveSongByMood(@RequestParam String mood, @RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.replace("Bearer ", "");
        WebClient webClient = webClientBuilder.baseUrl(baseUri).build();

        try {
            String token = spotifyAuthService.getAccessToken().block();
            String responseBody = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", mood)
                            .queryParam("type", "track")
                            .queryParam("limit", 1)
                            .build())
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode track = mapper.readTree(responseBody)
                    .path("tracks").path("items").get(0);

            String title = track.path("name").asText();
            String artist = track.path("artists").get(0).path("name").asText();
            String album = track.path("album").path("name").asText();

            SongDto dto = new SongDto(title, artist, album);
            songService.saveSong(jwt, dto);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new SongDto("Error", e.getMessage(), ""));
        }
    }
}