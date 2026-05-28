package com.example.ragpoc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(OpenAi openai, Chroma chroma, String uploadDir, Rag rag) {
    public record OpenAi(String apiKey, String baseUrl, String chatModel, String embeddingModel) {}
    public record Chroma(String url, String collection) {}
    public record Rag(int chunkSize, int chunkOverlap, int topK, int memoryLimit) {}
}
