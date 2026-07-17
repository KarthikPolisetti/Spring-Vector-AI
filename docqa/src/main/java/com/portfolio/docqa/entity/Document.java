package com.portfolio.docqa.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private  String filename;

    @Column(name="file_size")
    private Long fileSize;

    @Column(name = "total_pages")
    private  Integer totalPages;

    @Column(name = "total_chunks")
    private Integer totalChunks;

    @Column(length = 50)
    private  String status="PROCESSING";


    @Column(name="uploaded_at", insertable = false,updatable = false)
    private LocalDateTime uploadedAt;
}
