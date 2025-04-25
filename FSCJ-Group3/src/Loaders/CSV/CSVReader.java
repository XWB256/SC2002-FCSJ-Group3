package Loaders.CSV;


import Utilities.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVReader {
    private static final String DATA_FOLDER = "FSCJ-Group3/Databases/";

    //read csv file and return a list, each element in the list is a map representing one line of data
    public static List<Map<String, String>> getRecords(String filename) {
        List<Map<String, String>> records = new ArrayList<>();
        Path filePath = Paths.get(DATA_FOLDER + filename + ".csv");

        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String headerLine = br.readLine(); // read headers
            if (headerLine == null) {
                throw new IOException("file is empty: " + filename);
            }

            String[] headers = headerLine.split(",");

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, String> record = new HashMap<>();

                for (int i = 0; i < headers.length; i++) {
                    record.put(headers[i].trim(), (i < values.length) ? Utility.replaceEmptyString(values[i].trim()) : "None");
                }
                records.add(record);
            }
        } catch (IOException e) {
            System.err.println("fail to read the file: " + filename + ".csv");
            e.printStackTrace();
        }

        return records;
    }

    // test
    public static void main(String[] args) {
        List<Map<String, String>> applicants = CSVReader.getRecords("Applicant");
        System.out.println("Applicant.csv: ");
        for (Map<String, String> applicant : applicants) {
            System.out.println(applicant);
        }
    }
}

