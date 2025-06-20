import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.Deflater;

public class Compress {
    public static void main(String[] args) {
        System.out.println(args[0]);    
        byte[] input;
        try {
            input = Files.readAllBytes(Paths.get(args[0]));
            byte[] compressed = deflate(input); // используем тот же метод deflate
            
            try (FileOutputStream stream = new FileOutputStream(args[0]+".gzip")) {
                stream.write(compressed);
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
