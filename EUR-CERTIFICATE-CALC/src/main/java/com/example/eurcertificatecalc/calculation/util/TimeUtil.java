package com.example.eurcertificatecalc.calculation.util;

import org.springframework.stereotype.Service;

public class TimeUtil {

    private static long startTime;
    private static long stopTime;
    private static long totalTime;

    public static void startTimer() {
        startTime = System.nanoTime();
    }

    public static void stopTimer() {
        stopTime = System.nanoTime();
    }

    public static String getTotalTimeForProgram() {
        totalTime = stopTime - startTime;

        return String.valueOf((double) totalTime / 1_000_000_000);
    }
}
