package com.example.ragpoc.service;

import com.example.ragpoc.config.AppProperties;
import com.example.ragpoc.model.DocumentChunk;
import com.example.ragpoc.util.TextChunker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class IngestionService {
    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);
    private final AppProperties props;
    private final DocumentParser parser;
    private final OpenAiService openAi;
    private final ChromaService chroma;

    public IngestionService(AppProperties props, DocumentParser parser, OpenAiService openAi, ChromaService chroma) {
        this.props = props;
        this.parser = parser;
        this.openAi = openAi;
        this.chroma = chroma;
    }

    public int ingest(MultipartFile[] files) throws Exception {
        Files.createDirectories(Path.of(props.uploadDir()));
        int count = 0;
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            String source = Path.of(file.getOriginalFilename()).getFileName().toString();
            Path saved = Path.of(props.uploadDir()).resolve(UUID.randomUUID() + "-" + source);
            Files.copy(file.getInputStream(), saved, StandardCopyOption.REPLACE_EXISTING);
            String text = parser.parse(file);
            List<DocumentChunk> chunks = new ArrayList<>();
            int index = 0;
            for (String chunk : TextChunker.chunk(text, props.rag().chunkSize(), props.rag().chunkOverlap())) {
                chunks.add(new DocumentChunk(source + "-" + UUID.randomUUID() + "-" + index++, source, chunk, openAi.embed(chunk)));
            }
            chroma.add(chunks);
            count += chunks.size();
            log.info("Ingested {} chunks from {}", chunks.size(), source);
        }
        return count;
    }
}
