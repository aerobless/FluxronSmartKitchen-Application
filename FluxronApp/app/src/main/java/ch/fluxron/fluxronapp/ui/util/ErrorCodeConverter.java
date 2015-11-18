package ch.fluxron.fluxronapp.ui.util;

import android.util.Log;

/**
 * Used to convert a Fluxron encoded error code to human readable data.
 * <p/>
 * Int --> Hex
 * Byte 0: Error Code
 * Byte 1-3: Nof hours when the error happened
 * <p/>
 * Hours are formatted like this: [1-(n-1)].[n]
 */
public class ErrorCodeConverter {

    private ErrorCodeConverter() {
    }

    /**
     * Converts integer containing encoded Fluxron error code to a error code resource string.
     *
     * @param input
     * @return
     */
    public static String convertToErrorCode(int input) {
        String hex = Integer.toHexString(input);
        String result = "e";
        if (hex.length() == 8) {
            result += Integer.toString(Integer.parseInt(hex.substring(0, 2), 16));
        } else {
            int code = Integer.parseInt(hex.substring(0, 1), 16);
            if (code < 10) {
                result += "0";
            }
            result += Integer.toString(code);
        }
        return result;
    }

    /**
     * Converts integer containing encoded Fluxron error code to a time resource string.
     *
     * @param input
     * @return
     */
    public static String convertToTime(int input) {
        String hex = Integer.toHexString(input);
        String result;
        if (hex.length() == 8) {
            result = Integer.toString(Integer.parseInt(hex.substring(2, 8), 16));
        } else if (hex.length() <= 1) {
            result = "000";
        } else {
            result = Integer.toString(Integer.parseInt(hex.substring(1, 7), 16));
        }
        if (result.length() <= 1) {
            result = "00" + result;
        }
        result = result.substring(0, result.length() - 1) + "." + result.substring(result.length() - 1, result.length());
        return result;
    }
}
