package pl.librus.client.cache;

import android.content.Context;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MultipleResults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.librus.client.api.APIClient;
import pl.librus.client.api.Average;
import pl.librus.client.api.Grade;
import pl.librus.client.api.GradeComment;
import pl.librus.client.api.TextGrade;

/**
 * Created by szyme on 21.01.2017.
 */

public class GradeCacheLoader extends AbstractDataLoader<GradeCache, Void> {
    public GradeCacheLoader(Context context) {
        super(context);
    }

    @Override
    protected Promise<GradeCache, GradeCache, GradeCache> getDownloadPromise(APIClient client, Void arg) {
        List<Promise> tasks = new ArrayList<>();
        final List<Grade> grades = new ArrayList<>();
        final List<TextGrade> textGrades = new ArrayList<>();
        final List<Average> averages = new ArrayList<>();
        final List<GradeComment> comments = new ArrayList<>();
        tasks.addAll(Arrays.asList(
                client.getGrades().done(new DoneCallback<List<Grade>>() {
                    @Override
                    public void onDone(List<Grade> result) {
                        grades.addAll(result);
                    }
                }),
                client.getTextGrades().done(new DoneCallback<List<TextGrade>>() {
                    @Override
                    public void onDone(List<TextGrade> result) {
                        textGrades.addAll(result);
                    }
                }),
                client.getAverages().done(new DoneCallback<List<Average>>() {
                    @Override
                    public void onDone(List<Average> result) {
                        averages.addAll(result);
                    }
                }),
                client.getComments().done(new DoneCallback<List<GradeComment>>() {
                    @Override
                    public void onDone(List<GradeComment> result) {
                        comments.addAll(result);
                    }
                })));
        final Deferred<GradeCache, GradeCache, GradeCache> deferred = new DeferredObject<>();
        new DefaultDeferredManager()
                .when(tasks.toArray(new Promise[tasks.size()]))
                .done(new DoneCallback<MultipleResults>() {
                    @Override
                    public void onDone(MultipleResults result) {
                        GradeCache gradeCache = new GradeCache(grades, textGrades, averages, comments);
                        deferred.resolve(gradeCache);
                    }
                });
        return deferred.promise();
    }

    @Override
    protected String getFilename(Void arg) {
        return "grade_cache";
    }
}
