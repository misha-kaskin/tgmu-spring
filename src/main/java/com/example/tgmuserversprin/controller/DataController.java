package com.example.tgmuserversprin.controller;

import com.example.tgmuserversprin.handler.DbHandler;
import com.example.tgmuserversprin.handler.FileTitleModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class DataController {
    @Autowired
    private final DbHandler dbHandler;

    public DataController(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @GetMapping("/data")
    public ResponseEntity<Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<FileTitleModel>>>>>>>> getData() throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD")
                .body(dbHandler.getData());
    }
}
