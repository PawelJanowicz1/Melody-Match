package org.example.melodymatch.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/music")
public class MusicController {

    @Autowired
    private SpotifyAuthService spotifyAuthService;

    @GetMapping("/mood")
    public String findMusicByMood(@RequestParam String mood) {
        String token = spotifyAuthService.getAccessToken();

        String searchUrl = "https://api.spotify.com/v1/search?q=" + mood + "&type=track&limit=5";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }
}