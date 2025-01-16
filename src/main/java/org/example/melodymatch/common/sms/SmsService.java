package org.example.melodymatch.common.sms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SmsService {

    @Value("${serwersms.username}")
    private String username;

    @Value("${serwersms.password}")
    private String password;

    @Value("${serwersms.token}")
    private String token;

    @Value("${serwersms.sender}")
    private String sender;

    @Value("${serwersms.url}")
    private String url;

    @Value("${serwersms.active}")
    private Boolean active;

    public ResponseEntity<String> sendSms(String phoneNumber, String message) {
        final var headers = createHeaders();
        final var requestBody = createRequestData(phoneNumber, message);
        final var httpEntity = new HttpEntity<>(requestBody, headers);

        try {
            if (active) {
                final var response = sendRequest(httpEntity);
                return handleResponse(response);
            }
            else {
                System.out.println(message);
            }
        } catch (Exception e) {
            System.out.println("Error sending SMS: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending SMS: " + e.getMessage());
        }
        return null;
    }

    public ResponseEntity<String> sendSmsDTO(String phoneNumber, String message) {
        final var headers = createHeaders();
        final var requestBody = createRequestData(phoneNumber, message);
        final var httpEntity = new HttpEntity<>(requestBody, headers);

        try {
            if (active) {
                final var response = sendRequest(httpEntity);
                return handleResponse(response);
            }
            else {
                System.out.println(message);
            }
        } catch (Exception e) {
            System.out.println("Error sending SMS: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending SMS: " + e.getMessage());
        }
        return null;
    }

    private HttpHeaders createHeaders() {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }

    private Map<String, String> createRequestData(String phoneNumber, String message) {
        final var requestBody = new HashMap<String, String>();
        requestBody.put("username", username);
        requestBody.put("password", password);
        requestBody.put("phone", phoneNumber);
        requestBody.put("text", message);
        requestBody.put("sender", sender);
        return requestBody;
    }

    private ResponseEntity<String> sendRequest(HttpEntity<Map<String, String>> entity) {
        final var restTemplate = new RestTemplate();
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    private ResponseEntity<String> handleResponse(ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            final var objectMapper = new ObjectMapper();
            try {
                final var jsonResponse = objectMapper.readTree(response.getBody());
                final var successNode = jsonResponse.get("success");
                if (successNode != null && successNode.asBoolean()) {
                    return ResponseEntity.ok().body("SMS sent successfully.");
                } else {
                    return ResponseEntity.badRequest().body("Failed to send SMS: " + jsonResponse);
                }
            } catch (JsonProcessingException e) {
                return ResponseEntity.badRequest().body("Failed to parse response: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(response.getStatusCode())
                .body("Failed to send SMS due to HTTP status code: " + response.getStatusCode());
        }
    }

}
