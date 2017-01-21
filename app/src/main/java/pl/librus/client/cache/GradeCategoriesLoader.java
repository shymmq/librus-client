package pl.librus.client.cache;

import android.content.Context;

import org.jdeferred.Promise;

import java.util.ArrayList;

import pl.librus.client.api.APIClient;
import pl.librus.client.api.GradeCategory;

/**
 * Created by szyme on 21.01.2017.
 */

public class GradeCategoriesLoader extends AbstractDataLoader<ArrayList<GradeCategory>, Void> {
    public GradeCategoriesLoader(Context context) {
        super(context);
    }

    @Override
    protected Promise<ArrayList<GradeCategory>, Void, Void> getDownloadPromise(APIClient client, Void arg) {
        return client.getGradeCategories();
    }

    @Override
    protected String getFilename(Void arg) {
        return "gradecat_cache";
    }
}
