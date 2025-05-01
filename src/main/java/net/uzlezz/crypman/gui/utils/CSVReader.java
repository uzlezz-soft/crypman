package net.uzlezz.crypman.gui.utils;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class CSVReader {
    public static List<List<String>> read(File file) {
        try (var reader = new BufferedReader(new FileReader(file))) {
            return reader.lines()
                    .map(line -> Arrays.asList(line.split(",")))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
