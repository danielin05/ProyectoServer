package com.objects;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CommandDAO {

    private List<Comanda> comands;
     
    private static final String DB_URL = "jdbc:mysql://localhost:3306/barretina";

    
    
        public static void main(String[] args) {

            Connection conn = DBConnect(DB_URL);
            //saveNewCommand(conn, addCommand());
            updateCommand(conn, addCommand());
    }

    /* FUNCION PARA ESTABLECER LA CONEXION A LA BASE DE DATOS */

    public static Connection DBConnect(String url){
        String user = "daniel",password = "P@ssw0rd";
        try {
            Connection conn = DriverManager.getConnection(DB_URL, user, password);
            System.out.println("CONECTION SUCCESSFUL");
            return conn;

        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
            return null;
    }

    /* FUNCION PARA GUARDAR UNA COMANDA TOTALMENTE NUEVA JUNTO A TODOS SUS PRODUCTOS E INFORMACION */
    
    public static void saveNewCommand(Connection conn, Comanda comanda){
        Integer ultimoId = 0;

        String createCommand = "INSERT INTO comanda(id_camarero, num_mesa, clientes, estado, precio_total) " +
                                 "VALUES(?, ?, ?, ?, ?);";

        String createDetailsCommand = "INSERT INTO detalle_comanda(id_comanda, nombre_producto, descripcion_producto, precio_producto, estado_producto) " +
                                     "VALUES(?, ?, ?, ?, ?);";

        String lastCommandId = "SELECT MAX(id) AS ultimo_id FROM comanda;";
                                     
        try (PreparedStatement pstmt = conn.prepareStatement(createCommand)) {
            pstmt.setInt(1, Integer.parseInt(comanda.getClientFX().getId())); // ID del camarero
            pstmt.setInt(2, comanda.getNumber());                             // Número de mesa
            pstmt.setInt(3, comanda.getClientsNumber());                      // Número de clientes
            pstmt.setString(4, comanda.getEstado());                          // Estado de la comanda
            pstmt.setDouble(5, calculateTotalPrice(comanda));                 // Precio total de la comanda

            pstmt.executeUpdate();
            System.out.println("SE HA CREADO CORRECTAMENTE LA COMANDA");

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
        } 

        try (PreparedStatement pstmt = conn.prepareStatement(lastCommandId)) {

            ResultSet rs = pstmt.executeQuery();

            // Verificar si el resultado es vacío (cuando la tabla está vacía)
            if (rs.next()) {
                ultimoId = rs.getInt("ultimo_id");
            }

            // Si no se encuentra un id, asignamos el valor inicial
            if (ultimoId == 0) {
                ultimoId = 1;
            }

            rs.close();

            } catch (SQLException e) {
                System.out.println("No se ha cargado el ultimo id " + e.getMessage());
            }

        for (CommandProduct commandProduct : comanda.getProducts()){

            try (PreparedStatement pstmt = conn.prepareStatement(createDetailsCommand)) {
                pstmt.setInt(1, ultimoId);                                                      // ID de la comanda
                pstmt.setString(2, commandProduct.getProducte().getNombre());                   // Nombre del producto
                pstmt.setString(3, commandProduct.getProducte().getDescription());              // Descripcion del producto
                pstmt.setDouble(4, Double.parseDouble(commandProduct.getProducte().getPreu())); // Precio del producto
                pstmt.setString(5, commandProduct.getEstado());                                 // Estado del producto

                pstmt.executeUpdate();
                System.out.println("SE HAN CREADO CORRECTAMENTE LOS DETALLES DE LA COMANDA");

            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
            } 
        }
    }

    /* FUNCION PARA CALCULAR EL PRECIO TOTAL DE LOS PRODUCTOS DE LA COMANDA */                   
    
    private static double calculateTotalPrice(Comanda comanda) {
        double total = 0.0;
        for (CommandProduct product : comanda.getProducts()) {
            total += Double.parseDouble(product.getProducte().getPreu());
        }
        return total;
    }

    /* FUNCION PARA ACTUALIZAR UNA COMANDA YA EXISTENTE */

    public static void updateCommand(Connection conn, Comanda comanda){

        Integer commandId = 0;

        String checkCommandId = "SELECT id FROM comanda " + 
                                "WHERE num_mesa = ? AND estado <> 'pagado';"; 

        String deletePastDetailsCommand = "DELETE FROM detalle_comanda WHERE id_comanda = ? AND estado_producto <> 'pagado';";

        String updateCommand = "UPDATE comanda " + 
                               "SET id_camarero = ?, num_mesa = ?, clientes = ?, estado = ?, precio_total = ? " + 
                               "WHERE id = ?;";

        String createDetailsCommand = "INSERT INTO detalle_comanda(id_comanda, nombre_producto, descripcion_producto, precio_producto, estado_producto) " +
                                      "VALUES(?, ?, ?, ?, ?);";
    
        try (PreparedStatement pstmt = conn.prepareStatement(checkCommandId)) {

            pstmt.setInt(1, comanda.getNumber());

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                commandId = rs.getInt("id");
            }

            rs.close();

        } catch (SQLException e) {
            System.out.println("Esta comanda no existe " + e.getMessage());
        }


        try (PreparedStatement pstmt = conn.prepareStatement(updateCommand)) {
        pstmt.setInt(1, Integer.parseInt(comanda.getClientFX().getId())); // ID del camarero
        pstmt.setInt(2, comanda.getNumber());                             // Número de mesa
        pstmt.setInt(3, comanda.getClientsNumber());                      // Número de clientes
        pstmt.setString(4, comanda.getEstado());                          // Estado de la comanda
        pstmt.setDouble(5, calculateTotalPrice(comanda));                 // Precio total de la comanda
        pstmt.setInt(6, commandId);

        pstmt.executeUpdate();
        System.out.println("SE HA ACTUALIZADO CORRECTAMENTE LA COMANDA");

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
        } 

        try (PreparedStatement pstmt = conn.prepareStatement(deletePastDetailsCommand)) {
            pstmt.setInt(1, commandId); // ID de la comanda
    
            pstmt.executeUpdate();
            System.out.println("SE HA BORRADO CORRECTAMENTE LOS DETALLES ANTIGUOS DE LA COMANDA");
    
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
            } 

        for (CommandProduct commandProduct : comanda.getProducts()){

            try (PreparedStatement pstmt = conn.prepareStatement(createDetailsCommand)) {
                pstmt.setInt(1, commandId);                                                      // ID de la comanda
                pstmt.setString(2, commandProduct.getProducte().getNombre());                   // Nombre del producto
                pstmt.setString(3, commandProduct.getProducte().getDescription());              // Descripcion del producto
                pstmt.setDouble(4, Double.parseDouble(commandProduct.getProducte().getPreu())); // Precio del producto
                pstmt.setString(5, commandProduct.getEstado());                                 // Estado del producto

                pstmt.executeUpdate();
                System.out.println("SE HAN CREADO CORRECTAMENTE LOS NUEVOS DETALLES DE LA COMANDA");

            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
            } 
        }
    
    }


    /* FUNCION TEMPORAL PARA HACER PRUEBAS SE BORRARA MAS ADELANTE */

    public static Comanda addCommand() {
        // Crear la primera comanda
        Product pizzaMargherita = new Product("Pizza Margherita", "8.50", "Tomato, mozzarella, and basil", "");
        pizzaMargherita.addTags(List.of("Italian", "Vegetarian", "caliente"));
        
        Product spaghettiCarbonara = new Product("Spaghetti Carbonara", "10.00", "Pasta with eggs, cheese, pancetta", "");
        spaghettiCarbonara.addTags(List.of("Italian", "Pasta", "caliente"));
        
        CommandProduct commandPizza1 = new CommandProduct(pizzaMargherita);
        CommandProduct commandSpaghetti1 = new CommandProduct(spaghettiCarbonara);
        CommandProduct commandPizza2 = new CommandProduct(pizzaMargherita);
        CommandProduct commandSpaghetti2 = new CommandProduct(spaghettiCarbonara);
        CommandProduct commandPizza3 = new CommandProduct(pizzaMargherita);
        CommandProduct commandSpaghetti3 = new CommandProduct(spaghettiCarbonara);
        
        Comanda comanda1 = new Comanda(1, 6, new ClientFX("Daniel", "2", "321"));
        comanda1.addProducts(List.of(commandPizza1, commandSpaghetti1, commandPizza2, commandSpaghetti2, commandPizza3, commandSpaghetti3));
        
        return comanda1;
        
    }    
}
