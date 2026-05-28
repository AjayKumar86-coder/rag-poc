package com.example.ragpoc.service;

import com.example.ragpoc.config.AppProperties;
import com.example.ragpoc.model.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemoryService {
    private final ArrayDeque<ChatMessage> history = new ArrayDeque<>();
    private final int limit;

    public MemoryService(AppProperties props) {
        this.limit = props.rag().memoryLimit();
    }

    public synchronized void add(String role, String content) {
        history.addLast(new ChatMessage(role, content));
        while (history.size() > limit) {
            history.removeFirst();
        }
    }

    public synchronized List<ChatMessage> recent() {
        return new ArrayList<>(history);
    }
}
