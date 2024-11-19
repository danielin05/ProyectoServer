package com.server;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.userauth.keyprovider.*;

import java.sql.Connection;
import java.sql.DriverManager;

public class SSHMySQLConnection {

    public static void main(String[] args) {
        // Configuración de SSH
        String sshHost = "ieticloudpro.ieti.cat";  // Dirección del servidor SSH
        String sshUser = "barretina3";      // Usuario SSH
        String sshPrivateKey = "C:\\Users\\pablo\\pvicenterourassh"; // Ruta a tu clave privada SSH
        int sshPort = 20127;                      // Puerto SSH

        int mysqlPort = 3306;                   // Puerto de MySQL
        String mysqlDatabase = "barretina";     // Nombre de la base de datos
        String mysqlUser = "admin";             // Usuario MySQL
        String mysqlPassword = "admin";         // Contraseña MySQL

        // Puerto local en el que se escucha el túnel
        int localPort = 3306;

        // Crear un cliente SSH con SSHJ
        try (SSHClient client = new SSHClient()) {

            // Configurar el cliente SSH
            client.loadKnownHosts();
            client.addHostKeyVerifier((hostname, port, key) -> true); // Deshabilitar verificación de clave (no recomendado para producción)

            // Cargar la clave privada
            KeyProvider keyProvider = client.loadKeys(sshPrivateKey); // Cambié el método para cargar la clave
            client.connect(sshHost, sshPort);
            client.authPublickey(sshUser, keyProvider);

            // Establecer el túnel SSH
            try (Session session = client.startSession()) {
                // Redirigir el puerto local al puerto de MySQL
                session.exec("ssh -L " + localPort + ":localhost:" + mysqlPort + " " + sshUser + "@" + sshHost);

                System.out.println("Túnel SSH establecido en el puerto local " + localPort);

                // Conectar a MySQL a través del túnel SSH
                String jdbcUrl = "jdbc:mysql://127.0.0.1:" + localPort + "/" + mysqlDatabase;
                Connection conn = DriverManager.getConnection(jdbcUrl, mysqlUser, mysqlPassword);

                System.out.println("Conectado a la base de datos MySQL a través del túnel SSH");

                // Realizar operaciones con la base de datos...
                // Cierra la conexión a la base de datos
                conn.close();

                System.out.println("Conexión cerrada y túnel SSH desconectado.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
