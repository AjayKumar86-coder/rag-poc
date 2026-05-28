package com.example.ragpoc.util;

import java.util.ArrayList;
import java.util.List;

public class TextChunker {
    public static List<String> chunk(String text, int size, int overlap) {
        String clean = text == null ? "" : text.replaceAll("\\s+", " ").trim();
        List<String> chunks = new ArrayList<>();
        if (clean.isBlank()) {
            return chunks;
        }
        int start = 0;
        while (start < clean.length()) {
            int end = Math.min(start + size, clean.length());
            chunks.add(clean.substring(start, end));
            if (end == clean.length()) {
                break;
            }
            start = Math.max(0, end - overlap);
        }
        return chunks;
    }
}
