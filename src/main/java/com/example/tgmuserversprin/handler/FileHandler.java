package com.example.tgmuserversprin.handler;

import com.example.tgmuserversprin.model.FileModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler implements HttpHandler {
    private final FileParser fileParser = new FileParser();
    private final DbHandler dbHandler = new DbHandler();

    public FileHandler() throws SQLException {
    }

    @SneakyThrows
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        String method = exchange.getRequestMethod();

        if (method.equals("POST")) {
            System.out.println("Start file");

            try {
                fileParser.Handle(exchange.getRequestBody());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            System.out.println("End file");
        } else if (method.equals("DELETE")) {
            String data = new String(exchange.getRequestBody().readAllBytes());

            Gson gson = new GsonBuilder()
                    .create();

            String[] models = gson.fromJson(data, String[].class);

            List<FileModel> fileModels = new ArrayList<>();

            for (String model : models) {
                String[] el = model.split("/");

                fileModels.add(FileModel.builder()
                        .faculty(el[0])
                        .course(el[1])
                        .semester(el[2])
                        .subjectName(el[3])
                        .module(el[4])
                        .subjectType(el[5])
                        .themeTitle(el[6])
                        .path("src/main/resources/" + String.join("/", el))
                        .name(el[7])
                        .build());
            }

            try {
                dbHandler.deleteFile(fileModels);
            } catch (Exception e) {
                System.out.println("file del " + e.getMessage());
            }

        } else if (method.equals("PUT")) {

        }

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD");

        exchange.sendResponseHeaders(200, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
