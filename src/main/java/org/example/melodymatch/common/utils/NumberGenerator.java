package org.example.melodymatch.common.utils;

import java.util.Random;

public class NumberGenerator {

    private static final int min = 100000;
    private static final int max = 999999;

    public static int generateNumber() {
        final var random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

}
