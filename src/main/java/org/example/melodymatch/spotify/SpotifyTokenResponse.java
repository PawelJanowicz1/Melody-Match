package org.example.melodymatch.spotify;

import lombok.Data;

@Data
public class SpotifyTokenResponse {
    private String access_token;
    private String token_type;
    private int expiresIn;

    public String getAccessToken() {
        return access_token;
    }
}