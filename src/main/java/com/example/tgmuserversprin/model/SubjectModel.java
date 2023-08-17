package com.example.tgmuserversprin.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectModel {
    private final String faculty;
    private final String course;
    private final String semester;
    private final String subjectName;
    private final String path;
}
