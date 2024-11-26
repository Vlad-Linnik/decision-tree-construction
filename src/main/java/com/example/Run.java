package com.example;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class Run {
    public static List<Map<String, String>> loadData(String filePath) {
        List<Map<String, String>> data = new ArrayList<>();
        try {
            try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                String[] headers = reader.readNext();
                if (headers != null) {
                    String[] line;
                    while ((line = reader.readNext()) != null) {
                        Map<String, String> row = new HashMap<>();
                        for (int i = 0; i < headers.length; i++) {
                            row.put(headers[i], line[i]);
                        }
                        data.add(row);
                    }
                }
            } catch (CsvValidationException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return data;
    }

    public static void main(String[] args) {
        String fileName = "partiya.csv";
        List<Map<String, String>> data = loadData(fileName);

        if (data != null && !data.isEmpty()) {
            TreeNode root = new TreeNode(data, "root", "Клас");
            DisplayTree displayTree = new DisplayTree();
            displayTree.exportToDot(root, "tree.dot");
        }

    }
}
