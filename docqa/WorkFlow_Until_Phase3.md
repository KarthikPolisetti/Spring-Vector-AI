Phase A: The Ingestion Pipeline (PDF Upload)
1. The User Click (Frontend)

User Action: The user drags and drops a 300-page PDF into the React browser window.

React Files: The DocumentUpload component captures the file. It passes it to src/services/api.js, which triggers an Axios POST request (/api/documents/upload) with the file as multipart/form-data.

2. The Bouncer & The Reader (Backend - DocumentService)

Entry Point: Your Spring Boot DocumentController.java catches the API request and hands the file to DocumentService.java.

Validation & Registration: DocumentService.java verifies it's a real PDF, creates a Document entity, and saves it to PostgreSQL with the status PROCESSING.

Extraction: Using Apache PDFBox, the service strips the raw text out of the PDF.

3. The Butcher (Backend - ChunkingService)

Slicing: DocumentService.java passes the massive block of raw text to ChunkingService.java.

Overlapping: ChunkingService.java splits the text into precise ~500-word pieces with a 50-word overlap (the "panoramic photo" method) so the AI never loses its train of thought. These text chunks are saved into the document_chunks table via DocumentChunkRepository.java.

4. The Mathematician (Backend - EmbeddingService)

Vectorizing: Now, the backend loops through every chunk. For each chunk, it calls EmbeddingService.java.

AI API Call: EmbeddingService.java sends the raw text chunk over the internet to the Gemini Embedding API. Gemini returns an array of 768 decimal numbers (a vector).

Storage: Using a raw SQL update command in DocumentChunkRepository.java, the vector is attached to the chunk in the PostgreSQL database. The document status is updated to READY.

(Meanwhile, the React frontend is asynchronously polling GET /api/documents/{id}/status. Once it sees READY, the spinner disappears, and the Chat Interface opens!)

Phase B: The Query Pipeline (Chatting with the AI)
1. The User Question (Frontend)

User Action: The user types a tricky question like "What is the tone of this document?" and hits "Send" in the ChatInterface component.

React Files: api.js sends a POST request to /api/documents/{documentId}/query.

2. The Orchestra Conductor (Backend - QueryController)

Entry Point: QueryController.java receives the question. It acts as the boss, directing exactly where the question goes next.

3. The Middleman (Backend - AI Query Expansion)

Brainstorming: QueryController.java hands the raw question to LlmService.expandQuery().

AI API Call: Gemini reads the vague question and generates highly targeted search keywords (e.g., "tone, formal, informal, emotional, style").

4. The Search Engine (Backend - SimilaritySearchService & PostgreSQL)

Vectorizing the Keywords: SimilaritySearchService.java asks EmbeddingService.java to turn those expanded keywords into a 768-dimension vector.

Database Math: The vector is passed to PostgreSQL. Under the hood, the pgvector extension calculates Cosine Similarity—mathematically comparing the keyword vector against every chunk vector in the database.

Retrieval: PostgreSQL instantly returns the Top 5 text chunks that most closely match the mathematical meaning of the keywords.

5. The Final Expert (Backend - LlmService)

The Empty Box Method: QueryController.java takes the user's original question PLUS the Top 5 retrieved text chunks and hands them to LlmService.generateAnswer().

AI API Call: LlmService wraps this in a strict System Prompt ("Answer ONLY using the provided context. Do not hallucinate.") and sends it to the Gemini Chat API with the temperature set to 0.1.

Generation: Gemini reads the 5 chunks, finds the answer, and writes a conversational response.

6. The Final Output (Frontend)

Delivery: QueryController.java returns a JSON response containing both the AI's final answer and the raw source chunks.

Display: The React ChatInterface component renders the final text on the screen cleanly using react-markdown.

Transparency: The SourceChunks component renders a collapsible dropdown under the answer, allowing the user to click "View Sources" and actually read the raw paragraphs the AI used, proving it didn't hallucinate.
