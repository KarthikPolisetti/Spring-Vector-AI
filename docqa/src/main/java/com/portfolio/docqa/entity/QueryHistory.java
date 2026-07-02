package com.portfolio.docqa.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "query_history")
@Data
@NoArgsConstructor
public class QueryHistory {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id",nullable = false)
    private  Document document;

    @Column(nullable = false ,columnDefinition = "TEXT")
    private  String question;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String answer;

    @CreationTimestamp
    @Column(name="asked_at",updatable = false)
    private LocalDateTime askedAt;
}
