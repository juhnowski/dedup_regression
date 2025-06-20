# АННОТАЦИЯ
Проект - изучение схема дедупликации на основе сходства для систем управления базами данных (СУБД) в режиме реального времени методом однопроходного кодирования:
- сжатие отдельных страниц базы данных
- сжатие сообщений журнала операций (oplog) на уровне блоков
- дельта-кодирование отдельных записей в базе данных на уровне байтов. 

Преимущества метода однопроходного кодирования: 
- уменьшение размера данных, хранящихся на диске, по сравнению с традиционными схемами сжатия 
- уменьшение объема данных, передаваемых по сети для служб репликации. 

Чтобы оценить работу алгоритма:
- написана модель распределенной NoSQL СУБД с поддержкой dedup в распределенную NoSQL СУБД
- проанализированы свойства модели. 

Цель:
- получить значение коэффициента блочного сжатия
- получить значение коэффициента сжатия для дедупликации
- получить значение коэффициента сжатия для дедупликации с блочнным сжатием


# 1. ВВЕДЕНИЕ
Темпы роста объема данных превышают снижение стоимости оборудования. Сжатие баз данных является одним из решений этой проблемы. Для
хранения баз данных, помимо экономии места, сжатие помогает сократить количество дисковых операций ввода-вывода и повысить производительность, поскольку запрашиваемые данные помещаются на меньшем количестве страниц. Для распределенных баз данных, реплицированных по географическим регионам, также существует острая необходимость в сокращении объема передачи данных, используемого для синхронизации реплик.
Наиболее широко используемый подход к сокращению объема данных в операционных СУБД — это сжатие на уровне блоков [30, 37, 46, 43, 3, 16]. Такие
СУБД используются для поддержки пользовательских приложений, которые выполняют простые запросы для извлечения небольшого количества записей за раз (в отличие от выполнения сложных запросов, которые сканируют большие сегменты базы данных). Хотя сжатие на уровне блоков простое и
эффективное, оно не решает проблему избыточности между блоками и, следовательно, оставляет значительные возможности для улучшения многих приложений (например, из-за версионирования на уровне приложений в вики или частичного копирования записей на досках объявлений). 

Дедупликация (dedup) стала
популярной в системах резервного копирования для устранения дублирующегося контента во всем корпусе данных, часто достигая гораздо более высоких коэффициентов сжатия. Поток резервного копирования делится на фрагменты, и в качестве идентификатора каждого фрагмента используется устойчивый к коллизиям хэш (например, SHA-1). Система дедупликации поддерживает глобальный индекс всех хэшей и использует его для
обнаружения дубликатов. Дедупликация хорошо работает как для основных, так и для резервных наборов данных, которые состоят из больших файлов, которые редко изменяются (а если и изменяются, то изменения редки).

К сожалению, традиционные схемы дедупликации на основе фрагментов не подходят
для операционных СУБД, где приложения выполняют запросы на обновление, которые изменяют отдельные записи. Количество дублирующихся данных в отдельной записи, скорее всего, незначительно. Но большие размеры фрагментов (например, 4–8 КБ) являются нормой, чтобы избежать огромных индексов в памяти и большого количества чтений с диска.


В этой статье представлена ​​dbDedup, облегченная схема для онлайновых
систем баз данных, которая использует дедупликацию на основе сходства [65] для
сжатия отдельных записей. 

Вместо индексации каждого хеша фрагмента,
dbDedup выбирает небольшое подмножество хешей фрагментов для каждой новой
записи базы данных, а затем использует этот образец для идентификации похожей
записи в базе данных.

Затем он использует дельта-сжатие на уровне байтов для
двух записей, чтобы уменьшить как используемое онлайн-хранилище, так и пропускную способность удаленной репликации. dbDedup обеспечивает более высокие коэффициенты сжатия
с меньшими накладными расходами памяти, чем дедупликация на основе фрагментов, и хорошо сочетается
со сжатием на уровне блоков, как показано на рис. 1.
Мы представляем и объединяем несколько методов для достижения этой эффективности.
Прежде всего, мы представляем новое двустороннее кодирование
для эффективной передачи закодированных новых записей (прямое кодирование) в
удаленные реплики, сохраняя новые записи с закодированными формами
выбранных исходных записей (обратное кодирование).


Ilia
Ilya
Il'ya
Il'ia

