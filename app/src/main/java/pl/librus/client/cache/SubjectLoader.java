package pl.librus.client.cache;

import android.content.Context;

import org.jdeferred.Promise;

import java.util.ArrayList;

import pl.librus.client.api.APIClient;
import pl.librus.client.api.Subject;

/**
 * Created by szyme on 22.01.2017.
 */

public class SubjectLoader extends AbstractDataLoader<ArrayList<Subject>, Void> {
    public SubjectLoader(Context context) {
        super(context);
    }

    @Override
    protected Promise<ArrayList<Subject>, ?, ?> getDownloadPromise(APIClient client, Void arg) {
        return client.getSubjects();
    }

    @Override
    protected String getFilename(Void arg) {
        return "subject_cache";
    }
}
