package pl.librus.client.cache;

import android.content.Context;

import org.jdeferred.Promise;

import java.util.ArrayList;

import pl.librus.client.api.APIClient;
import pl.librus.client.api.Teacher;

/**
 * Created by szyme on 22.01.2017.
 */

public class TeacherLoader extends AbstractDataLoader<ArrayList<Teacher>, Void> {
    public TeacherLoader(Context context) {
        super(context);
    }

    @Override
    protected Promise<ArrayList<Teacher>, Void, Void> getDownloadPromise(APIClient client, Void arg) {
        return client.getTeachers();
    }

    @Override
    protected String getFilename(Void arg) {
        return "teacher_cache";
    }
}
