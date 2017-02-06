package pl.librus.client.sql;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.collect.Lists;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.requery.Persistable;
import pl.librus.client.LibrusUtils;
import pl.librus.client.api.APIClient;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceCategory;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.EventCategory;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.GradeCategory;
import pl.librus.client.datamodel.GradeComment;
import pl.librus.client.datamodel.JsonLesson;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.LessonType;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.datamodel.Timetable;
import pl.librus.client.timetable.TimetableUtils;
import pl.librus.client.ui.MainApplication;

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

    public void updateAll() {
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
        tasks.add(updateObject("/LuckyNumbers", "LuckyNumber", LuckyNumber.class));
        tasks.add(updateAccount());
        tasks.add(updateNearestTimetables());
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
            }
        }).fail(new FailCallback<OneReject>() {
            @Override
            public void onFail(OneReject result) {
                LibrusUtils.logError(result.toString());
            }
        });
    }

    private Promise updateNearestTimetables() {
        List<LocalDate> weekStarts = TimetableUtils.getNextFullWeekStarts(LocalDate.now());
        List<Promise> tasks = new ArrayList<>(weekStarts.size());
        for (LocalDate weekStart : weekStarts) {
            tasks.add(updateTimetable(weekStart));
        }
        return new DefaultDeferredManager().when(tasks.toArray(new Promise[tasks.size()]));
    }

    private Promise<List<Lesson>, Void, Void> updateTimetable(LocalDate weekStart){
        final Deferred<List<Lesson>, Void, Void> deferred = new DeferredObject<>();

        client.getTimetable(weekStart).done(new DoneCallback<Timetable>() {
            @Override
            public void onDone(Timetable timetable) {
                List<Lesson> result = Lists.newArrayList();
                for (Map.Entry<LocalDate, List<List<JsonLesson>>> e: timetable.entrySet()) {
                    LocalDate date = e.getKey();
                    for(List<JsonLesson> list : e.getValue()){
                        if(list.size() > 0) {
                            Lesson l = list.get(0).convert(date);
                            result.add(l);
                            MainApplication.getData()
                                    .upsert(l);
                        }
                    }

                }
                deferred.resolve(result);
            }
        });
        return deferred.promise();
    }

    private Promise updateAccount() {
        return client.getObject("/Me", "Me", Me.class).done(new DoneCallback<Me>() {
            @Override
            public void onDone(Me result) {
            MainApplication.getData().upsert(result.account());
            }
        });
    }

    private <T extends Persistable> Promise updateList(final String endpoint, String topLevelName, final Class<T> clazz) {
        return client.getList(endpoint, topLevelName, clazz).done(new DoneCallback<List<T>>() {
            @Override
            public void onDone(final List<T> result) {
                LibrusUtils.log("upserting: " +  endpoint);
                MainApplication.getData().upsert(result);
            }
        });
    }

    private <T extends Persistable> Promise updateObject(final String endpoint, String topLevelName, final Class<T> clazz) {
        return client.getObject(endpoint, topLevelName, clazz).done(new DoneCallback<T>() {
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

    public interface OnUpdateCompleteListener {
        void onUpdateComplete();
    }

    public Promise<List<Lesson>, Void, Void> getLessonsForWeek(LocalDate weekStart) {
        List<Lesson> cached = MainApplication.getData()
                .select(Lesson.class)
                .where(LessonType.DATE.gte(weekStart))
                .and(LessonType.DATE.lt(weekStart.plusWeeks(1)))
                .get()
                .toList();

        if(cached.isEmpty()) {
            return updateTimetable(weekStart);
        }else {
            return new DeferredObject<List<Lesson>, Void, Void>().resolve(cached).promise();

        }
    }
}
