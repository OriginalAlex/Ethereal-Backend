package io.github.originalalex.ethereal.math;

public class HexToDecimal {

    public static int hexToDecimal(String hexString) {
        int value = 0;
        int significance = 1;
        char[] arr = hexString.toCharArray();
        for (int i = arr.length-1; i > -1; i--) {
            value += (arr[i] - '0') * significance;
            significance <<= 4;
        }
        return value;
    }

}
