package com.portfolio.docqa.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_chunks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id",nullable = false)
    private Document document;

    @Column(name = "chunk_index",nullable = false)
    private Integer chunkIndex;


    @Column(name = "chunk_text",nullable = false,columnDefinition = "TEXT")
    private  String chunkText;


    @Column(name = "created_at",insertable = false,updatable = false)
    private LocalDateTime createdAt;




}
