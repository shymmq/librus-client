package pl.librus.client;

import android.util.Log;

import static pl.librus.client.LibrusConstants.DBG;
import static pl.librus.client.LibrusConstants.TAG;

public class LibrusUtils {

    /**
     * Metoda do okreslania porawnej formy rzeczownika przy liczebniku.
     *
     * @param n liczba (n>=0)
     * @param s forma mianownika liczby pojedynczej
     * @param m forma mianownika liczby mnogiej
     * @param d forma dopelniacza liczby mnogiej
     * @return poprawna forma rzeczownika dla danej liczby (bez liczebika)
     */
    public static String getPluralForm(int n, String s, String m, String d) {
        if (n == 0)
            return d;
        else if (n == 1)
            return s;
        else if (n > 1 && (n % 10 == 2 || n % 10 == 3 || n % 10 == 4) && n % 100 / 10 != 1)
            return m;
        else if (n > 1 && ((n % 10 != 2 || n % 10 != 3 || n % 10 != 4) || n % 100 / 10 == 1))
            return d;
        else return m;
    }

    public static void log(String s, int level, boolean trim) {
        if (DBG) {
            if (trim) {
                Log.println(level, TAG, s);
            } else {
                final int chunkSize = 1000;
                if (s.length() > chunkSize) log("Splitting log into chunks. Length: " + s.length());
                for (int i = 0; i < s.length(); i += chunkSize)
                    Log.println(level, TAG, s.substring(i, Math.min(s.length(), i + chunkSize)));
            }
        }
    }

    public static void log(String text) {
        log(text, Log.DEBUG, true);
    }

    public static void log(String text, int level) {
        log(text, level, true);
    }

    public static void log(String text, boolean trim) {
        log(text, Log.DEBUG, trim);
    }
}
