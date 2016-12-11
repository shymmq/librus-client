package pl.librus.client.api;

import android.content.Context;
import android.util.Log;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;

import org.jdeferred.Deferred;
import org.jdeferred.DeferredManager;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidAlwaysCallback;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.AndroidDoneCallback;
import org.jdeferred.android.AndroidExecutionScope;
import org.jdeferred.android.AndroidFailCallback;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.librus.client.timetable.TimetableUtils;

public class LibrusData implements Serializable {
    private static final long serialVersionUID = 9103658319690261655L;

    private static final String TAG = "librus-client-log";
    private final long timestamp;
    transient private Context context;


    private Timetable timetable;
    private List<Announcement> announcements;
    private LuckyNumber luckyNumber;
    private List<Event> events;
    private List<Grade> grades;

    //Persistent data:
    private List<Teacher> teachers;
    private List<Subject> subjects;
    private List<EventCategory> eventCategories;
    private List<GradeCategory> gradeCategories;
    private LibrusAccount account;
    private boolean debug = false;

    public LibrusData(Context context) {
        this.context = context;
        this.timestamp = System.currentTimeMillis();
    }

    static public Promise<LibrusData, Object, Object> load(final Context context) {
        final Deferred<LibrusData, Object, Object> deferred = new DeferredObject<>();
        final String cache_filename = "librus_client_cache";

        AsyncManager.runBackgroundTask(new TaskRunnable<Object, LibrusData, Object>() {
            @Override
            public LibrusData doLongOperation(Object o) throws InterruptedException {
                try {
                    FileInputStream fis = context.openFileInput(cache_filename);
                    ObjectInputStream is = new ObjectInputStream(fis);
                    LibrusData cache = (LibrusData) is.readObject();
                    cache.setContext(context);
                    is.close();
                    fis.close();
                    return cache;
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "doLongOperation: File not found.");
                    deferred.reject(null);
                    return null;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void callback(LibrusData librusData) {
                if (librusData != null) {
                    Log.d(TAG, "callback: File loaded successfully");
                    deferred.resolve(librusData);
                }
            }
        });
        return deferred.promise();
    }

    private void log(String text) {
        if (debug) Log.d(TAG, text);
    }

    public Promise<Void, Void, Void> update() {
        Log.d(TAG, "update: Starting update");
        final Deferred<Void, Void, Void> deferred = new DeferredObject<>();
        List<Promise> tasks = new ArrayList<>();
        APIClient client = new APIClient(context);
        tasks.add(client.getTimetable(TimetableUtils.getWeekStart(), TimetableUtils.getWeekStart().plusWeeks(1)).done(new DoneCallback<Timetable>() {
            @Override
            public void onDone(Timetable result) {
                setTimetable(result);
                log("Timetable downloaded");
            }
        }));
        tasks.add(client.getAnnouncements().done(new DoneCallback<List<Announcement>>() {
            @Override
            public void onDone(List<Announcement> result) {
                setAnnouncements(result);
                log("Announcements downloaded");
            }
        }));
        tasks.add(client.getEvents().done(new DoneCallback<List<Event>>() {
            @Override
            public void onDone(List<Event> result) {
                setEvents(result);
                log("Events downloaded");
            }
        }));
        tasks.add(client.getLuckyNumber().done(new DoneCallback<LuckyNumber>() {
            @Override
            public void onDone(LuckyNumber result) {
                setLuckyNumber(result);
                log("LNumber downloaded");
            }
        }));
        tasks.add(client.getGrades().done(new DoneCallback<List<Grade>>() {
            @Override
            public void onDone(List<Grade> result) {
                setGrades(result);
                log("Grades downloaded");
            }
        }));

        DeferredManager dm = new AndroidDeferredManager();
        dm.when(tasks.toArray(new Promise[tasks.size()])).done(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults result) {
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

    public Promise<Void, Void, Void> updatePersistent() {
        Log.d(TAG, "updatePersistent: Starting persistent update");
        final Deferred<Void, Void, Void> deferred = new DeferredObject<>();
        List<Promise> tasks = new ArrayList<>();
        APIClient client = new APIClient(context);

        tasks.add(update());

        //Persistent data:
        tasks.add(client.getAccount().done(new DoneCallback<LibrusAccount>() {
            @Override
            public void onDone(LibrusAccount result) {
                setAccount(result);
                log("Account downloaded");

            }
        }));
        tasks.add(client.getTeachers().done(new DoneCallback<List<Teacher>>() {
            @Override
            public void onDone(List<Teacher> result) {
                setTeachers(result);
                log("Teachers downloaded");

            }
        }));
        tasks.add(client.getSubjects().done(new DoneCallback<List<Subject>>() {
            @Override
            public void onDone(List<Subject> result) {
                setSubjects(result);
                log("Subjects downloaded");

            }
        }));
        tasks.add(client.getEventCategories().done(new DoneCallback<List<EventCategory>>() {
            @Override
            public void onDone(List<EventCategory> result) {
                setEventCategories(result);
                log("EventCat downloaded");

            }
        }));
        tasks.add(client.getGradeCategories().done(new DoneCallback<List<GradeCategory>>() {
            @Override
            public void onDone(List<GradeCategory> result) {
                setGradeCategories(result);
                log("GradeCat downlaoded");
            }
        }));

        DeferredManager dm = new AndroidDeferredManager();
        dm.when(tasks.toArray(new Promise[tasks.size()])).done(new AndroidDoneCallback<MultipleResults>() {
            @Override
            public AndroidExecutionScope getExecutionScope() {
                return null;
            }

            @Override
            public void onDone(MultipleResults result) {
                save();
                Log.d(TAG, "onDone: Persistent update done");
                deferred.resolve(null);
            }
        }).fail(new AndroidFailCallback<OneReject>() {
            @Override
            public AndroidExecutionScope getExecutionScope() {
                return null;
            }

            @Override
            public void onFail(OneReject result) {
                Log.d(TAG, "onFail: Persistent update failed " + result.toString());
                deferred.reject(null);
            }
        }).always(new AndroidAlwaysCallback<MultipleResults, OneReject>() {
            @Override
            public void onAlways(Promise.State state, MultipleResults resolved, OneReject rejected) {
                Log.d(TAG, "updatePersistent: all tasks completed.");
            }

            @Override
            public AndroidExecutionScope getExecutionScope() {
                return null;
            }
        });

        return deferred.promise();
    }

    private void save() {
        try {
            String cache_filename = "librus_client_cache";
            FileOutputStream fos = context.openFileOutput(cache_filename, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    private void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public Timetable getTimetable() {
        return timetable;
    }

    private void setTimetable(Timetable timetable) {
        this.timetable = timetable;
    }

    public LibrusAccount getAccount() {
        return account;
    }

    private void setAccount(LibrusAccount account) {
        this.account = account;
    }

    public LuckyNumber getLuckyNumber() {
        return luckyNumber;
    }

    private void setLuckyNumber(LuckyNumber luckyNumber) {
        this.luckyNumber = luckyNumber;
    }

    public List<Event> getEvents() {
        return events;
    }

    private void setEvents(List<Event> events) {
        this.events = events;
    }

    private void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    private void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    private void setEventCategories(List<EventCategory> eventCategories) {
        this.eventCategories = eventCategories;
    }

    public Map<String, Teacher> getTeacherMap() {
        Map<String, Teacher> res = new HashMap<>();
        for (Teacher t : teachers) {
            res.put(t.getId(), t);
        }
        return res;
    }

    public Map<String, Subject> getSubjectMap() {
        Map<String, Subject> res = new HashMap<>();
        for (Subject s : subjects) {
            res.put(s.getId(), s);
        }
        return res;
    }

    public Map<String, EventCategory> getEventCategoriesMap() {
        Map<String, EventCategory> res = new HashMap<>();
        for (EventCategory e : eventCategories) {
            res.put(e.getId(), e);
        }
        return res;
    }

    public Map<String, GradeCategory> getGradeCategoriesMap() {
        Map<String, GradeCategory> res = new HashMap<>();
        for (GradeCategory g : gradeCategories) {
            res.put(g.getId(), g);
        }
        return res;
    }

    public void setGradeCategories(List<GradeCategory> gradeCategories) {
        this.gradeCategories = gradeCategories;
    }

    public Context getContext() {
        return context;
    }

    private void setContext(Context context) {
        this.context = context;
    }
}
