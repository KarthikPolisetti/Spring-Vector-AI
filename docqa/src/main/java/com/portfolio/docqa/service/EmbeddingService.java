package com.portfolio.docqa.service;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate=new RestTemplate();

    public float[] generateEmbedding(String text){
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-embedding-001:embedContent?key=" + apiKey;

        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(Map.of("text", text)));

        Map<String,Object> body=new HashMap<>();

        body.put("content",content);
        body.put("outputDimensionality",768);

        HttpEntity<Map<String,Object>> request=new HttpEntity<>(body,headers);

        try {
            // Call the Gemini API
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            // Extract the array of numbers from the JSON response
            Map<String, Object> embeddingNode = (Map<String, Object>) responseBody.get("embedding");
            List<Double> values = (List<Double>) embeddingNode.get("values");

            // Convert to float[]
            float[] result = new float[values.size()];
            for (int i = 0; i < values.size(); i++) {
                result[i] = values.get(i).floatValue();
            }
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate embedding from Gemini API: " + e.getMessage());
        }

    }


}
