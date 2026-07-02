[POSTMAN] -> Sends POST request to: localhost:8080/api/documents/upload
    │
    ▼
📂 DocumentController.java  (Your REST API Gateway)
    │ 
    ├─ Method: uploadDocument(@RequestParam MultipartFile file)
    │  └─ Triggers ───> documentService.processUpload(file);
    │
    ▼
📂 DocumentService.java  (The Core Brain)
    │
    ├─ Method: processUpload(MultipartFile file)
    │  │
    │  ├─ 1. Validation Logic (Checks file size and type)
    │  │
    │  ├─ 2. Initial Database Save
    │  │  └─ Triggers ───> documentRepository.save(doc);
    │  │         │
    │  │         └─> 📂 DocumentRepository.java (Interface)
    │  │               └─ (Hibernate executes INSERT INTO documents)
    │  │
    │  ├─ 3. PDF Extraction (using Apache PDFBox library)
    │  │
    │  ├─ 4. Text Chunking
    │  │  └─ Triggers ───> chunkingService.chunkText(extractedText);
    │  │         │
    │  │         ▼
    │  │     📂 ChunkingService.java
    │  │         ├─ Method: chunkText(String text)
    │  │         │  └─ Applies 500-word / 50-word overlap logic
    │  │         └─ Returns ───> List<String> back to DocumentService
    │  │         │
    │  │         ▼
    │  │     (Back inside DocumentService.java loop)
    │  │
    │  ├─ 5. Save Chunks to Database (Inside a for-loop)
    │  │  └─ Triggers ───> documentChunkRepository.save(chunk);
    │  │         │
    │  │         └─> 📂 DocumentChunkRepository.java (Interface)
    │  │               └─ (Hibernate executes INSERT INTO document_chunks)
    │  │
    │  ├─ 6. Final Database Update
    │  │  └─ Triggers ───> documentRepository.save(doc);
    │  │         │
    │  │         └─> 📂 DocumentRepository.java (Interface)
    │  │               └─ (Hibernate executes UPDATE documents SET status='CHUNKED')
    │  │
    │  └─ 7. Returns ───> doc.getId(); (Returns Long back to Controller)
    │
    ▼
📂 DocumentController.java
    │
    └─ Returns HTTP 200 OK with the Document ID back to Postman