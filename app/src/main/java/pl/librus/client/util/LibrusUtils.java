package pl.librus.client.util;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Optional;

import io.requery.Persistable;
import pl.librus.client.domain.Identifiable;

import static pl.librus.client.util.LibrusConstants.DBG;
import static pl.librus.client.util.LibrusConstants.TAG;

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

    private static void log(String s, int level, boolean trim) {
        try {
            if (DBG) {
                if (trim) {
                    Log.println(level, TAG, s);
                } else {
                    final int chunkSize = 1000;
                    if (s.length() > chunkSize)
                        log("Splitting logError into chunks. Length: " + s.length());
                    for (int i = 0; i < s.length(); i += chunkSize)
                        Log.println(level, TAG, s.substring(i, Math.min(s.length(), i + chunkSize)));
                }
            }
        } catch (Exception e) {
            System.out.println(s);
        }
    }

    public static void setTextViewValue(View container, TextView textView, Optional<String> optional) {
        if(optional.isPresent()) {
            container.setVisibility(View.VISIBLE);
            textView.setText(optional.get());
        } else {
            container.setVisibility(View.GONE);
        }
    }

    public static void setTextViewValue(TextView textView, Optional<String> optional) {
        setTextViewValue(textView, textView, optional);
    }

    public static void log(String text, Object... params) {
        log(String.format(text, params), Log.DEBUG, true);
    }

    public static void logError(String text) {
        log(text, Log.ERROR, true);
    }

    public static void handleError(Throwable t) {
        logError(t.getMessage());
    }

    public static void log(String text, boolean trim) {
        log(text, Log.DEBUG, trim);
    }

    public static void log(Object... texts) {
        String log = "";
        for (Object text : texts) {
            log += String.valueOf(text) + '\n';
        }
        log(log);
    }

    public static String getClassId(Class<?> clazz) {
        while(!clazz.getSuperclass().equals(Object.class)){
            clazz = clazz.getSuperclass();
        }
        return clazz.getSimpleName();
    }

    public static String getClassId(Object obj) {
        Class<?> clazz = obj.getClass();
        return getClassId(clazz);
    }

    public static SpannableStringBuilder boldText(String text) {
        return boldText(new SpannableStringBuilder(), text);
    }

    public static SpannableStringBuilder boldText(SpannableStringBuilder builder, String text) {
        int start = builder.length();
        builder.append(text);
        builder.setSpan(new StyleSpan(Typeface.BOLD), start, builder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return builder;
    }
}
