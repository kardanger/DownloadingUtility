//Модуль форматирования сообщений загрузчика

package com.company;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Класс единиц измерений данных
 */


class DataUtils{
    public static double typeDataLengthFile(long length){
        long value = Math.round(length/(1024.0*1024.0));
        return (value > 0) ? 1024 * 1024:1024;
    }

    public static String typeNameDataLengthFile(long length){
        long value = Math.round(length/(1024.0*1024.0));
        return (value > 0)? "MB" : "KB";
    }

    public static double typeDataTime(long elapsedTime){
        long value = Math.round(elapsedTime/(1000*60));
        return (value > 0) ? 1000*60:1000;
    }

    public static String typeNameDataTime(long elapsedTime){
        long value = Math.round(elapsedTime/(1000*60));
        return (value > 0)? "минуту" : "секунду";
    }
}

/**
 * Класс форматирования сообщений
 */

class Formatter{

    private Map<String, List<String>> words;
    private static Formatter instance = null;

    private static NumberFormat formatter = new DecimalFormat("#0.0");

    private Formatter(){
        words = new HashMap<>();

        addWord("файл","файла","файлов");
        addWord("минуту","минуты","минут");
        addWord("минута","минуты","минут");
        addWord("секунду","секунды","секунд");
        addWord("секунда","секунды","секунд");
    }

    static  String getStatFileLoadString(String filename, long length, long elapsedTime){
        return MessageFormat.format("Файл {0} загружен: {1} {2} за {3} {4}",
                filename, formatter.format(length/(DataUtils.typeDataLengthFile(length))), DataUtils.typeNameDataLengthFile(length),
                formatter.format(elapsedTime/DataUtils.typeDataTime(elapsedTime)),
                Formatter.getInstance().plurals(elapsedTime/DataUtils.typeDataTime(elapsedTime), DataUtils.typeNameDataTime(elapsedTime)));
    }

    static String getLoadAverageSpeed(long loadedFileSize, double seconds){
        return MessageFormat.format("Средняя скорость: {0} kb/s",
                (formatter.format(loadedFileSize*8/DataUtils.typeDataLengthFile(loadedFileSize)/seconds)));
    }

    static String getFilesString(int totalCountFiles, long totalFileSize){
        return MessageFormat.format("Загружено: {0} {1}, {2} {3}",
                totalCountFiles,
                Formatter.getInstance().plurals(totalCountFiles, "файл"),
                formatter.format(totalFileSize/(DataUtils.typeDataLengthFile(totalFileSize))),
                DataUtils.typeNameDataLengthFile(totalFileSize));
    }

    static String getTimeString(int minutes, double seconds){
        return MessageFormat.format("Время: {0} {1} {2} {3}",
                minutes ,
                Formatter.getInstance().plurals(minutes, "минута"),
                formatter.format(seconds),
                Formatter.getInstance().plurals((int)seconds, "секунда"));
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

    public String plurals(double n, String word){
        n = Math.abs(n) % 100;
        int n1 = (int)n % 10;
        if (n > 10 && n < 20) return words.get(word).get(1);
        if (n1 > 1 && n1 < 5) return words.get(word).get(0);
        if (n1 == 1) return word;
        return words.get(word).get(1);
    }
}

