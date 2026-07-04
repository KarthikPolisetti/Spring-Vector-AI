# Enterprise Advanced RAG Document QA System

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)

An enterprise-grade, fully containerized Retrieval-Augmented Generation (RAG) pipeline that allows users to upload massive PDF documents and query them using natural language. Built with Spring Boot, React, and PostgreSQL (`pgvector`), this system implements advanced AI routing and asynchronous processing to handle heavy concurrent workloads.

## 🚀 Key Features

* **Advanced RAG Pipeline:** Contextualizes LLM responses by mathematically matching user queries to document chunks stored in a pgvector database.
* **Intelligent Query Expansion (AI Router):** Utilizes a "Middleman AI" to dynamically translate abstract or indirect user queries into optimized vector-search parameters, drastically improving retrieval accuracy.
* **Asynchronous Ingestion Engine:** Implements non-blocking polling and `@EnableScheduling` watchdogs to process heavy PDF uploads (300+ pages) without starving database connections or freezing the UI.
* **Token-Optimized LLM Integration:** Communicates with the Google Gemini API, featuring robust error handling, token limit management (up to 2048 output tokens), and dynamic prompt engineering.
* **Fully Containerized:** One-click local deployment using Docker Compose, orchestrating the React frontend, Spring Boot backend, and pgvector database across an isolated network.

## 🏗️ Architecture Overview

1. **Frontend (React/Vite & Nginx):** Provides a responsive UI for file uploads and a conversational chat interface.
2. **Backend (Spring Boot 3):** Exposes REST APIs, extracts text via Apache PDFBox, manages asynchronous chunking/embedding, and orchestrates the LLM dual-routing process.
3. **Database (PostgreSQL + pgvector):** Stores document chunks and their high-dimensional vector embeddings for low-latency cosine similarity search.
4. **LLM Provider (Google Gemini):** Powers both the Query Expansion module and the Final Answer Generation module.

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot, Spring Data JPA, Apache PDFBox, Maven
* **Frontend:** React 18, Vite, Node.js, HTML/CSS
* **Database:** PostgreSQL 16, `pgvector` extension
* **AI/LLM:** Google Gemini API (gemini-1.5-flash)
* **DevOps:** Docker, Docker Compose

## ⚡ Quick Start (Run Locally)

### Prerequisites
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running.
* A free [Google Gemini API Key](https://aistudio.google.com/app/apikey).

### Installation & Launch

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/KarthikPolisetti/Spring-Vector-AI]
   cd [SpringVectorAi]


  Build the Java Backend:

Bash
cd backend
mvn clean package -DskipTests
cd ..
Launch the Docker Fleet:
Replace YOUR_API_KEY with your actual Gemini API Key.

Windows (PowerShell):

PowerShell
$env:GEMINI_API_KEY="YOUR_API_KEY"; docker-compose up --build -d
Mac/Linux:

Bash
GEMINI_API_KEY="YOUR_API_KEY" docker-compose up --build -d
Access the Application:
Open your browser and navigate to http://localhost:3000.

🧠 How the Query Expansion Works
To prevent "hallucinations" and handle vague questions (e.g., "Is this about a person or a company?"), the system does not search the database immediately.

The user's query is sent to a Query Expander LLM.

The AI generates highly specific search keywords based on the intent.

These keywords are vectorized and sent to pgvector for similarity matching.

The retrieved document chunks and the original prompt are sent to the Generator LLM to construct the final response.
