package pl.librus.client.sql;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.collect.Lists;

import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MasterProgress;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.requery.Persistable;
import io.requery.query.Scalar;
import pl.librus.client.LibrusUtils;
import pl.librus.client.api.APIClient;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceCategory;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.EventCategory;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.GradeCategory;
import pl.librus.client.datamodel.GradeComment;
import pl.librus.client.datamodel.JsonLesson;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.LessonType;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.Timetable;
import pl.librus.client.timetable.TimetableUtils;
import pl.librus.client.ui.MainApplication;

import static com.google.common.collect.Iterables.toArray;

/**
 * Created by szyme on 31.01.2017.
 * Contains methods to update data from server
 */

public class UpdateHelper {
    private final APIClient client;
    private final Context context;
    private final List<Promise> tasks = new ArrayList<>();
    private boolean loading = false;
    private OnUpdateCompleteListener onUpdateCompleteListener;

    public UpdateHelper(Context context) {
        client = new APIClient(context);
        this.context = context;
    }

    public Promise<MultipleResults, OneReject, MasterProgress> updateAll() {
        LibrusUtils.log("Starting update...");
        tasks.clear();
        loading = true;
        final long startTime = System.currentTimeMillis();

        tasks.add(updateList("/Subjects", "Subjects", Subject.class));
        tasks.add(updateList("/Users", "Users", Teacher.class));
        tasks.add(updateList("/Grades", "Grades", Grade.class));
        tasks.add(updateList("/Grades/Categories", "Categories", GradeCategory.class));
        tasks.add(updateList("/Grades/Comments", "Comments", GradeComment.class));
        tasks.add(updateList("/Lessons", "Lessons", PlainLesson.class));
        tasks.add(updateList("/HomeWorks", "HomeWorks", Event.class));
        tasks.add(updateList("/HomeWorks/Categories", "Categories", EventCategory.class));
        tasks.add(updateList("/Attendances", "Attendances", Attendance.class));
        tasks.add(updateList("/Attendances/Types", "Types", AttendanceCategory.class));
        tasks.add(updateList("/Grades/Averages", "Averages", Average.class));
        tasks.add(updateList("/Colors", "Colors", LibrusColor.class));
        tasks.add(updateObject("/LuckyNumbers", "LuckyNumber", LuckyNumber.class));
        tasks.add(updateAccount());
        tasks.add(updateNearestTimetables());
        return new DefaultDeferredManager().when(toArray(tasks, Promise.class)).then(new DoneCallback<MultipleResults>() {
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
            }
        });
    }

    private Promise updateNearestTimetables() {
        List<LocalDate> weekStarts = TimetableUtils.getNextFullWeekStarts(LocalDate.now());
        List<Promise> tasks = new ArrayList<>(weekStarts.size());
        for (LocalDate weekStart : weekStarts) {
            tasks.add(updateTimetable(weekStart));
        }
        return new DefaultDeferredManager().when(toArray(tasks, Promise.class));
    }

    private Promise<List<Lesson>, Throwable, Void> updateTimetable(LocalDate weekStart) {
        return client.getTimetable(weekStart).then(new DoneFilter<Timetable, List<Lesson>>() {
            @Override
            public List<Lesson> filterDone(Timetable timetable) {
                List<Lesson> result = Lists.newArrayList();
                for (Map.Entry<LocalDate, List<List<JsonLesson>>> e : timetable.entrySet()) {
                    LocalDate date = e.getKey();
                    for (List<JsonLesson> list : e.getValue()) {
                        if (list.size() > 0) {
                            Lesson l = list.get(0).convert(date);
                            result.add(l);
                            MainApplication.getData()
                                    .upsert(l);
                        }
                    }
                }
                return result;
            }
        });
    }

    private Promise updateAccount() {
        return client.getObject("/Me", "Me", Me.class).then(new DoneCallback<Me>() {
            @Override
            public void onDone(Me result) {
                MainApplication.getData().upsert(result.account());
            }
        });
    }

    private <T extends Persistable> Promise updateList(final String endpoint, String topLevelName, final Class<T> clazz) {

        return client.getList(endpoint, topLevelName, clazz).then(new DoneCallback<List<T>>() {
            @Override
            public void onDone(final List<T> result) {
                try {
                    LibrusUtils.log("upserting: " + endpoint + " of size: " + result.size());
                    MainApplication.getData().upsert(result);
                    Scalar<Integer> count = MainApplication.getData().count(clazz).get();
                    LibrusUtils.log("count after: " + count.value());
                } catch (Throwable e) {
                    LibrusUtils.logError(e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

    private <T extends Persistable> Promise updateObject(final String endpoint, String topLevelName, final Class<T> clazz) {
        return client.getObject(endpoint, topLevelName, clazz).then(new DoneCallback<T>() {
            @Override
            public void onDone(T result) {
                LibrusUtils.log("upserting: ", endpoint);
                MainApplication.getData().upsert(result);
            }
        });
    }

    public void setOnUpdateCompleteListener(OnUpdateCompleteListener onUpdateCompleteListener) {
        this.onUpdateCompleteListener = onUpdateCompleteListener;
    }

    public boolean isLoading() {
        return loading;
    }

    public Promise<List<Lesson>, Throwable, Void> getLessonsForWeek(LocalDate weekStart) {
        List<Lesson> cached = MainApplication.getData()
                .select(Lesson.class)
                .where(LessonType.DATE.gte(weekStart))
                .and(LessonType.DATE.lt(weekStart.plusWeeks(1)))
                .get()
                .toList();

        if (cached.isEmpty()) {
            return updateTimetable(weekStart);
        } else {
            return new DeferredObject<List<Lesson>, Throwable, Void>().resolve(cached).promise();

        }
    }

    public interface OnUpdateCompleteListener {
        void onUpdateComplete();
    }
}
