package com.Objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class imageToBase64 {

    /**
     * Convierte una imagen en un archivo a una cadena Base64 con prefijo para su uso en OllamaIA.
     *
     * @param imagePath La ruta del archivo de imagen que se desea convertir.
     * @return Una cadena en Base64 que representa la imagen, o null si hubo un error.
     */
    public static String convertImageToBase64(String imagePath) {
        File file = new File(imagePath);
        
        String base64String = null;

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fileInputStream.read(bytes);
            
            base64String = Base64.getEncoder().encodeToString(bytes);
            
            String imageType = "image/png";
            base64String = "data:" + imageType + ";base64," + base64String;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return base64String;
    }

    public static void main(String[] args) {
        String imagePath = "ruta/a/tu/imagen.png"; 
        String base64 = convertImageToBase64(imagePath);
        
        if (base64 != null) {
            System.out.println("Cadena Base64: " + base64);
        } else {
            System.out.println("Error al convertir la imagen a Base64.");
        }
    }
}