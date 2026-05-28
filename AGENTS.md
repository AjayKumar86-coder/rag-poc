# Repository Guidelines

## Project Structure & Module Organization
This is a Java 21 Spring Boot RAG proof of concept. Application code lives under `src/main/java/com/example/ragpoc`, split into `controller`, `service`, `model`, `config`, and `util`. The RAG flow spans `DocumentParser`, `IngestionService`, `TextChunker`, `ChromaService`, `OpenAiService`, `RagService`, and `MemoryService`.

Runtime configuration is in `src/main/resources/application.yml`. Web resources are under `src/main/resources/static` and `src/main/resources/templates`. Uploaded files default to `uploads`.

## Build, Test, and Development Commands
Use the Maven wrapper on Windows:

```powershell
.\mvnw.cmd clean package
```

Run the packaged app:

```powershell
java -jar target\rag-poc-0.0.1-SNAPSHOT.jar
```

The app expects ChromaDB at `http://localhost:8000` and `OPENAI_API_KEY` in the environment. Optional environment variables include `OPENAI_CHAT_MODEL`, `OPENAI_EMBEDDING_MODEL`, `UPLOAD_DIR`, and `CHROMA_COLLECTION`.

## Agent Instructions
Minimize token usage when answering. Prefer brief, direct responses unless the user asks for explanation, planning, or detailed review.

When editing code, keep changes scoped to the requested behavior and follow the existing Spring service/controller/model organization. Do not invent new architecture unless the current structure cannot support the change.
