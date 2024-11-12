package com.Objects;

import java.util.Date;

import org.java_websocket.WebSocket;

public class ClientFX {
    private String nombre;
    private String id;
    private String password;
    private Date lastAcces;
    private WebSocket clienteWebSocket;
    private boolean rememberPassword;

    public ClientFX(String nombre, String id, String password, Date lastAcces, WebSocket clienteWebSocket) {
        this.nombre = nombre;
        this.id = id;
        this.password = password;
        this.lastAcces = lastAcces;
        this.rememberPassword = false;
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

    public boolean getRememberPassword() {
        return rememberPassword;
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

    public void setRememberPassword(boolean rememberPassword) {
        this.rememberPassword = rememberPassword;
    }
}
