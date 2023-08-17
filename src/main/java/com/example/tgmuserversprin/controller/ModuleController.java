package com.example.tgmuserversprin.controller;

import com.example.tgmuserversprin.handler.DbHandler;
import com.example.tgmuserversprin.model.Data;
import com.example.tgmuserversprin.model.ModuleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ModuleController {
    @Autowired
    private final DbHandler dbHandler;

    public ModuleController(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @CrossOrigin
    @PostMapping(value = "/module")
    public void safeModule(@RequestBody List<String> model) throws SQLException {
        List<ModuleModel> moduleModels = model.stream()
                .map(el -> parseModelFromString(el))
                .collect(Collectors.toList());
        dbHandler.safeModule(moduleModels);
    }

    @CrossOrigin
    @DeleteMapping(value = "/module")
    public void deleteModule(@RequestBody List<String> model) throws SQLException, IOException {
        List<ModuleModel> moduleModels = model.stream()
                .map(el -> parseModelFromString(el))
                .collect(Collectors.toList());
        dbHandler.deleteModule(moduleModels);
    }

    private ModuleModel parseModelFromString(String model) {
        String[] el = model.split("/");
        String path = "src/main/resources/" + String.join("/", el);

        return ModuleModel.builder()
                .faculty(el[0])
                .course(el[1])
                .semester(el[2])
                .subjectName(el[3])
                .module(el[4])
                .subjectType(el[5])
                .path(path)
                .build();
    }
}
