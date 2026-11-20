/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.company.todolist;

/**
 *
 * @author aleja
 */




public class Tarea {
    private int id;
    private String titulo;
    private String descripcion;
    private String hora;
    private boolean completada;

    public Tarea() {
    }

    public Tarea(int id, String titulo, String descripcion, String hora, boolean completada) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.hora = hora;
        this.completada = completada;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }

    @Override
    public String toString() {
        String h = (hora != null && !hora.isBlank()) ? " (" + hora + ")" : "";
        String check = completada ? "[✔] " : "";
        return check + titulo + h;
    }
}
