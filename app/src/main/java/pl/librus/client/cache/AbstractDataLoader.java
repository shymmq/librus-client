package pl.librus.client.cache;

import android.content.Context;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import pl.librus.client.api.APIClient;

/**
 * Extend this class for creating your own cache types
 *
 * @param <T> Cache file type
 * @param <A> Optional argument. Set to Void if there are no arguments.
 */

public abstract class AbstractDataLoader<T extends Serializable, A> {

    private final APIClient client;
    private Context context;

    AbstractDataLoader(Context context) {
        this.context = context;
        this.client = new APIClient(context);
    }

    protected abstract Promise<T, ?, ?> getDownloadPromise(APIClient client, A arg);

    protected abstract String getFilename(A arg);

    public Promise<T, ?, ?> download(A arg) {
        return getDownloadPromise(client, arg);    //return promise to getDownloadPromise data
    }

    public Promise<T, ?, ?> loadFromCache(final A arg) {
        final Deferred<T, ?, ?> deferred = new DeferredObject<>();
        //Try to load data from cache
        loadFile(getFilename(arg))
                //success, resolve the promise
                .done(new DoneCallback<T>() {
                    @Override
                    public void onDone(T result) {
                        deferred.resolve(result);
                    }
                })
                //loading from cache failed, start downloading from server
                .fail(new FailCallback<Void>() {
                    @Override
                    public void onFail(Void result) {
                        getDownloadPromise(client, arg)
                                //downloaded, save data to cache and resolve the promise
                                .done(new DoneCallback<T>() {
                                    @Override
                                    public void onDone(T result) {
                                        saveFile(result, getFilename(arg));
                                        deferred.resolve(result);
                                    }
                                });
                    }
                });
        return deferred.promise();
    }

    public Promise<T, T, T> hybridLoad(final A arg) {
        final Deferred<T, T, T> deferred = new DeferredObject<>();

        //load from cache
        loadFile(getFilename(arg)).always(new AlwaysCallback<T, Void>() {
            @Override
            public void onAlways(final Promise.State s, final T resolved, Void rejected) {
                //notify about result
                deferred.notify(s == Promise.State.RESOLVED ? resolved : null);
                //after loading from cache, start getDownloadPromise from server
                getDownloadPromise(client, arg).done(new DoneCallback<T>() {
                    @Override
                    public void onDone(T result) {
                        saveFile(result, getFilename(arg));
                        if (s == Promise.State.RESOLVED) {
                            deferred.resolve(result);
                        } else {
                            deferred.reject(result);
                        }
                    }
                });
            }
        });

        return deferred.promise();

    }

    public Promise<T, T, T> hybridLoad() {
        return hybridLoad(null);
    }

    public Promise<T, ?, ?> loadFromCache() {
        return loadFromCache(null);
    }

    public Promise<T, ?, ?> download() {
        return download(null);
    }

    private Promise<T, Void, Void> loadFile(final String filename) {
        final Deferred<T, Void, Void> deferred = new DeferredObject<>();
        AsyncManager.runBackgroundTask(new TaskRunnable() {
            @Override
            public Object doLongOperation(Object o) throws InterruptedException {
                try {
                    FileInputStream fis = context.openFileInput(filename);
                    ObjectInputStream is = new ObjectInputStream(fis);
                    T cacheObject = (T) is.readObject();
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

    public enum LoadMode {
        /**
         * Loader will try to load data from cache. If it fails, it will getDownloadPromise data from server.
         */
        CACHE,
        /**
         * Loader will skip loading data from cache and getDownloadPromise data from the server
         */
        DOWNLOAD,
        /**
         * Loader will load data from cache. If it succeeds promise is notified with cached data, otherwise it's notified with null.
         * Loader will then getDownloadPromise data from server and finally fire DoneCallBack if cache loading succeeded and FailCallback otherwise.
         */
        HYBRID
    }
}
