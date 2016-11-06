package pl.librus.client;

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
import java.util.List;

class LibrusCache implements Serializable {
    private static final String TAG = "librus-client-log";
    private long timestamp;
    transient private Context context;
    //put additional data types here
    private LibrusAccount account;
    private Timetable timetable;
    private List<Announcement> announcements;

    private LibrusCache(Context context) {
        this.context = context;
        this.timestamp = System.currentTimeMillis();
    }

    static Promise<LibrusCache, Promise<LibrusCache, Object, Object>, Object> load(final Context context) {
        final Deferred<LibrusCache, Promise<LibrusCache, Object, Object>, Object> deferred = new DeferredObject<>();

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
                    deferred.reject(LibrusCache.update(context));
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

    private static Promise<LibrusCache, Object, Object> update(Context context) {
        Log.d(TAG, "update: Starting update");
        final Deferred<LibrusCache, Object, Object> deferred = new DeferredObject<>();
        List<Promise> tasks = new ArrayList<>();
        final LibrusCache cache = new LibrusCache(context);
        APIClient client = new APIClient(context);
        tasks.add(client.getTimetable(TimetableUtils.getWeekStart(), TimetableUtils.getWeekStart().plusWeeks(1)).done(new DoneCallback<Timetable>() {
            @Override
            public void onDone(Timetable result) {
                cache.setTimetable(result);
            }
        }));
        tasks.add(client.getAccount().done(new DoneCallback<LibrusAccount>() {
            @Override
            public void onDone(LibrusAccount result) {
                cache.setAccount(result);
            }
        }));
        tasks.add(client.getAnnouncements().done(new DoneCallback<List<Announcement>>() {
            @Override
            public void onDone(List<Announcement> result) {
                cache.setAnnouncements(result);
            }
        }));

        DeferredManager dm = new AndroidDeferredManager();
        dm.when(tasks.toArray(new Promise[tasks.size()])).done(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults result) {
                cache.save();
                deferred.resolve(cache);
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

    List<Announcement> getAnnouncements() {
        return announcements;
    }

    private void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public long getTimestamp() {
        return timestamp;
    }

    Timetable getTimetable() {
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
}
