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
import org.jdeferred.android.AndroidDeferredManager;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.librus.client.timetable.TimetableUtils;

public class LibrusCache implements Serializable {
    private static final String TAG = "librus-client-log";
    private final long timestamp;
    final transient private Context context;
    //put additional data types here
    private LibrusAccount account;
    private Timetable timetable;
    private List<Announcement> announcements;
    private Set<Integer> readAnnouncements;
    private LuckyNumber luckyNumber;
    private List<Event> events;

    public LibrusCache(Context context) {
        this.context = context;
        this.timestamp = System.currentTimeMillis();
        readAnnouncements = new HashSet<>();
    }

    static public Promise<LibrusCache, Object, Object> load(final Context context) {
        final Deferred<LibrusCache, Object, Object> deferred = new DeferredObject<>();

        AsyncManager.runBackgroundTask(new TaskRunnable<Object, LibrusCache, Object>() {
            @Override
            public LibrusCache doLongOperation(Object o) throws InterruptedException {
                try {
                    FileInputStream fis = context.openFileInput("librus_cache");
                    ObjectInputStream is = new ObjectInputStream(fis);
                    LibrusCache state = (LibrusCache) is.readObject();
                    is.close();
                    fis.close();
                    return state;
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
            public void callback(LibrusCache librusCache) {
                if (librusCache != null) {
                    Log.d(TAG, "callback: File loaded successfully");
                    deferred.resolve(librusCache);
                }
            }
        });
        return deferred.promise();
    }

    public Promise<Object, Object, Object> update() {
        Log.d(TAG, "update: Starting update");
        final Deferred<Object, Object, Object> deferred = new DeferredObject<>();
        List<Promise> tasks = new ArrayList<>();
        APIClient client = new APIClient(context);
        tasks.add(client.getTimetable(TimetableUtils.getWeekStart(), TimetableUtils.getWeekStart().plusWeeks(1)).done(new DoneCallback<Timetable>() {
            @Override
            public void onDone(Timetable result) {
                setTimetable(result);
            }
        }));
        tasks.add(client.getAccount().done(new DoneCallback<LibrusAccount>() {
            @Override
            public void onDone(LibrusAccount result) {
                setAccount(result);
            }
        }));
        tasks.add(client.getAnnouncements().done(new DoneCallback<List<Announcement>>() {
            @Override
            public void onDone(List<Announcement> result) {
                setAnnouncements(result);
            }
        }));
        tasks.add(client.getEvents().done(new DoneCallback<List<Event>>() {
            @Override
            public void onDone(List<Event> result) {
                setEvents(result);
            }
        }));
        tasks.add(client.getLuckyNumber().done(new DoneCallback<LuckyNumber>() {
            @Override
            public void onDone(LuckyNumber result) {
                setLuckyNumber(result);
            }
        }));

        DeferredManager dm = new AndroidDeferredManager();
        dm.when(tasks.toArray(new Promise[tasks.size()])).done(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults result) {
                save();
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

    private LibrusCache save() {
        try {
            FileOutputStream fos = context.openFileOutput("librus_cache", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
            return this;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    private void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public Set<Integer> getReadAnnouncements() {
        return readAnnouncements;
    }

    public long getTimestamp() {
        return timestamp;
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
}
