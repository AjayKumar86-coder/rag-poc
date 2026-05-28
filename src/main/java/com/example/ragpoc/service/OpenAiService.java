package com.example.ragpoc.service;

import com.example.ragpoc.config.AppProperties;
import com.example.ragpoc.model.ChatMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {
    private final AppProperties props;
    private final RestClient client;

    public OpenAiService(AppProperties props) {
        this.props = props;
        this.client = RestClient.builder()
                .baseUrl(props.openai().baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.openai().apiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @SuppressWarnings("unchecked")
    public List<Double> embed(String text) {
        Map<String, Object> res = client.post().uri("/embeddings")
                .body(Map.of("model", props.openai().embeddingModel(), "input", text))
                .retrieve().body(Map.class);
        return (List<Double>) ((Map<String, Object>) ((List<Object>) res.get("data")).getFirst()).get("embedding");
    }

    @SuppressWarnings("unchecked")
    public String chat(List<ChatMessage> messages) {
        List<Map<String, String>> payloadMessages = messages.stream()
                .map(m -> Map.of("role", m.role(), "content", m.content()))
                .toList();
        Map<String, Object> res = client.post().uri("/chat/completions")
                .body(Map.of("model", props.openai().chatModel(), "temperature", 0.1, "messages", payloadMessages))
                .retrieve().body(Map.class);
        Map<String, Object> choice = (Map<String, Object>) ((List<Object>) res.get("choices")).getFirst();
        return (String) ((Map<String, Object>) choice.get("message")).get("content");
    }
}
