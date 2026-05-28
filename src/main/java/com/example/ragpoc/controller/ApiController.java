package com.example.ragpoc.controller;

import com.example.ragpoc.service.RagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class ApiController {
    private final RagService rag;

    public ApiController(RagService rag) {
        this.rag = rag;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @PostMapping("/api/chat")
    public RagService.RagResponse chat(@RequestBody Map<String, String> body) {
        try {
            if (body == null || !body.containsKey("message")) {
                throw new IllegalArgumentException("Missing 'message' parameter in request body.");
            }
            return rag.ask(body.get("message"));
        } catch (IllegalArgumentException e) {
            return Map.of("error", "Invalid input: " + e.getMessage());
        }
    }
}
