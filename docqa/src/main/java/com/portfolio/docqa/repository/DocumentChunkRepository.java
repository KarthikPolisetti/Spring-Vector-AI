package com.portfolio.docqa.repository;

import com.portfolio.docqa.entity.DocumentChunk;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk,Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE document_chunks SET  embedding=CAST(:embedding AS vector) WHERE id=:id",nativeQuery = true)
    void updateEmbedding(@Param("id") Long id,@Param("embedding") String embedding);

    @Query(value = """
        SELECT chunk_text 
        FROM document_chunks 
        WHERE document_id = :documentId 
        ORDER BY embedding <=> CAST(:embedding AS vector) 
        LIMIT :topK
        """, nativeQuery = true)
    List<String> findSimilarChunks(
            @Param("documentId") Long documentId,
            @Param("embedding") String embedding,
            @Param("topK") int topK
    );
}


