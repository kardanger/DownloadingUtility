package com.company;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Formatter{

    private Map<String, List<String>> words;
    private static Formatter instance = null;

    private final static double kB = 1024;
    private final static double kb = 1000;

    private Formatter(){
        words = new HashMap<>();
        instance = null;
    }

    static  String getStatFileLoadString(String filename, long length, long elapsedTime){
        double seconds = elapsedTime/1000.0;
        return MessageFormat.format("Файл {0} загружен: {1} MB за {2} {3}",
                filename, length/(kB*kB), seconds, Formatter.getInstance().plurals((int)seconds, "секунду"));
    }

    static String getLoadAverageSpeed(long loadedFileSize, double seconds){
        return MessageFormat.format("Средняя скорость: {0} kb/s",
                (loadedFileSize*8/kb)/seconds);
    }

    static String getFilesString(int totalCountFiles, long totalFileSize){

        return MessageFormat.format("Загружено: {0} {1}, {2} MB",
                totalCountFiles,
                Formatter.getInstance().plurals(totalCountFiles, "файл"),
                totalFileSize/(kB*kB));
    }

    static String getTimeString(int minutes, double seconds){
        return MessageFormat.format("Время: {0} {1} {2} {3}",
                minutes ,
                Formatter.getInstance().plurals((int)minutes, "минуту"),
                seconds,
                Formatter.getInstance().plurals((int)seconds, "секунду"));
    }

    public static Formatter getInstance() {
        if (instance == null) {
            instance = new Formatter();
        }
        return instance;
    }

    public void addWord(String form1, String form2, String form3){
        List<String> list = Arrays.asList(form2,form3);
        words.put(form1, list);
    }

    public String plurals(int n, String word){
        n = Math.abs(n) % 100;
        int n1 = n % 10;
        if (n > 10 && n < 20) return words.get(word).get(1);
        if (n1 > 1 && n1 < 5) return words.get(word).get(0);
        if (n1 == 1) return word;
        return words.get(word).get(1);
    }
}

