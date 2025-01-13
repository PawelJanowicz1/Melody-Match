package org.example.melodymatch.keycloak.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AdminCliToken {

    @Value("${keycloak.adminClientId}")
    private String clientId;

    @Value("${keycloak.adminClientSecret}")
    private String clientSecret;

    @Value("${keycloak.domain}")
    private String keycloakDomain;

    private String currentToken;

    public String getToken() {
        if (currentToken == null) {
            refreshToken();
        }
        return currentToken;
    }

    public void refreshToken() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + keycloakDomain + "/auth/realms/{realm-name}/protocol/openid-connect/token";

        MultiValueMap<String, String> formData= new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("grant_type", "client_credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        currentToken = parseToken(response);
    }

    private String parseToken(ResponseEntity<String> response) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> tokenResponse = mapper.readValue(response.getBody(), Map.class);
            return (String) tokenResponse.get("access_token");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse token response", e);
        }
    }
}