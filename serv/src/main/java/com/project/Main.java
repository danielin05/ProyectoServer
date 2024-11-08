package com.project;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Main {
    public static void main(String[] args) {
        try {
            // Cargar y parsear el archivo XML
            String userDir = System.getProperty("user.dir");
            File archivoXML = new File(userDir + "/src/main/resources/Productes.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivoXML);
            doc.getDocumentElement().normalize();

            // Mostrar todos los productos
            System.out.println("Contenido del archivo XML:");
            NodeList listaProductos = doc.getElementsByTagName("producto");
            for (int i = 0; i < listaProductos.getLength(); i++) {
                Node nodo = listaProductos.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;
                    System.out.println("ID: " + elemento.getAttribute("id"));
                    System.out.println("Tags: " + elemento.getAttribute("tags"));
                    System.out.println("Nombre: " + elemento.getElementsByTagName("nom").item(0).getTextContent());
                    System.out.println("Precio: " + elemento.getElementsByTagName("preu").item(0).getTextContent());
                    System.out.println("Descripción: " + elemento.getElementsByTagName("descripcio").item(0).getTextContent());
                    System.out.println("Imagen: " + elemento.getElementsByTagName("imatge").item(0).getTextContent());
                    System.out.println("---------------------------------");
                }
            }

            // Mostrar solo los productos con tag "bebida"
            System.out.println("\nProductos con tag 'bebida':");
            for (int i = 0; i < listaProductos.getLength(); i++) {
                Node nodo = listaProductos.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;
                    String tags = elemento.getAttribute("tags");
                    if (tags.contains("bebida")) {
                        System.out.println("ID: " + elemento.getAttribute("id"));
                        System.out.println("Nombre: " + elemento.getElementsByTagName("nom").item(0).getTextContent());
                        System.out.println("Precio: " + elemento.getElementsByTagName("preu").item(0).getTextContent());
                        System.out.println("Descripción: " + elemento.getElementsByTagName("descripcio").item(0).getTextContent());
                        System.out.println("---------------------------------");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}