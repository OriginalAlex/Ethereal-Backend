package io.github.originalalex.ethereal.utils;

public class NumberUtils {

    /**
     * There should be no negative values here (excluding losing the game so we will check if the value is greater than 0
     */
    public static double getValue(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
