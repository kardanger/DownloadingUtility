package com.company;

import java.text.MessageFormat;

public class Main {

    public static void main(String[] args) {

        Formatter.getInstance().addWord("файл","файла","файлов");
        Formatter.getInstance().addWord("минуту","минуты","минут");
        Formatter.getInstance().addWord("минута","минуты","минут");
        Formatter.getInstance().addWord("секунду","секунды","секунд");
        Formatter.getInstance().addWord("секунда","секунды","секунд");

        String filepath = args[1];
        String linksFile = args[2];

        CollectionLinks links = new CollectionLinks(linksFile, filepath);
        int numThreads = Integer.parseInt(args[0]);

        long startTime = System.currentTimeMillis();

        FileLoader loader = new FileLoader(links, numThreads);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        double seconds = elapsedTime/1000.0;
        int minutes = (int)seconds/60;

        System.out.println(Formatter.getFilesString(loader.getTotalCountFiles(), loader.getTotalFileSize()));
        System.out.println(Formatter.getTimeString(minutes, seconds));
        System.out.println(Formatter.getLoadAverageSpeed(loader.getLoadedFileSize(), seconds));
    }
}
