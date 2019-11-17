package com.company;

public class Main {

    public static void main(String[] args) {

        String filepath = args[1];
        String linksFile = args[2];

        CollectionLinks links = new CollectionLinks(linksFile, filepath);
        int numThreads = Integer.parseInt(args[0]);

        long startTime = System.currentTimeMillis();

        FileLoader loader = new FileLoader(links, numThreads);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        System.out.println("Total elapsed time, ms " + elapsedTime);
        System.out.println(loader.getTotalFileSize() + " bytes loaded");
    }
}