Juhnovckiy
Juchnovckiy
Jukhnovckiy
Juhnovskiy
Juchnovsckiy
Jukhnovskiy
Juhnowckiy
Juchnwvckiy
Jukhnowckiy
Juhnowskiy
Juchnowsckiy
Jukhnowskiy
Juhnovcki
Juchnovcki
Jukhnovcki
Juhnovski
Juchnovscki
Jukhnovski
Juhnowcki
Juchnwvcki
Jukhnowcki
Juhnowski
Juchnowscki
Jukhnowski
Juhnovcky
Juchnovcky
Jukhnovcky
Juhnovsky
Juchnovscky
Jukhnovsky
Juhnowcky
Juchnwvcky
Jukhnowcky
Juhnowsky
Juchnowscky
Jukhnowsky
Juhnovckyi
Juchnovckyi
Jukhnovckyi
Juhnovskyi
Juchnovsckyi
Jukhnovskyi
Juhnowckyi
Juchnwvckyi
Jukhnowckyi
Juhnowskyi
Juchnowsckyi
Jukhnowskyi
Juhnovckyii
Juchnovckyii
Jukhnovckyii
Juhnovskyii
Juchnovsckyii
Jukhnovskyii
Juhnowckyii
Juchnwvckyii
Jukhnowckyii
Juhnowskyii
Juchnowsckyii
Jukhnowskyii
Yuhnovckiy
Yuchnovckiy
Yukhnovckiy
Yuhnovskiy
Yuchnovsckiy
Yukhnovskiy
Yuhnowckiy
Yuchnwvckiy
Yukhnowckiy
Yuhnowskiy
Yuchnowsckiy
Yukhnowskiy
Yuhnovcki
Yuchnovcki
Yukhnovcki
Yuhnovski
Yuchnovscki
Yukhnovski
Yuhnowcki
Yuchnwvcki
Yukhnowcki
Yuhnowski
Yuchnowscki
Yukhnowski
Yuhnovcky
Yuchnovcky
Yukhnovcky
Yuhnovsky
Yuchnovscky
Yukhnovsky
Yuhnowcky
Yuchnwvcky
Yukhnowcky
Yuhnowsky
Yuchnowscky
Yukhnowsky
Yuhnovckyi
Yuchnovckyi
Yukhnovckyi
Yuhnovskyi
Yuchnovsckyi
Yukhnovskyi
Yuhnowckyi
Yuchnwvckyi
YYukhnowckyi
Yuhnowskyi
Yuchnowsckyi
Yukhnowskyi
Yuhnovckyii
Yuchnovckyii
Yukhnovckyii
Yuhnovskyii
Yuchnovsckyii
Yukhnovskyii
Yuhnowckyii
Yuchnwvckyii
Yukhnowckyii
Yuhnowskyii
Yuchnowsckyii
Yukhnowskyii
Iiuhnovckiy
Iiuchnovckiy
Iiukhnovckiy
Iiuhnovskiy
Iiuchnovsckiy
Iiukhnovskiy
Iiuhnowckiy
Iiuchnwvckiy
Iiukhnowckiy
Iiuhnowskiy
Iiuchnowsckiy
Iiukhnowskiy
Iiuhnovcki
Iiuchnovcki
Iiukhnovcki
Iiuhnovski
Iiuchnovscki
Iiukhnovski
Iiuhnowcki
Iiuchnwvcki
Iiukhnowcki
Iiuhnowski
Iiuchnowscki
Iiukhnowski
Iiuhnovcky
Iiuchnovcky
Iiukhnovcky
Iiuhnovsky
Iiuchnovscky
Iiukhnovsky
Iiuhnowcky
Iiuchnwvcky
Iiukhnowcky
Iiuhnowsky
Iiuchnowscky
Iiukhnowsky
Iiuhnovckyi
Iiuchnovckyi
Iiukhnovckyi
Iiuhnovskyi
Iiuchnovsckyi
Iiukhnovskyi
Iiuhnowckyi
Iiuchnwvckyi
IiYukhnowckyi
Iiuhnowskyi
Iiuchnowsckyi
Iiukhnowskyi
Iiuhnovckyii
Iiuchnovckyii
Iiukhnovckyii
Iiuhnovskyii
Iiuchnovsckyii
Iiukhnovskyii
Iiuhnowckyii
Iiuchnwvckyii
Iiukhnowckyii
Iiuhnowskyii
Iiuchnowsckyii
Iiukhnowskyii



