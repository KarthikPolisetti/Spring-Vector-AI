package com.portfolio.docqa.service;

import com.portfolio.docqa.entity.Document;
import com.portfolio.docqa.entity.DocumentChunk;
import com.portfolio.docqa.repository.DocumentChunkRepository;
import com.portfolio.docqa.repository.DocumentRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ChunkingService chunkingService;

    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    @Autowired
    private EmbeddingService embeddingService;

    public Long processUpload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File cannot be empty.");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Invalid file type. Only PDFs are supported.");
        }

        Document doc = new Document();
        doc.setFilename(file.getOriginalFilename());
        doc.setFileSize(file.getSize());
        doc.setStatus("PROCESSING");
        doc = documentRepository.save(doc);

        try {
            PDDocument pdDocument = Loader.loadPDF(file.getInputStream().readAllBytes());
            PDFTextStripper stripper = new PDFTextStripper();
            String extractedText = stripper.getText(pdDocument);
            int totalPages = pdDocument.getNumberOfPages();
            pdDocument.close();

            // 1. Check for scanned/empty PDFs FIRST before doing any chunking
            if (extractedText == null || extractedText.trim().length() < 50) {
                doc.setStatus("FAILED");
                documentRepository.save(doc);
                throw new RuntimeException("This PDF contains scanned images. Only text-based PDFs are supported.");
            }

            // 2. Generate chunks
            List<String> textChunks = chunkingService.chunkText(extractedText);

            // 3. Save chunks with an incrementing index
            int chunkIndex = 0;
            for (String chunkText : textChunks) {
                DocumentChunk chunk = new DocumentChunk();
                chunk.setDocument(doc);
                chunk.setChunkIndex(chunkIndex++); // Notice the ++ here!
                chunk.setChunkText(chunkText);
                chunk = documentChunkRepository.save(chunk);


                try {
                    float[] vectorArray = embeddingService.generateEmbedding(chunkText);
                    String vectorString = Arrays.toString(vectorArray);

                    documentChunkRepository.updateEmbedding(chunk.getId(), vectorString);

                    Thread.sleep(1000);
                }

                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Embedding process interrupted");
                }
            }
            // 4. Update main document status
            doc.setTotalPages(totalPages);
            doc.setTotalChunks(textChunks.size());
            doc.setStatus("READY");
            documentRepository.save(doc);

        }

        catch (Exception e) {
            doc.setStatus("FAILED");
            documentRepository.save(doc);
            throw new RuntimeException("Error processing PDF: " + e.getMessage());
        }

        return doc.getId();
    }



    @Scheduled(fixedDelay = 300000)
    public  void detectStuckDocuments(){

    }
}