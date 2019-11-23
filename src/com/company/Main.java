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

        Formatter.getInstance().addWord("файл","файла","файлов");
        Formatter.getInstance().addWord("минуту","минуты","минут");
        Formatter.getInstance().addWord("секунду","секунды","секунд");

        double seconds = elapsedTime/1000.0;
        int minutes = (int)seconds/60;

        System.out.println("Загружено: " + loader.getTotalCountFiles() + " " + Formatter.getInstance().plurals(loader.getTotalCountFiles(), "файл") + ", " + loader.getTotalFileSize()/1024.0/1024.0 + " MB");
        System.out.println("Время: " + minutes + " " +  Formatter.getInstance().plurals((int)minutes, "минуту")  + " " +  seconds  + " " + Formatter.getInstance().plurals((int)seconds, "секунду"));
        System.out.println("Средняя скорость: " + loader.getLoadedFileSize()*8/1000000.0/seconds + " kb/s");
    }
}
