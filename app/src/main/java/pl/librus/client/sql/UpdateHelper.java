package pl.librus.client.sql;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MultipleResults;
import org.joda.time.LocalDate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pl.librus.client.LibrusUtils;
import pl.librus.client.api.APIClient;
import pl.librus.client.api.SchoolDay;
import pl.librus.client.api.SchoolWeek;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.timetable.TimetableUtils;

/**
 * Created by szyme on 31.01.2017.
 */

public class UpdateHelper {
    private final LibrusDbHelper helper;
    private final APIClient client;
    private final Context context;
    private List<Promise> tasks = new ArrayList<>();
    private boolean loading = false;
    private OnUpdateCompleteListener onUpdateCompleteListener;

    public UpdateHelper(Context context) {
        helper = OpenHelperManager.getHelper(context, LibrusDbHelper.class);
        client = new APIClient(context);
        this.context = context;
    }

    public Promise<Void, Void, Void> updateAll() {
        final Deferred<Void, Void, Void> deferred = new DeferredObject<>();
        LibrusUtils.log("Starting update...");
        tasks.clear();
        loading = true;
        final long startTime = System.currentTimeMillis();

        tasks.add(updateList("/Subjects", "Subjects", Subject.class));
        tasks.add(updateList("/Users", "Users", Teacher.class));
        tasks.add(updateObject("/LuckyNumbers", "LuckyNumber", LuckyNumber.class));
        tasks.add(updateAccount());
        tasks.add(updateTimetable());
        new DefaultDeferredManager().when(tasks.toArray(new Promise[tasks.size()])).done(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults result) {
                loading = false;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = preferences.edit();
                long currentTimeMillis = System.currentTimeMillis();
                LibrusUtils.log("Update completed in " + (currentTimeMillis - startTime) + " ms");
                editor.putLong("last_update", currentTimeMillis);
                editor.apply();
                if (onUpdateCompleteListener != null) {         //if there is a listener hooked, run it
                    onUpdateCompleteListener.onUpdateComplete();
                    onUpdateCompleteListener = null;            //reset listener after update is complete
                }

                deferred.resolve(null);
            }
        });
        return deferred.promise();
    }

    private Promise updateTimetable() {
        List<LocalDate> weekStarts = TimetableUtils.getNextFullWeekStarts(LocalDate.now());
        List<Promise> tasks = new ArrayList<>(weekStarts.size());
        for (LocalDate weekStart : weekStarts) {
            tasks.add(client.getSchoolWeek(weekStart).done(new DoneCallback<SchoolWeek>() {
                @Override
                public void onDone(SchoolWeek result) {
                    try {
                        Dao<Lesson, ?> dao = helper.getDao(Lesson.class);
                        for (SchoolDay schoolDay : result.getSchoolDays()) {
                            for (Lesson lesson : schoolDay.getLessons().values()) {
                                dao.createOrUpdate(lesson);
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }));
        }
        return new DefaultDeferredManager().when(tasks.toArray(new Promise[tasks.size()]));
    }

    private Promise updateAccount() {
        return client.getMe().done(new DoneCallback<Me>() {
            @Override
            public void onDone(Me result) {
                try {
                    helper.getLibrusAccountDao().createOrUpdate(result.getAccount());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private <T> Promise updateList(String endpoint, String topLevelName, final Class<T> clazz) {
        return client.getList(endpoint, topLevelName, clazz).done(new DoneCallback<List<T>>() {
            @Override
            public void onDone(List<T> result) {
                try {
                    Dao<T, ?> dao = helper.getDao(clazz);
                    for (T item : result) {
                        dao.createOrUpdate(item);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private <T> Promise updateObject(String endpoint, String topLevelName, final Class<T> clazz) {
        return client.getObject(endpoint, topLevelName, clazz).done(new DoneCallback<T>() {
            @Override
            public void onDone(T result) {
                try {
                    Dao<T, ?> dao = helper.getDao(clazz);
                    dao.createOrUpdate(result);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void setOnUpdateCompleteListener(OnUpdateCompleteListener onUpdateCompleteListener) {
        this.onUpdateCompleteListener = onUpdateCompleteListener;
    }

    public boolean isLoading() {
        return loading;
    }

    public interface OnUpdateCompleteListener {
        void onUpdateComplete();
    }
}
