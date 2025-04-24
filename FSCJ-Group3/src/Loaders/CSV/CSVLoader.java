package Loaders.CSV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CSVLoader<T> {
    private static final Map<Class<?>, CSVLoader<?>> instances = new HashMap<>();
    private final String fileName;
    private final Function<Map<String, String>, T> objectCreator;
    private final Function<T, Map<String, String>> objectSerializer;

    private CSVLoader(String fileName,
                      Function<Map<String, String>, T> objectCreator,
                      Function<T, Map<String, String>> objectSerializer) {
        this.fileName = fileName;
        this.objectCreator = objectCreator;
        this.objectSerializer = objectSerializer;
    }

    @SuppressWarnings("unchecked")
    public static <T> CSVLoader<T> getInstance(Class<T> clazz,
                                               String fileName,
                                               Function<Map<String, String>, T> objectCreator,
                                               Function<T, Map<String, String>> objectSerializer) {
        return (CSVLoader<T>) instances.computeIfAbsent(
                clazz,
                key -> new CSVLoader<>(fileName, objectCreator, objectSerializer)
        );
    }

    public List<T> loadRecords() {
        List<Map<String, String>> records = CSVReader.getRecords(fileName);
        List<T> objects = new ArrayList<>();
        for (Map<String, String> record : records) {
            objects.add(objectCreator.apply(record));
        }
        return objects;
    }

    public void saveRecords(List<T> data) {
        List<Map<String, String>> serialized = new ArrayList<>();
        for (T obj : data) {
            serialized.add(objectSerializer.apply(obj));
        }
        CSVWriter.writeRecords(fileName, serialized);
    }
}

