package com.Objects;

import java.util.Date;

import org.java_websocket.WebSocket;

public class ClientFX {
    private String nombre;
    private String id;
    private String password;
    private Date lastAcces;
    private WebSocket clienteWebSocket;

    public ClientFX(String nombre, String id, String password, Date lastAcces, WebSocket clienteWebSocket) {
        this.nombre = nombre;
        this.id = id;
        this.password = password;
        this.lastAcces = lastAcces;
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

    public Date getLastAcces() {
        return lastAcces;
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

    public void setLastAcces(Date lastAcces) {
        this.lastAcces = lastAcces;
    }
}
