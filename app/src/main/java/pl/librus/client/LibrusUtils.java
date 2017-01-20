package pl.librus.client;

import android.content.Context;
import android.util.Log;

import org.jdeferred.Deferred;
import org.jdeferred.DeferredManager;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import pl.librus.client.api.APIClient;
import pl.librus.client.api.SchoolWeek;
import pl.librus.client.cache.LibrusCacheLoader;
import pl.librus.client.cache.TimetableCache;
import pl.librus.client.timetable.TimetableUtils;

import static pl.librus.client.LibrusConstants.*;

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

    public static Promise<Void, Void, Void> update(Context context, boolean persistent) {
        final Deferred<Void, Void, Void> deferred = new DeferredObject<>();
        List<Promise> tasks = new ArrayList<>();
        APIClient client = new APIClient(context);
        final LibrusCacheLoader cacheLoader = new LibrusCacheLoader(context);

        final TimetableCache timetableCache = new TimetableCache(new ArrayList<SchoolWeek>());
        List<LocalDate> weekStarts = TimetableUtils.getNextFullWeekStarts(LocalDate.now());
        for (final LocalDate weekStart : weekStarts) {
            tasks.add(client.getSchoolWeek(weekStart).done(new DoneCallback<SchoolWeek>() {
                @Override
                public void onDone(SchoolWeek result) {
                    timetableCache.addSchoolWeek(result);
                    log("School week " + result.getWeekStart() + " downloaded");
                }
            }));
        }
        DeferredManager dm = new AndroidDeferredManager();
        dm.when(tasks.toArray(new Promise[tasks.size()])).done(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults result) {
                cacheLoader.save(timetableCache, TIMETABLE_CACHE);
                deferred.resolve(null);
            }
        }).fail(new FailCallback<OneReject>() {
            @Override
            public void onFail(OneReject result) {
                deferred.reject(null);
            }
        });

        return deferred.promise();
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
