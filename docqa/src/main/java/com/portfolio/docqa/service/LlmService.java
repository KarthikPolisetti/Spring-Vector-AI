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
public class LlmService {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate=new RestTemplate();

    public  String generateAnswer(String question, List<String> contextChunks){
        String context=String.join("\n\n---\n\n",contextChunks);

        String systemPromptText = """
            You are a precise document assistant.
            Answer questions ONLY using the provided context.
            If the answer is not present in the context, respond with exactly:
            "I cannot find the answer to this question in the provided document."
            Never use outside knowledge. Never guess. Never make up information.
            """;

        String userPromptText="Context:\n"+context +"\n\nQuestion: "+question;

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=" + apiKey;

        HttpHeaders headers=new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String,Object> requestBody=new HashMap<>();

        requestBody.put("systemInstruction", Map.of("parts", List.of(Map.of("text", systemPromptText))));

        // 2. User Prompt (Context + Question)
        requestBody.put("contents", List.of(Map.of("parts", List.of(Map.of("text", userPromptText)))));


        requestBody.put("generationConfig", Map.of(
                "temperature", 0.1,
                "maxOutputTokens", 1024
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);


        try{
            ResponseEntity<Map> response=restTemplate.postForEntity(url,request,Map.class);

            Map<String,Object> responseBody=response.getBody();
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            return (String) parts.get(0).get("text");

        }

        catch(Exception e){
            throw new RuntimeException("Failed to generate answer from LLM:"+e.getMessage());
        }






    }

    // --- NEW: QUERY EXPANSION METHOD ---
    public String expandQuery(String originalQuestion) {
        String systemPromptText = """
            You are a search query optimization assistant for a vector database.
            Your job is to take a user's question and convert it into a simple, comma-separated list of highly specific keywords and synonyms.
            Do NOT answer the question. Do NOT use full sentences. 
            Output ONLY the keywords.
            Example Question: "What is the tone of the document?"
            Example Output: "tone, style, voice, formal, informal, emotion, author's attitude"
            """;

        String userPromptText = "Question: " + originalQuestion + "\n\nKeywords:";

        // We use flash because this needs to be lightning fast!
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("systemInstruction", Map.of("parts", List.of(Map.of("text", systemPromptText))));
        requestBody.put("contents", List.of(Map.of("parts", List.of(Map.of("text", userPromptText)))));
        requestBody.put("generationConfig", Map.of("temperature", 0.4, "maxOutputTokens", 50));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            System.err.println("Warning: Query expansion failed, falling back to original question.");
            return originalQuestion; // Fallback gracefully if API fails
        }
    }

}
