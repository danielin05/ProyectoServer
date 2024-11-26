package com.Objects;

import java.io.*;
import java.net.URL;
import java.util.Base64;

public class base64Transform {

    /**
     * Convierte una imagen de un archivo a una cadena Base64 con un prefijo para su uso en OllamaIA.
     * 
     * @param imagePath La ruta del archivo de imagen que se desea convertir (debe incluir el subdirectorio si está en uno).
     * @return Una cadena en Base64 que representa la imagen, o null si hubo un error.
     */
    public static String convertImageToBase64(String imagePath) {
        // Asegúrate de que la ruta incluye el subdirectorio correcto dentro del JAR
        URL resourceUrl = base64Transform.class.getClassLoader().getResource(imagePath);

        // Imprime la ruta del recurso
        if (resourceUrl != null) {
            System.out.println("Buscando imagen en: " + resourceUrl.toString());
        } else {
            System.out.println("No se encontró el recurso: " + imagePath);
        }

        // Intentamos abrir el recurso como un InputStream
        InputStream inputStream = base64Transform.class.getClassLoader().getResourceAsStream(imagePath);

        if (inputStream == null) {
            System.out.println("No se pudo encontrar la imagen en el flujo de entrada: " + imagePath);
            return null;
        }

        String base64String = null;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            // Convertimos la imagen a Base64
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            base64String = Base64.getEncoder().encodeToString(imageBytes);

            // Definimos el tipo de imagen (esto puede cambiar dependiendo del tipo de imagen)
            String imageType = "image/png"; // Cambiar si el tipo de imagen es diferente
            base64String = "data:" + imageType + ";base64," + base64String;

        } catch (IOException e) {
            System.err.println("Error al leer el archivo de imagen: " + e.getMessage());
            e.printStackTrace();
        }

        return base64String;
    }

    /**
     * Convierte una cadena Base64 a una imagen y la guarda en el sistema de archivos.
     * 
     * @param base64String La cadena Base64 que representa la imagen.
     * @param nombre El nombre de la imagen a guardar.
     * @param rutaSalida La ruta donde se guardará la imagen decodificada.
     */
    public static void convertirBase64ToImage(String base64String, String nombre, String rutaSalida) {
        try {
            // Si la cadena Base64 tiene un prefijo (como 'data:image/png;base64,'), lo eliminamos
            if (base64String.contains(",")) {
                base64String = base64String.split(",")[1];
            }

            byte[] imageBytes = Base64.getDecoder().decode(base64String);

            // Creamos el directorio de salida si no existe
            File outputDir = new File(rutaSalida);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

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
