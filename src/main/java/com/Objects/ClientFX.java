package com.Objects;

import org.java_websocket.WebSocket;

public class ClientFX {
    private String nombre;
    private String id;
    private String password;
    private WebSocket clienteWebSocket;

    public ClientFX(String nombre, String id, String password, WebSocket clienteWebSocket) {
        this.nombre = nombre;
        this.id = id;
        this.password = password;
        this.clienteWebSocket = clienteWebSocket;
    }

    public String getNombre() {
        return nombre;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public WebSocket getClienteWebSocket() {
        return clienteWebSocket;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setClienteWebSocket(WebSocket clienteWebSocket) {
        this.clienteWebSocket = clienteWebSocket;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
