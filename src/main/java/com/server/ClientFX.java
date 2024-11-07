package com.server;

import org.java_websocket.WebSocket;

public class ClientFX {
    private String nombre;
    private int id;
    private int password;
    private WebSocket clienteWebSocket;

    public ClientFX(String nombre, int id, int password, WebSocket clienteWebSocket) {
        this.nombre = nombre;
        this.id = id;
        this.password = password;
        this.clienteWebSocket = clienteWebSocket;
    }

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }

    public int getPassword() {
        return password;
    }

    public WebSocket getClienteWebSocket() {
        return clienteWebSocket;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPassword(int password) {
        this.password = password;
    }
}
