package com.example.webhook.service;

import com.example.webhook.model.WebhookResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String GENERATE_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String SUBMIT_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    @PostConstruct
    public void runOnStartup() {
        try {
            // Step 1: Send POST to generate webhook
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", "John Doe");
            requestBody.put("regNo", "REG12347"); // change as per your reg no
            requestBody.put("email", "john@example.com");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                    GENERATE_URL, HttpMethod.POST, entity, WebhookResponse.class);

            WebhookResponse webhookResponse = response.getBody();

            if (webhookResponse == null) {
                System.out.println("❌ Webhook response null!");
                return;
            }

            String webhookUrl = webhookResponse.getWebhook();
            String token = webhookResponse.getAccessToken();

            System.out.println("✅ Webhook URL: " + webhookUrl);
            System.out.println("✅ Token: " + token);

            // Step 2: Based on regNo last two digits, pick SQL solution manually
            // Example dummy SQL query (replace with real solution)
            String finalQuery = "SELECT * FROM employees;";

            // Step 3: Submit SQL query with JWT Authorization
            Map<String, String> answerBody = new HashMap<>();
            answerBody.put("finalQuery", finalQuery);

            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setContentType(MediaType.APPLICATION_JSON);
            authHeaders.setBearerAuth(token);

            HttpEntity<Map<String, String>> answerEntity = new HttpEntity<>(answerBody, authHeaders);

            ResponseEntity<String> submitResponse = restTemplate.exchange(
                    SUBMIT_URL, HttpMethod.POST, answerEntity, String.class);

            System.out.println("📤 Submission response: " + submitResponse.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
