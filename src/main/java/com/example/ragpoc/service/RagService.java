package com.example.ragpoc.service;

import com.example.ragpoc.config.AppProperties;
import com.example.ragpoc.model.ChatMessage;
import com.example.ragpoc.model.RetrievedChunk;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RagService {
    private final AppProperties props;
    private final OpenAiService openAi;
    private final ChromaService chroma;
    private final MemoryService memory;

    public RagService(AppProperties props, OpenAiService openAi, ChromaService chroma, MemoryService memory) {
        this.props = props;
        this.openAi = openAi;
        this.chroma = chroma;
        this.memory = memory;
    }

    public RagResponse ask(String question) {
        List<RetrievedChunk> chunks = chroma.query(openAi.embed(question), props.rag().topK());
        StringBuilder context = new StringBuilder();
        for (RetrievedChunk chunk : chunks) {
            context.append("Source: ").append(chunk.source()).append("\n").append(chunk.text()).append("\n\n");
        }
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", "Answer using the provided context. Be concise. If context is insufficient, say so."));
        messages.addAll(memory.recent());
        messages.add(new ChatMessage("user", "Context:\n" + context + "\nQuestion: " + question));
        String answer = openAi.chat(messages);
        memory.add("user", question);
        memory.add("assistant", answer);
        return new RagResponse(answer, chunks);
    }

    public record RagResponse(String answer, List<RetrievedChunk> contexts) {}
}
