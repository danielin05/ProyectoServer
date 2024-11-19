package com.server;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class SSHMySQLConnection {

    public static void main(String[] args) {
        // Configuración de SSH
        String sshHost = "ieticloudpro.ieti.cat";  // IP o dominio del servidor SSH
        String sshUser = "barretina3";      // Tu usuario SSH
        String sshPrivateKey = "C:\\Users\\pablo\\pvicenterourassh"; // Ruta a tu clave privada SSH
        int sshPort = 20127;                      // Puerto SSH (22 por defecto)

        // Configuración de MySQL
        String mysqlHost = "localhost";         // Siempre es localhost en el túnel
        int mysqlPort = 3306;                   // Puerto de MySQL (3306 por defecto)
        String mysqlDatabase = "barretina"; // Nombre de la base de datos
        String mysqlUser = "admin"; // Usuario de MySQL
        String mysqlPassword = "admin"; // Contraseña de MySQL

        // Puerto local en el que se escucha el túnel
        int localPort = 3306;

        try {
            // Crear una nueva sesión SSH
            JSch jsch = new JSch();

            // Cargar la clave privada SSH (asegúrate de que el archivo esté en formato PEM y con la ruta correcta)
            jsch.addIdentity(sshPrivateKey);

            // Crear sesión SSH
            Session session = jsch.getSession(sshUser, sshHost, sshPort);

            // Evitar advertencias de la autenticación del servidor (opcional)
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            // Conectar a la sesión SSH
            session.connect();

            // Redirigir el puerto local al puerto de MySQL a través del túnel SSH
            session.setPortForwardingL(localPort, mysqlHost, mysqlPort);
            System.out.println("Túnel SSH establecido en el puerto local " + localPort);

            // Ahora, conecta a MySQL usando JDBC a través del túnel SSH
            String jdbcUrl = "jdbc:mysql://127.0.0.1:" + localPort + "/" + mysqlDatabase;
            Connection conn = DriverManager.getConnection(jdbcUrl, mysqlUser, mysqlPassword);

            System.out.println("Conectado a la base de datos MySQL a través del túnel SSH");

            // Realiza las operaciones de base de datos aquí...

            // Cierra la conexión a la base de datos y SSH
            conn.close();
            session.disconnect();
            System.out.println("Conexión cerrada y túnel SSH desconectado.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
