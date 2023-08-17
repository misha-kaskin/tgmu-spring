package com.example.tgmuserversprin.handler;

import com.example.tgmuserversprin.model.TitleModel;
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

public class TitleHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder().create();
    private final DbHandler dbHandler = new DbHandler();

    public TitleHandler() throws SQLException {
    }

    @SneakyThrows
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        String method = exchange.getRequestMethod();

        if (method.equals("POST")) {
            System.out.println("Start title");
            String data = new String(exchange.getRequestBody().readAllBytes());
            String[] models = gson.fromJson(data, String[].class);
            List<TitleModel> titleModels = new ArrayList<>();

            for (String model : models) {
                titleModels.add(parseFromString(model));
            }

            dbHandler.safeTitle(titleModels);

            System.out.println("End title");
        } else if (method.equals("DELETE")) {
            String data = new String(exchange.getRequestBody().readAllBytes());
            String[] models = gson.fromJson(data, String[].class);
            List<TitleModel> titleModels = new ArrayList<>();

            for (String model : models) {
                titleModels.add(parseFromString(model));
            }

            try {
                dbHandler.deleteTitle(titleModels);
            } catch (Exception e) {
                System.out.println("title del " + e.getMessage());
            }
        } else if (method.equals("PUT")) {
            String data = new String(exchange.getRequestBody().readAllBytes());
            String[] models = gson.fromJson(data, String[].class);
            List<TitleModel> titleModels = new ArrayList<>();

            for (String model : models) {
                titleModels.add(parseFromString(model));
            }

            dbHandler.updateTitle(titleModels);
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
