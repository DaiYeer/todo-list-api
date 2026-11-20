/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.company.todolist;

/**
 *
 * @author aleja
 */


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConexionAPI {

    // Coincide con path('api/', include('hello.urls')) y path('tareas/', ...)
    private static final String BASE_URL = "http://127.0.0.1:8000/api/tareas/";
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static List<Tarea> obtenerTareas() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            throw new IOException("GET falló con código: " + res.statusCode());
        }
        return parsearLista(res.body());
    }

    public static void crearTarea(Tarea tarea) throws IOException, InterruptedException {
        String json = construirJson(tarea);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 201 && res.statusCode() != 200) {
            throw new IOException("POST falló (" + res.statusCode() + "): " + res.body());
        }
    }

    public static void actualizarTarea(Tarea tarea) throws IOException, InterruptedException {
        String json = construirJson(tarea);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + tarea.getId() + "/"))
                .header("Content-Type", "application/json; charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            throw new IOException("PUT falló (" + res.statusCode() + "): " + res.body());
        }
    }

    public static void eliminarTarea(int id) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + id + "/"))
                .DELETE()
                .build();

        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200 && res.statusCode() != 204) {
            throw new IOException("DELETE falló (" + res.statusCode() + "): " + res.body());
        }
    }

    // Empaqueta hora + descripcion en 'descripcion' para funcionar con tu modelo actual de Django
    private static String construirJson(Tarea t) {
        String descCompleta = empaquetarDescripcion(t.getHora(), t.getDescripcion());
        return "{"
                + "\"titulo\":\"" + escapar(t.getTitulo()) + "\","
                + "\"descripcion\":\"" + escapar(descCompleta) + "\","
                + "\"completada\":" + t.isCompletada()
                + "}";
    }

    private static String empaquetarDescripcion(String hora, String desc) {
        String h = (hora != null) ? hora.trim() : "";
        String d = (desc != null) ? desc : "";
        if (h.isEmpty()) return d;
        return "HORA::" + h + "::FIN_HORA\n" + d;
    }

    private static String[] desempaquetarDescripcion(String rawDesc) {
        if (rawDesc == null) return new String[]{"", ""};
        Matcher m = Pattern.compile("^HORA::(.*?)::FIN_HORA\\r?\\n?(.*)$", Pattern.DOTALL).matcher(rawDesc);
        if (m.find()) {
            return new String[]{m.group(1), m.group(2)};
        }
        return new String[]{"", rawDesc};
    }

    private static String escapar(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String desescapar(String s) {
        if (s == null) return "";
        return s.replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\\\", "\\");
    }

    private static List<Tarea> parsearLista(String body) {
        List<Tarea> lista = new ArrayList<>();
        if (body == null || body.isBlank()) return lista;
        Matcher m = Pattern.compile("\\{[^{}]*\\}").matcher(body);
        while (m.find()) {
            lista.add(parsearObjeto(m.group()));
        }
        return lista;
    }

    private static Tarea parsearObjeto(String obj) {
        int id = extraerInt(obj, "id");
        String titulo = desescapar(extraerStr(obj, "titulo"));
        String rawDesc = desescapar(extraerStr(obj, "descripcion"));
        boolean completada = extraerBool(obj, "completada");

        String[] partes = desempaquetarDescripcion(rawDesc);
        return new Tarea(id, titulo, partes[1], partes[0], completada);
    }

    private static int extraerInt(String json, String campo) {
        Matcher m = Pattern.compile("\"" + campo + "\"\\s*:\\s*(\\d+)").matcher(json);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    private static String extraerStr(String json, String campo) {
        Matcher m = Pattern.compile("\"" + campo + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"").matcher(json);
        return m.find() ? m.group(1) : "";
    }

    private static boolean extraerBool(String json, String campo) {
        Matcher m = Pattern.compile("\"" + campo + "\"\\s*:\\s*(true|false)").matcher(json);
        return m.find() && Boolean.parseBoolean(m.group(1));
    }
}
