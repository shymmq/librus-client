package pl.librus.client.cache;

import android.content.Context;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import pl.librus.client.api.APIClient;
import pl.librus.client.api.SchoolWeek;

/**
 * Created by szyme on 20.01.2017.
 */

public class DataLoader {
    private final Context context;
    private final APIClient client;

    public DataLoader(Context context) {
        this.context = context;
        client = new APIClient(context);
    }

    public Promise<SchoolWeek, Void, Void> getSchoolWeek(final LocalDate weekStart, boolean loadFromCache) {
        final Deferred<SchoolWeek, Void, Void> deferred = new DeferredObject<>();
        final String filename = "week" + weekStart.toString("xxww");
        if (loadFromCache) {
            loadFile(filename).done(new DoneCallback<Object>() {
                @Override
                public void onDone(Object result) {
                    deferred.resolve((SchoolWeek) result);
                }
            }).fail(new FailCallback<Void>() {
                @Override
                public void onFail(Void result) {
                    client.getSchoolWeek(weekStart).done(new DoneCallback<SchoolWeek>() {
                        @Override
                        public void onDone(SchoolWeek result) {
                            saveFile(result, filename);
                            deferred.resolve(result);
                        }
                    });
                }
            });
        } else {
            client.getSchoolWeek(weekStart).done(new DoneCallback<SchoolWeek>() {
                @Override
                public void onDone(SchoolWeek result) {
                    saveFile(result, filename);
                    deferred.resolve(result);
                }
            });
        }
        return deferred.promise();
    }

    private Promise<Object, Void, Void> loadFile(final String filename) {
        final Deferred<Object, Void, Void> deferred = new DeferredObject<>();
        AsyncManager.runBackgroundTask(new TaskRunnable() {
            @Override
            public Object doLongOperation(Object o) throws InterruptedException {
                try {
                    FileInputStream fis = context.openFileInput(filename);
                    ObjectInputStream is = new ObjectInputStream(fis);
                    Object cacheObject = is.readObject();
                    is.close();
                    fis.close();
                    deferred.resolve(cacheObject);
                } catch (Exception e) {
                    e.printStackTrace();
                    deferred.reject(null);
                }
                return null;
            }
        });
        return deferred.promise();
    }

    private void saveFile(final Serializable file, final String filename) {
        AsyncManager.runBackgroundTask(new TaskRunnable() {
            @Override
            public Object doLongOperation(Object o) throws InterruptedException {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    new File(context.getFilesDir().getAbsolutePath() + "/" + filename).delete();

                    FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(file);
                    os.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}
