package com.example.tgmuserversprin.controller;

import com.example.tgmuserversprin.handler.DbHandler;
import com.example.tgmuserversprin.handler.FileTitleModel;
import com.example.tgmuserversprin.model.Data;
import com.example.tgmuserversprin.model.Model;
import com.example.tgmuserversprin.model.ModuleModel;
import com.example.tgmuserversprin.model.SubjectModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class SubjectController {
    @Autowired
    private final DbHandler dbHandler;

    public SubjectController(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @CrossOrigin
    @PostMapping(value = "/subject", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void safeSubject(@RequestBody List<String> model) throws SQLException {
        List<SubjectModel> subjectModels = model.stream()
                .map(el -> parseModelFromString(el))
                .collect(Collectors.toList());
        log.info(String.valueOf(subjectModels));
        dbHandler.safeSubject(subjectModels);
    }

    @CrossOrigin
    @DeleteMapping(value = "/subject")
    public void deleteSubject(@RequestBody List<String> model) throws SQLException, IOException {
        List<SubjectModel> subjectModels = model.stream()
                .map(el -> parseModelFromString(el))
                .collect(Collectors.toList());
        dbHandler.deleteSubject(subjectModels);
    }

    @CrossOrigin
    @PutMapping(value = "/subject")
    public void updateSubject(@RequestBody List<String> model) throws SQLException {
        List<SubjectModel> subjectModels = model.stream()
                .map(el -> parseModelFromString(el))
                .collect(Collectors.toList());
        dbHandler.updateSubject(subjectModels);
    }

    private SubjectModel parseModelFromString(String model) {
        String[] el = model.split("/");
        String path = "src/main/resources/" + String.join("/", el);

        return SubjectModel.builder()
                .faculty(el[0])
                .course(el[1])
                .semester(el[2])
                .subjectName(el[3])
                .path(path)
                .build();
    }
}
