package com.example.tgmuserversprin.handler;

import com.example.tgmuserversprin.model.ModuleModel;
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

public class ModuleHandler implements HttpHandler {
    private final DbHandler dbHandler = new DbHandler();
    private final Gson gson = new GsonBuilder().create();

    public ModuleHandler() throws SQLException {
    }

    @SneakyThrows
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        String method = exchange.getRequestMethod();

        if (method.equals("POST")) {
            System.out.println("Start module");

            String data = new String(exchange.getRequestBody().readAllBytes());
            String[] models = gson.fromJson(data, String[].class);
            List<ModuleModel> moduleModels = new ArrayList<>();

            for (String model : models) {
                moduleModels.add(parseModelFromString(model));
            }

            dbHandler.safeModule(moduleModels);

            System.out.println("End module");
        } else if (method.equals("DELETE")) {
            String data = new String(exchange.getRequestBody().readAllBytes());
            String[] models = gson.fromJson(data, String[].class);
            List<ModuleModel> moduleModels = new ArrayList<>();

            for (String model : models) {
                moduleModels.add(parseModelFromString(model));
            }

            try {
                dbHandler.deleteModule(moduleModels);
            } catch (Exception e) {
                System.out.println("Module del " + e.getMessage());
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
