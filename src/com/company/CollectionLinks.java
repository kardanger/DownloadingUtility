package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Класс URL загружаемого файла
 */
class FileURL {
    private final String fileURL;
    private final List <String> localFilenames;

    public String getFileURL() {
        return fileURL;
    }

    public List <String> getLocalFilename() {
        return localFilenames;
    }

    public FileURL(String fileURL, List <String> localFilenames) {
        this.fileURL = fileURL;
        this.localFilenames = localFilenames;
    }
}

public class CollectionLinks {

    Map<String, List<String>> collection;

    public static FileURL getURL(Map.Entry<String, List<String>> entry ){
        return  new FileURL(entry.getKey(), entry.getValue());
    }

    public Set<Map.Entry<String, List<String>>> getURLs()
    {
        return collection.entrySet();
    }

    public void printDataCollection(){
        for (Map.Entry<String, List<String>> entry : collection.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue().toString());
        }
    }

    CollectionLinks(String filename, String filepath) {

        this.collection = new HashMap<>();

        try {
            String input = new String(Files.readAllBytes(Paths.get(filename)));
            String[] array = input.split("\n");

            for (String s : array) {
                String[] record = s.split(" ");
                collection.computeIfAbsent(record[0], k -> new ArrayList<>()).add(filepath + "/" + record[1]);
                collection.computeIfPresent("name", (key, value) -> {
                    value.add(filepath + "/" + record[1]);
                    return  value;
                });
            }

        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
