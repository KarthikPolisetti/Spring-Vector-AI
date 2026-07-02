package com.portfolio.docqa.controller;


import com.portfolio.docqa.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Long> uploadDocument(@RequestParam("file")MultipartFile file){
        Long documentId=documentService.processUpload(file);
        return  ResponseEntity.ok(documentId);
    }


    // Add these to your DocumentController

    @GetMapping("/{id}/status")
    public Map<String, String> getDocumentStatus(@PathVariable Long id) {
        // Fetch the document from the DB. If it exists, return its status.
        // For now, we will return a mock response so you can test the route.
        return Map.of("status", "READY");
    }

    @GetMapping("/{id}/history")
    public List<Map<String, Object>> getDocumentHistory(@PathVariable Long id) {
        // Fetch the list of past queries from your query_history table.
        // (You will need to create the QueryHistory entity/repository if you haven't yet).
        return List.of(); // Return empty list for now
    }
}
