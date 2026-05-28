package com.example.ragpoc.controller;

import com.example.ragpoc.service.IngestionService;
import com.example.ragpoc.service.RagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PageController {
    private final RagService rag;
    private final IngestionService ingestion;

    public PageController(RagService rag, IngestionService ingestion) {
        this.rag = rag;
        this.ingestion = ingestion;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/chat")
    public String chat(@RequestParam String message, Model model) {
        RagService.RagResponse response = rag.ask(message);
        model.addAttribute("answer", response.answer());
        model.addAttribute("contexts", response.contexts());
        return "index";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile[] files, Model model) throws Exception {
        model.addAttribute("message", "Ingested " + ingestion.ingest(files) + " chunks");
        return "upload";
    }
}
