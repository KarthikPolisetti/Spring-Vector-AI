package com.portfolio.docqa.controller;


import com.portfolio.docqa.service.SimilaritySearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

//Here this controller is responsible for the requests coming from the /api/debug
@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private SimilaritySearchService similaritySearchService;

    @PostMapping("/search")

    public List<String> debugSearch(@RequestBody Map<String, Object> request){
        Long documentId=((Number) request.get("documentId")).longValue();
        String question=((String) request.get("question"));

        return similaritySearchService.findSimilarityChunks(documentId,question,5);
    }

}
