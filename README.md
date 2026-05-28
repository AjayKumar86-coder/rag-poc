# RAG PoC

Minimal Spring Boot RAG app with OpenAI, ChromaDB, Thymeleaf UI, uploads, ingestion, retrieval, and in-memory chat history.

## Prereqs

- Java 21
- ChromaDB running on `http://localhost:8000`
- `OPENAI_API_KEY`

## Windows Run

```powershell
$env:OPENAI_API_KEY="sk-..."
$env:CHROMA_URL="http://localhost:8000"
mvnw.cmd clean package
java -jar target\rag-poc-0.0.1-SNAPSHOT.jar
```

Open `http://localhost:8080`.

## Optional Config

```powershell
$env:OPENAI_CHAT_MODEL="gpt-4o-mini"
$env:OPENAI_EMBEDDING_MODEL="text-embedding-3-small"
$env:UPLOAD_DIR="uploads"
$env:CHROMA_COLLECTION="rag_poc"
```
