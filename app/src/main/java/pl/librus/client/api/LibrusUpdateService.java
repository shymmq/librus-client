package pl.librus.client.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.Map;

import pl.librus.client.sql.LibrusDbContract.Account;
import pl.librus.client.sql.LibrusDbContract.Lessons;
import pl.librus.client.sql.LibrusDbHelper;

/**
 * Created by szyme on 27.01.2017.
 */

public class LibrusUpdateService {
    public static Promise<Void, Void, Void> updateAll(Context context) {
        final Deferred<Void, Void, Void> deferred = new DeferredObject<>();
        APIClient client = new APIClient(context);
        LibrusDbHelper dbHelper = new LibrusDbHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Lessons.TABLE_NAME, null, null);
        client.getSchoolWeek(LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY)).done(new DoneCallback<SchoolWeek>() {
            @Override
            public void onDone(SchoolWeek result) {
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
        });
        client.getAccount().done(new DoneCallback<LibrusAccount>() {
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
        });
        return deferred.promise();
    }
}
