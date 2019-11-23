package com.company;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Formatter{

    private Map<String, List<String>> words;
    private static Formatter instance = null;

    private Formatter(){
        words = new HashMap<>();
        instance = null;
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

