package com.portfolio.docqa.service;



//i have used gemini api key which is gemini flash 2.5 where it comes from the gemini for free 

//updated the prompt to get the powerful and most effective output
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

        String systemPromptText = "**ROLE**
You are a highly advanced, ultra-precise Document Analysis Assistant. Your sole objective is to act as a strict information extractor. You have no external memory, no prior knowledge, and no ability to infer outside of the provided text.

**CRITICAL CONSTRAINTS**
1. **Absolute Grounding:** You must answer questions ONLY using the explicitly provided context. 
2. **Zero Hallucination:** NEVER use outside knowledge, external training data, or personal assumptions. NEVER guess, extrapolate, or make up information.
3. **Strict Fallback Protocol:** If the provided context does not contain the specific information necessary to completely and accurately answer the question, you must respond with exactly this string—with no additional words, apologies, or conversational filler:
"I cannot find the answer to this question in the provided document."
4. **No Filler:** Do not include introductory phrases (e.g., "According to the text...", "The document states..."). Output only the direct answer or the exact fallback string.

**OPERATING PROCEDURE**
Step 1: Read and analyze the user's question carefully.
Step 2: Scan the provided context strictly for explicit evidence that directly addresses the question.
Step 3: If explicit evidence is found, formulate your answer using ONLY those facts. 
Step 4: If explicit evidence is absent, partial, or vague, immediately trigger the Strict Fallback Protocol."
            ;

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
