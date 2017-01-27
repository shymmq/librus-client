package pl.librus.client.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MultipleResults;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.librus.client.sql.LibrusDbContract;
import pl.librus.client.sql.LibrusDbContract.Account;
import pl.librus.client.sql.LibrusDbContract.Grades;
import pl.librus.client.sql.LibrusDbContract.Lessons;
import pl.librus.client.sql.LibrusDbContract.Subjects;
import pl.librus.client.sql.LibrusDbContract.Teachers;
import pl.librus.client.sql.LibrusDbHelper;

/**
 * Created by szyme on 27.01.2017.
 */

public class LibrusUpdateService {
    private final Context context;
    List<OnUpdateCompleteListener> listeners = new ArrayList<>();
    private boolean loading = false;

    public LibrusUpdateService(Context context) {
        this.context = context;
    }

    public Promise<Void, Void, Void> updateAll() {
        final Deferred<Void, Void, Void> deferred = new DeferredObject<>();
        APIClient client = new APIClient(context);
        LibrusDbHelper dbHelper = new LibrusDbHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Promise> tasks = new ArrayList<>();
        loading = true;
        tasks.add(client.getSchoolWeek(LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY)).done(new DoneCallback<SchoolWeek>() {
            @Override
            public void onDone(SchoolWeek result) {
                db.delete(Lessons.TABLE_NAME, null, null);
                for (SchoolDay schoolDay : result.getSchoolDays()) {
                    LocalDate date = schoolDay.getDate();
                    long dateMillis = date.toDateTimeAtStartOfDay().getMillis();
                    for (Map.Entry<Integer, Lesson> entry : schoolDay.getLessons().entrySet()) {
                        Lesson lesson = entry.getValue();
                        ContentValues values = new ContentValues();
                        values.put(Lessons.COLUMN_NAME_DATE, dateMillis);
                        values.put(Lessons.COLUMN_NAME_ID, lesson.getId());
                        values.put(Lessons.COLUMN_NAME_LESSON_NUMBER, lesson.getLessonNumber());
                        values.put(Lessons.COLUMN_NAME_SUBJECT_ID, lesson.getSubject().getId());
                        values.put(Lessons.COLUMN_NAME_SUBJECT_NAME, lesson.getSubject().getName());
                        values.put(Lessons.COLUMN_NAME_TEACHER_ID, lesson.getTeacher().getId());
                        values.put(Lessons.COLUMN_NAME_TEACHER_FIRST_NAME, lesson.getTeacher().getFirstName());
                        values.put(Lessons.COLUMN_NAME_TEACHER_LAST_NAME, lesson.getTeacher().getLastName());
                        values.put(Lessons.COLUMN_NAME_SUBSTITUTION, lesson.isSubstitutionClass() ? 1 : 0);
                        values.put(Lessons.COLUMN_NAME_CANCELED, lesson.isCanceled() ? 1 : 0);
                        values.put(Lessons.COLUMN_NAME_ORG_SUBJECT_ID, lesson.getOrgSubjectId());
                        values.put(Lessons.COLUMN_NAME_ORG_TEACHER_ID, lesson.getOrgTeacherId());
                        db.insert(Lessons.TABLE_NAME, null, values);
                    }
                }
            }
        }));
        tasks.add(client.getAccount().done(new DoneCallback<LibrusAccount>() {
            @Override
            public void onDone(LibrusAccount result) {
                db.delete(Account.TABLE_NAME, null, null);
                ContentValues values = new ContentValues();
                values.put(Account.COLUMN_NAME_ID, result.getId());
                values.put(Account.COLUMN_NAME_CLASS_ID, result.getClassId());
                values.put(Account.COLUMN_NAME_FIRST_NAME, result.getFirstName());
                values.put(Account.COLUMN_NAME_LAST_NAME, result.getLastName());
                values.put(Account.COLUMN_NAME_USERNAME, result.getLogin());
                values.put(Account.COLUMN_NAME_EMAIL, result.getEmail());
                db.insert(Account.TABLE_NAME, null, values);
            }
        }));
        tasks.add(client.getTeachers().done(new DoneCallback<ArrayList<Teacher>>() {
            @Override
            public void onDone(ArrayList<Teacher> result) {
                db.delete(Teachers.TABLE_NAME, null, null);
                for (Teacher t : result) {
                    ContentValues values = new ContentValues();
                    values.put(Teachers.COLUMN_NAME_ID, t.getId());
                    values.put(Teachers.COLUMN_NAME_FIRST_NAME, t.getFirstName());
                    values.put(Teachers.COLUMN_NAME_LAST_NAME, t.getLastName());
                    db.insert(Teachers.TABLE_NAME, null, values);
                }
            }
        }));
        tasks.add(client.getSubjects().done(new DoneCallback<ArrayList<Subject>>() {
            @Override
            public void onDone(ArrayList<Subject> result) {
                db.delete(Subjects.TABLE_NAME, null, null);
                for (Subject s : result) {
                    ContentValues values = new ContentValues();
                    values.put(Subjects.COLUMN_NAME_ID, s.getId());
                    values.put(Subjects.COLUMN_NAME_NAME, s.getName());
                    db.insert(Subjects.TABLE_NAME, null, values);
                }
            }
        }));
        tasks.add(client.getGrades().done(new DoneCallback<List<Grade>>() {
            @Override
            public void onDone(List<Grade> result) {
                db.delete(Grades.TABLE_NAME, null, null);
                for (Grade g : result) {
                    ContentValues values = new ContentValues();
                    values.put(Grades.COLUMN_NAME_ID, g.getId());
                    values.put(Grades.COLUMN_NAME_GRADE, g.getGrade());
                    values.put(Grades.COLUMN_NAME_SUBJECT_ID, g.getSubjectId());
                    values.put(Grades.COLUMN_NAME_LESSON_ID, g.getLessonId());
                    values.put(Grades.COLUMN_NAME_CATEGORY_ID, g.getCategoryId());
                    values.put(Grades.COLUMN_NAME_COMMENT_ID, g.getCommentId());
                    values.put(Grades.COLUMN_NAME_ADDED_BY_ID, g.getAddedById());
                    values.put(Grades.COLUMN_NAME_SEMESTER, g.getSemester());
                    values.put(Grades.COLUMN_NAME_DATE, g.getDate().toDateTimeAtStartOfDay().getMillis());
                    values.put(Grades.COLUMN_NAME_ADD_DATE, g.getAddDate().toDateTime().getMillis());
                    values.put(Grades.COLUMN_NAME_TYPE, g.getType().ordinal());
                    db.insert(Grades.TABLE_NAME, null, values);
                }
            }
        }));
        tasks.add(client.getGradeCategories().done(new DoneCallback<ArrayList<GradeCategory>>() {
            @Override
            public void onDone(ArrayList<GradeCategory> result) {
                db.delete(LibrusDbContract.GradeCategories.TABLE_NAME, null, null);
                for (GradeCategory gc : result) {
                    ContentValues values = new ContentValues();
                    values.put(LibrusDbContract.GradeCategories.COLUMN_NAME_ID, gc.getId());
                    values.put(LibrusDbContract.GradeCategories.COLUMN_NAME_NAME, gc.getName());
                    values.put(LibrusDbContract.GradeCategories.COLUMN_NAME_WEIGHT, gc.getWeight());
                    db.insert(LibrusDbContract.GradeCategories.TABLE_NAME, null, values);
                }
            }
        }));
        tasks.add(client.getLuckyNumber().done(new DoneCallback<LuckyNumber>() {
            @Override
            public void onDone(LuckyNumber result) {
                ContentValues values = new ContentValues();
                values.put(LibrusDbContract.LuckyNumbers.COLUMN_NAME_DATE, result.getLuckyNumberDay().toDateTimeAtStartOfDay().getMillis());
                values.put(LibrusDbContract.LuckyNumbers.COLUMN_NAME_LUCKY_NUMBER, result.getLuckyNumber());
                db.insert(LibrusDbContract.LuckyNumbers.TABLE_NAME, null, values);
            }
        }));
        new DefaultDeferredManager().when(tasks.toArray(new Promise[tasks.size()])).done(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults result) {
                loading = false;
                for (OnUpdateCompleteListener listener : listeners) {
                    listener.run();
                }
                deferred.resolve(null);
            }
        });
        return deferred.promise();
    }

    public void addListener(OnUpdateCompleteListener listener) {
        listeners.add(listener);
    }

    public boolean isLoading() {
        return loading;
    }

    public interface OnUpdateCompleteListener {
        void run();
    }
}
