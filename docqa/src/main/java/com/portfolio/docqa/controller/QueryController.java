package com.portfolio.docqa.controller;

import com.portfolio.docqa.service.SimilaritySearchService;
import com.portfolio.docqa.service.LlmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class QueryController {

    @Autowired
    private SimilaritySearchService similaritySearchService;

    @Autowired
    private LlmService llmService;

    @PostMapping("/{documentId}/query")
    public Map<String, Object> queryDocument(@PathVariable Long documentId, @RequestBody Map<String, String> request) {
        String originalQuestion = request.get("question");
        System.out.println("--- NEW QUERY REQUEST ---");
        System.out.println("1. User asked: " + originalQuestion);

        // Stop 1: It goes to expandQuery
        String expandedKeywords = llmService.expandQuery(originalQuestion);
        System.out.println("2. AI Expanded Keywords: " + expandedKeywords);




        // 2. Retrieve the top 5 most relevant chunks (The 'R' in RAG)
        List<String> relevantChunks = similaritySearchService.findSimilarityChunks(documentId, expandedKeywords, 5);
        System.out.println("3. Retrieved " + relevantChunks.size() + " chunks from pgvector.");


        // Stop 2: It goes to generateAnswer and Generate the answer using the AI (The 'A' and 'G' in RAG)
        String aiAnswer = llmService.generateAnswer(originalQuestion, relevantChunks);

        // 3. Build the response payload
        Map<String, Object> response = new HashMap<>();
        response.put("question", originalQuestion);
        response.put("answer", aiAnswer);
        response.put("sourceChunks", relevantChunks); // Returning sources is NOT optional!
        response.put("documentId", documentId);

        return response;
    }
}