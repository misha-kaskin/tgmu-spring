package com.example.tgmuserversprin.handler;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FileTitleModel {
    private final String title;
    private final List<String> files;
}
