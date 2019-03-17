package com.example.trungspc.toiecvocab.utils;

public class HelperClass {
    public static String getLevel(int level) {
        switch (level) {
            case 0:
                return "New Word";
            case 1:
            case 2:
            case 3:
                return "Review";
            case 4:
                return "Master";
            default:
                return "";
        }
    }
}
