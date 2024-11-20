package com.objects;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SSHMySQLConnection {

    private SSHClient sshClient;
    private Connection dbConnection;

    // Configuración SSH y MySQL
    private final String sshHost = "ieticloudpro.ieti.cat";
    private final String sshUser = "barretina3";
    private final String sshPrivateKey = "C:\\Users\\pablo\\pvicenterourassh";
    private final int sshPort = 20127;

    private final int mysqlPort = 3306;
    private final String mysqlDatabase = "barretina";
    private final String mysqlUser = "admin";
    private final String mysqlPassword = "admin";

    private final int localPort = 3306;

    /**
     * Conecta al servidor SSH y a la base de datos MySQL
     */
    public void connect() {
        try {
            // Configurar cliente SSH
            sshClient = new SSHClient();
            sshClient.loadKnownHosts();
            sshClient.addHostKeyVerifier((hostname, port, key) -> true);

            // Cargar clave privada y conectar
            KeyProvider keyProvider = sshClient.loadKeys(sshPrivateKey);
            sshClient.connect(sshHost, sshPort);
            sshClient.authPublickey(sshUser, keyProvider);

            System.out.println("Conexión SSH establecida.");

            // Establecer túnel SSH
            try (Session session = sshClient.startSession()) {
                session.exec("ssh -L " + localPort + ":localhost:" + mysqlPort + " " + sshUser + "@" + sshHost);
                System.out.println("Túnel SSH redirigiendo puerto " + localPort);
            }

            // Conectar a MySQL
            String jdbcUrl = "jdbc:mysql://127.0.0.1:" + localPort + "/" + mysqlDatabase;
            dbConnection = DriverManager.getConnection(jdbcUrl, mysqlUser, mysqlPassword);
            System.out.println("Conexión a la base de datos MySQL establecida.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Desconecta del servidor SSH y de la base de datos MySQL
     */
    public void disconnect() {
        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
                System.out.println("Conexión a MySQL cerrada.");
            }
            if (sshClient != null && sshClient.isConnected()) {
                sshClient.disconnect();
                System.out.println("Conexión SSH cerrada.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ejecuta una consulta SQL (SELECT)
     *
     * @param query la consulta SQL a ejecutar
     * @return el resultado de la consulta
     */
    public ResultSet executeQuery(String query) {
        try {
            Statement stmt = dbConnection.createStatement();
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Ejecuta una actualización SQL (INSERT, UPDATE, DELETE)
     *
     * @param query la consulta SQL de actualización
     * @return el número de filas afectadas
     */
    public int executeUpdate(String query) {
        try {
            Statement stmt = dbConnection.createStatement();
            return stmt.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void main(String[] args) {
        SSHMySQLConnection sshMySQLConnection = new SSHMySQLConnection();

        // Conectar
        sshMySQLConnection.connect();

        // Ejemplo de consulta (SELECT)
        try {
            ResultSet rs = sshMySQLConnection.executeQuery("SELECT * FROM camarero");
            while (rs != null && rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                System.out.println("ID: " + id + ", Nombre: " + nombre);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Cerrar conexiones
        sshMySQLConnection.disconnect();
    }
}
