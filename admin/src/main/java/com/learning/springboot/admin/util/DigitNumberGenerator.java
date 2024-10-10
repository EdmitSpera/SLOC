package com.learning.springboot.admin.util;

import java.util.Random;

public class DigitNumberGenerator {
    private static final Random random = new Random();
    private static final int MIN_VALUE = 100000;
    private static final int MAX_VALUE = 999999;

    /**
     * 产生六位随机数
     *
     * @return
     */
    public String generateSixDigitNumber() {
        return String.valueOf(random.nextInt(MAX_VALUE - MIN_VALUE + 1) + MIN_VALUE);
    }
}
