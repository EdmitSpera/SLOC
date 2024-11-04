package com.learning.springboot.admin.util;

import java.time.LocalDate;
import java.time.Month;

public class SemesterUtil {

    /**
     * 根据当前日期判断当前学年和学期
     * @return 当前学期信息，如"2024秋季学期"
     */
    public static String getCurrentSemester() {
        LocalDate now = LocalDate.now();  // 获取当前日期
        int year = now.getYear();         // 当前年份
        Month month = now.getMonth();     // 当前月份
        int dayOfMonth = now.getDayOfMonth();  // 当前日期的天数

        if (month == Month.AUGUST && dayOfMonth >= 30 || month == Month.SEPTEMBER || month == Month.OCTOBER ||
                month == Month.NOVEMBER || month == Month.DECEMBER || month == Month.JANUARY ||
                (month == Month.FEBRUARY && dayOfMonth <= 20)) {
            // 如果当前日期在 8月30日 到 次年2月20日之间，当前学期为秋季学期
            return year + "秋季学期";
        } else if ((month == Month.FEBRUARY && dayOfMonth >= 21) || month == Month.MARCH || month == Month.APRIL ||
                month == Month.MAY || month == Month.JUNE || month == Month.JULY ||
                (month == Month.AUGUST && dayOfMonth <= 29)) {
            // 如果当前日期在 2月21日 到 8月29日之间，当前学期为春季学期
            return year - 1 + "春季学期";
        }

        // 理论上不应该到这里，因为所有日期都被覆盖
        throw new IllegalStateException("无法确定当前学期");
    }
}
