package com.example.ragpoc.service;

import com.example.ragpoc.config.AppProperties;
import com.example.ragpoc.model.DocumentChunk;
import com.example.ragpoc.model.RetrievedChunk;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChromaService {
    private static final String TENANT = "default_tenant";
    private static final String DATABASE = "default_database";
    private final AppProperties props;
    private final RestClient client;
    private String collectionId;

    public ChromaService(AppProperties props) {
        this.props = props;
        this.client = RestClient.builder().baseUrl(props.chroma().url()).build();
    }

    @PostConstruct
    public void init() {
        try {
            collectionId = getCollectionId();
        } catch (Exception ignored) {
            createCollection();
            collectionId = getCollectionId();
        }
    }

    public void add(List<DocumentChunk> chunks) {
        if (chunks.isEmpty()) {
            return;
        }
        client.post().uri("/api/v2/tenants/{tenant}/databases/{database}/collections/{id}/add",
                        TENANT, DATABASE, collectionId)
                .body(Map.of(
                        "ids", chunks.stream().map(DocumentChunk::id).toList(),
                        "documents", chunks.stream().map(DocumentChunk::text).toList(),
                        "embeddings", chunks.stream().map(DocumentChunk::embedding).toList(),
                        "metadatas", chunks.stream().map(c -> Map.of("source", c.source())).toList()
                ))
                .retrieve().toBodilessEntity();
    }

    @SuppressWarnings("unchecked")
    public List<RetrievedChunk> query(List<Double> embedding, int topK) {
        Map<String, Object> res = client.post().uri("/api/v2/tenants/{tenant}/databases/{database}/collections/{id}/query",
                        TENANT, DATABASE, collectionId)
                .body(Map.of(
                        "query_embeddings", List.of(embedding),
                        "n_results", topK,
                        "include", List.of("documents", "metadatas", "distances")
                ))
                .retrieve().body(Map.class);
        List<String> docs = (List<String>) ((List<Object>) res.get("documents")).getFirst();
        List<Map<String, Object>> metas = (List<Map<String, Object>>) ((List<Object>) res.get("metadatas")).getFirst();
        List<Number> distances = (List<Number>) ((List<Object>) res.get("distances")).getFirst();
        List<RetrievedChunk> out = new ArrayList<>();
        for (int i = 0; i < docs.size(); i++) {
            out.add(new RetrievedChunk(String.valueOf(metas.get(i).get("source")), docs.get(i), distances.get(i).doubleValue()));
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private String getCollectionId() {
        List<Map<String, Object>> collections = client.get()
                .uri("/api/v2/tenants/{tenant}/databases/{database}/collections", TENANT, DATABASE)
                .retrieve().body(List.class);
        return collections.stream()
                .filter(c -> props.chroma().collection().equals(c.get("name")))
                .findFirst()
                .map(c -> String.valueOf(c.get("id")))
                .orElseThrow();
    }

    private void createCollection() {
        client.post().uri("/api/v2/tenants/{tenant}/databases/{database}/collections", TENANT, DATABASE)
                .body(Map.of(
                        "name", props.chroma().collection(),
                        "get_or_create", true,
                        "metadata", Map.of("hnsw:space", "cosine")
                ))
                .retrieve().toBodilessEntity();
    }
}
