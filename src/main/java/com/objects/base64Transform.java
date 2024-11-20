package com.objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class base64Transform {

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

    public static void convertirBase64ToImage(String base64String, String nombre, String rutaSalida) {
        try {
            if (base64String.contains(",")) {
                base64String = base64String.split(",")[1];
            }
    
            byte[] imageBytes = Base64.getDecoder().decode(base64String);

            String outputPath = rutaSalida + File.separator + nombre;
    
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                fos.write(imageBytes);
                System.out.println("Imagen guardada en: " + outputPath);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error al decodificar el string Base64: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error al guardar la imagen: " + e.getMessage());
        }
    }
}