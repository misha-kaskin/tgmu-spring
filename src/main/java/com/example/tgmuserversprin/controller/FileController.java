package com.example.tgmuserversprin.controller;


import com.example.tgmuserversprin.handler.DbHandler;
import com.example.tgmuserversprin.model.FileModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class FileController {
    @Autowired
    private final DbHandler dbHandler;

    public FileController(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @CrossOrigin
    @PostMapping("/file")
    public void uploadFile(@RequestParam("file") MultipartFile[] files) throws IOException {
        Arrays.stream(files).forEach(el -> {
                    String[] str = el.getOriginalFilename().split("/");
                    String fileName = str[str.length - 1];
                    String filePath = "src/main/resources/"
                            + String.join("/", Arrays.copyOfRange(str, 0, str.length - 1));

                    log.info(fileName);
                    log.info(filePath);

                    try {
                        dbHandler.safeFile(fileName, filePath);

                        FileUtils.forceMkdir(new File(filePath));
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(new File("src/main/resources/" + el.getOriginalFilename()));
                        fos.write(el.getBytes());
                        fos.close();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    @CrossOrigin
    @DeleteMapping("/file")
    public void deleteFile(@RequestBody List<String> models) throws SQLException, IOException {
        List<FileModel> fileModels = models.stream()
                .map(el -> parseModelFromString(el))
                .collect(Collectors.toList());

        dbHandler.deleteFile(fileModels);
    }

    private FileModel parseModelFromString(String model) {
        String[] el = model.split("/");

        return FileModel.builder()
                .faculty(el[0])
                .course(el[1])
                .semester(el[2])
                .subjectName(el[3])
                .module(el[4])
                .subjectType(el[5])
                .themeTitle(el[6])
                .path("src/main/resources/" + String.join("/", el))
                .name(el[7])
                .build();
    }
}
