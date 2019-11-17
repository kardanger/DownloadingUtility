package com.company;

public class Main {

    public static void main(String[] args) {

        

        CollectionLinks links = new CollectionLinks("/tmp/links");

        int numThreads = 4;

        long startTime = System.currentTimeMillis();

        FileLoader loader = new FileLoader(links, numThreads);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        System.out.println("Total elapsed time, ms " + elapsedTime);
        System.out.println(loader.getTotalFileSize() + " bytes loaded");
    }
}
