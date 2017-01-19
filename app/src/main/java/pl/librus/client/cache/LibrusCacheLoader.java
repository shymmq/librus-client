package pl.librus.client.cache;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.List;

import pl.librus.client.api.SchoolWeek;

/**
 * Created by szyme on 13.01.2017.
 */

public class LibrusCacheLoader {
    private final Integer FAIL_NOT_FOUND = 0;
    private final Integer FAIL_EXPIRED = 1;
    private final Context context;

    public LibrusCacheLoader(Context context) {
        this.context = context;
    }

    public void save(LibrusCache cacheObject,String filename) {
        try {
            File file = new File(context.getFilesDir().getAbsolutePath() + "/" + filename);
            if(file.exists()){
                file.delete();
            }
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(cacheObject);
            os.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Promise<LibrusCache, Void, Void> load(final String filename) {
        final Deferred<LibrusCache, Void, Void> deferred = new DeferredObject<>();

        AsyncManager.runBackgroundTask(new TaskRunnable() {
            @Override
            public Object doLongOperation(Object o) throws InterruptedException {
                try {
                    FileInputStream fis = context.openFileInput(filename);
                    ObjectInputStream is = new ObjectInputStream(fis);
                    LibrusCache cacheObject = (LibrusCache) is.readObject();
                    is.close();
                    fis.close();
                    deferred.resolve(cacheObject);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    deferred.reject(null);
                }
                return null;
            }
        });

        return deferred.promise();
    }


}
