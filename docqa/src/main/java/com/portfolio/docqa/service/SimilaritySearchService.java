package com.portfolio.docqa.service;


import com.portfolio.docqa.repository.DocumentChunkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SimilaritySearchService {

    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    @Autowired
    private  EmbeddingService embeddingService;

    public List<String> findSimilarityChunks(Long documentId,String question,int topk){
        float[] questionEmbedding=embeddingService.generateEmbedding(question);

        String embeddingString= Arrays.toString(questionEmbedding);

        return documentChunkRepository.findSimilarChunks(documentId,embeddingString,topk);

    }
}