Ilia Juhnovckiy
Ilia Juchnovckiy
Ilia Jukhnovckiy
Ilia Juhnovskiy
Ilia Juchnovsckiy
Ilia Jukhnovskiy
Ilia Juhnowckiy
Ilia Juchnwvckiy
Ilia Jukhnowckiy
Ilia Juhnowskiy
Ilia Juchnowsckiy
Ilia Jukhnowskiy
Ilia Juhnovcki
Ilia Juchnovcki
Ilia Jukhnovcki
Ilia Juhnovski
Ilia Juchnovscki
Ilia Jukhnovski
Ilia Juhnowcki
Ilia Juchnwvcki
Ilia Jukhnowcki
Ilia Juhnowski
Ilia Juchnowscki
Ilia Jukhnowski
Ilia Juhnovcky
Ilia Juchnovcky
Ilia Jukhnovcky
Ilia Juhnovsky
Ilia Juchnovscky
Ilia Jukhnovsky
Ilia Juhnowcky
Ilia Juchnwvcky
Ilia Jukhnowcky
Ilia Juhnowsky
Ilia Juchnowscky
Ilia Jukhnowsky
Ilia Juhnovckyi
Ilia Juchnovckyi
Ilia Jukhnovckyi
Ilia Juhnovskyi
Ilia Juchnovsckyi
Ilia Jukhnovskyi
Ilia Juhnowckyi
Ilia Juchnwvckyi
Ilia Jukhnowckyi
Ilia Juhnowskyi
Ilia Juchnowsckyi
Ilia Jukhnowskyi
Ilia Juhnovckyii
Ilia Juchnovckyii
Ilia Jukhnovckyii
Ilia Juhnovskyii
Ilia Juchnovsckyii
Ilia Jukhnovskyii
Ilia Juhnowckyii
Ilia Juchnwvckyii
Ilia Jukhnowckyii
Ilia Juhnowskyii
Ilia Juchnowsckyii
Ilia Jukhnowskyii
Ilia Yuhnovckiy
Ilia Yuchnovckiy
Ilia Yukhnovckiy
Ilia Yuhnovskiy
Ilia Yuchnovsckiy
Ilia Yukhnovskiy
Ilia Yuhnowckiy
Ilia Yuchnwvckiy
Ilia Yukhnowckiy
Ilia Yuhnowskiy
Ilia Yuchnowsckiy
Ilia Yukhnowskiy
Ilia Yuhnovcki
Ilia Yuchnovcki
Ilia Yukhnovcki
Ilia Yuhnovski
Ilia Yuchnovscki
Ilia Yukhnovski
Ilia Yuhnowcki
Ilia Yuchnwvcki
Ilia Yukhnowcki
Ilia Yuhnowski
Ilia Yuchnowscki
Ilia Yukhnowski
Ilia Yuhnovcky
Ilia Yuchnovcky
Ilia Yukhnovcky
Ilia Yuhnovsky
Ilia Yuchnovscky
Ilia Yukhnovsky
Ilia Yuhnowcky
Ilia Yuchnwvcky
Ilia Yukhnowcky
Ilia Yuhnowsky
Ilia Yuchnowscky
Ilia Yukhnowsky
Ilia Yuhnovckyi
Ilia Yuchnovckyi
Ilia Yukhnovckyi
Ilia Yuhnovskyi
Ilia Yuchnovsckyi
Ilia Yukhnovskyi
Ilia Yuhnowckyi
Ilia Yuchnwvckyi
Ilia YYukhnowckyi
Ilia Yuhnowskyi
Ilia Yuchnowsckyi
Ilia Yukhnowskyi
Ilia Yuhnovckyii
Ilia Yuchnovckyii
Ilia Yukhnovckyii
Ilia Yuhnovskyii
Ilia Yuchnovsckyii
Ilia Yukhnovskyii
Ilia Yuhnowckyii
Ilia Yuchnwvckyii
Ilia Yukhnowckyii
Ilia Yuhnowskyii
Ilia Yuchnowsckyii
Ilia Yukhnowskyii
Ilia Iiuhnovckiy
Ilia Iiuchnovckiy
Ilia Iiukhnovckiy
Ilia Iiuhnovskiy
Ilia Iiuchnovsckiy
Ilia Iiukhnovskiy
Ilia Iiuhnowckiy
Ilia Iiuchnwvckiy
Ilia Iiukhnowckiy
Ilia Iiuhnowskiy
Ilia Iiuchnowsckiy
Ilia Iiukhnowskiy
Ilia Iiuhnovcki
Ilia Iiuchnovcki
Ilia Iiukhnovcki
Ilia Iiuhnovski
Ilia Iiuchnovscki
Ilia Iiukhnovski
Ilia Iiuhnowcki
Ilia Iiuchnwvcki
Ilia Iiukhnowcki
Ilia Iiuhnowski
Ilia Iiuchnowscki
Ilia Iiukhnowski
Ilia Iiuhnovcky
Ilia Iiuchnovcky
Ilia Iiukhnovcky
Ilia Iiuhnovsky
Ilia Iiuchnovscky
Ilia Iiukhnovsky
Ilia Iiuhnowcky
Ilia Iiuchnwvcky
Ilia Iiukhnowcky
Ilia Iiuhnowsky
Ilia Iiuchnowscky
Ilia Iiukhnowsky
Ilia Iiuhnovckyi
Ilia Iiuchnovckyi
Ilia Iiukhnovckyi
Ilia Iiuhnovskyi
Ilia Iiuchnovsckyi
Ilia Iiukhnovskyi
Ilia Iiuhnowckyi
Ilia Iiuchnwvckyi
Ilia IiYukhnowckyi
Ilia Iiuhnowskyi
Ilia Iiuchnowsckyi
Ilia Iiukhnowskyi
Ilia Iiuhnovckyii
Ilia Iiuchnovckyii
Ilia Iiukhnovckyii
Ilia Iiuhnovskyii
Ilia Iiuchnovsckyii
Ilia Iiukhnovskyii
Ilia Iiuhnowckyii
Ilia Iiuchnwvckyii
Ilia Iiukhnowckyii
Ilia Iiuhnowskyii
Ilia Iiuchnowsckyii
Ilia Iiukhnowskyii

