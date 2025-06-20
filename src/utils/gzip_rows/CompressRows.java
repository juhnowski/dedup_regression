import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.Deflater;

public class CompressRows {
    public static void main(String[] args) {

       // Используем BufferedReader для чтения файла построчно
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8));
             FileOutputStream stream = new FileOutputStream(args[0]+".rgz");   
            ) {
            String line;

            // Читаем файл построчно
            while ((line = reader.readLine()) != null) {
                // Преобразуем строку в массив байтов
                byte[] lineBytes = line.getBytes(StandardCharsets.UTF_8);
                byte[] compressed = deflate(lineBytes); // используем тот же метод deflate
                stream.write(compressed);
                stream.write('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для сжатия байтового массива с использованием Deflater
     */
    private static byte[] deflate(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }  
}
