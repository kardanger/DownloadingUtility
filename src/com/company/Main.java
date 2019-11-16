package com.company;

public class Main {

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        FileLoader loader = new FileLoader();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        System.out.println("Total elapsed time, ms " + elapsedTime);
        System.out.println(loader.getTotalFileSize() + " bytes loaded");
    }
}
