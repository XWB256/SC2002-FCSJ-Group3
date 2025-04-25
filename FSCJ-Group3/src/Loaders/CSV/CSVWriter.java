package Loaders.CSV;

import java.io.*;
import java.util.*;

public class CSVWriter {
    private static final String DATA_FOLDER = "FSCJ-Group3/Databases/";

    public static void writeRecords(String fileName, List<Map<String, String>> records) {
        String path = DATA_FOLDER + fileName + ".csv";

        List<String> headers;

        //get headers from records or original csv files
        if (records != null && !records.isEmpty()) {
            headers = new ArrayList<>(records.get(0).keySet());
        } else {
            //if records is empty, read header from csv file
            headers = readHeaderFromFile(path);
            if (headers == null || headers.isEmpty()) {
                System.err.println("empty file: cannot find header");
                return;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            //write header
            writer.write(String.join(",", headers));
            writer.newLine();

            //write records
            if (records != null && !records.isEmpty()) {
                for (Map<String, String> record : records) {
                    List<String> row = new ArrayList<>();
                    for (String header : headers) {
                        row.add(record.getOrDefault(header, ""));
                    }
                    writer.write(String.join(",", row));
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            System.err.println("fail to write data to file: " + e.getMessage());
        }
    }

    private static List<String> readHeaderFromFile(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String headerLine = reader.readLine();
            if (headerLine != null) {
                return Arrays.asList(headerLine.split(","));
            }
        } catch (IOException e) {
            System.err.println("fail to read header: " + e.getMessage());
        }
        return null;
    }
}
