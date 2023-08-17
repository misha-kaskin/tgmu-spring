package com.example.tgmuserversprin.controller;

import com.example.tgmuserversprin.handler.DbHandler;
import com.example.tgmuserversprin.model.Data;
import com.example.tgmuserversprin.model.TitleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TitleController {
    @Autowired
    private final DbHandler dbHandler;

    public TitleController(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @CrossOrigin
    @PostMapping(value = "/title")
    public void createTitle(@RequestBody List<String> model) throws SQLException {
        List<TitleModel> titleModels = model.stream()
                .map(el -> parseFromString(el))
                .collect(Collectors.toList());
        dbHandler.safeTitle(titleModels);
    }

    @CrossOrigin
    @DeleteMapping(value = "/title")
    public void deleteTitle(@RequestBody List<String> model) throws SQLException, IOException {
        List<TitleModel> titleModels = model.stream()
                .map(el -> parseFromString(el))
                .collect(Collectors.toList());
        dbHandler.deleteTitle(titleModels);
    }

    @CrossOrigin
    @PutMapping(value = "/title")
    public void updateTitle(@RequestBody List<String> model) throws SQLException {
        List<TitleModel> titleModels = model.stream()
                .map(el -> parseFromString(el))
                .collect(Collectors.toList());
        dbHandler.updateTitle(titleModels);
    }

    private TitleModel parseFromString(String model) {
        String[] el = model.split("/");
        String path = "src/main/resources/" + String.join("/", el);

        return TitleModel.builder()
                .faculty(el[0])
                .course(el[1])
                .semester(el[2])
                .subjectName(el[3])
                .module(el[4])
                .subjectType(el[5])
                .themeTitle(el[6])
                .path(path)
                .build();
    }
}