```
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.Deflater;

public class JsonBlockCompression {

    public static void main(String[] args) {
        // Исходная JSON-строка
        String jsonString = "{\"FirstName\":\"Ilya\", \"SecondName\":\"Juhnowski\",\"age\":48,\"dateOfBirthday\":\"18.02.1978\"}";

        // Парсим JSON в Map (ключ-значение)
        Map<String, String> jsonMap = parseJsonToMap(jsonString);

        // Сжимаем каждое поле (блок) отдельно
        Map<String, byte[]> compressedBlocks = compressJsonBlocks(jsonMap);

        // Вывод результата
        for (Map.Entry<String, byte[]> entry : compressedBlocks.entrySet()) {
            System.out.println("Поле: " + entry.getKey());
            System.out.println("Сжатые данные (hex): " + bytesToHex(entry.getValue()));
            System.out.println("-----");
        }
    }

    /**
     * Простой парсер JSON в Map (работает только с простыми строковыми и числовыми значениями)
     */
    private static Map<String, String> parseJsonToMap(String jsonString) {
        // Удаляем фигурные скобки
        String trimmed = jsonString.replaceAll("[{}\"]", "").trim();

        // Разбиваем по запятым между полями
        String[] pairs = trimmed.split(",");

        Map<String, String> map = new LinkedHashMap<>();
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                // Если значение — число, оставляем как есть, иначе оборачиваем в кавычки (упрощённо)
                if (!value.matches("-?\\d+(\\.\\d+)?")) {
                    // Предполагаем, что это строка (даже если без кавычек в исходнике)
                    value = "\"" + value + "\"";
                }

                map.put(key, value);
            }
        }

        return map;
    }

    /**
     * Сжимает каждое значение JSON (по ключу) отдельно с использованием Deflater
     */
    private static Map<String, byte[]> compressJsonBlocks(Map<String, String> jsonMap) {
        Map<String, byte[]> compressedMap = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Преобразуем значение в байты (UTF-8)
            byte[] input = value.getBytes(StandardCharsets.UTF_8);

            // Сжимаем с помощью Deflater
            byte[] compressed = deflate(input);

            // Сохраняем сжатый блок
            compressedMap.put(key, compressed);
        }

        return compressedMap;
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

    /**
     * Вспомогательный метод: переводит байты в hex-строку для удобного вывода
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}

```

сжать всю строку
```
byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
byte[] compressed = deflate(input); // используем тот же метод deflate
```