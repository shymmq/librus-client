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

import pl.librus.client.sql.LibrusDbContract;
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
        db.delete(LibrusDbContract.LessonsTable.TABLE_NAME, null, null);
        client.getSchoolWeek(LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY))
                .done(new DoneCallback<SchoolWeek>() {
                    @Override
                    public void onDone(SchoolWeek result) {
                        for (SchoolDay schoolDay : result.getSchoolDays()) {
                            LocalDate date = schoolDay.getDate();
                            long dateMillis = date.toDateTimeAtStartOfDay().getMillis();
                            for (Map.Entry<Integer, Lesson> entry : schoolDay.getLessons().entrySet()) {
                                Lesson lesson = entry.getValue();
                                ContentValues values = new ContentValues();
                                values.put(LibrusDbContract.LessonsTable.COLUMN_NAME_DATE, dateMillis);
                                values.put(LibrusDbContract.LessonsTable.COLUMN_NAME_ID, lesson.getId());
                                values.put(LibrusDbContract.LessonsTable.COLUMN_NAME_LESSON_NUMBER, lesson.getLessonNumber());
                                values.put(LibrusDbContract.LessonsTable.COLUMN_NAME_SUBJECT_ID, lesson.getSubject().getId());
                                values.put(LibrusDbContract.LessonsTable.COLUMN_NAME_SUBJECT_NAME, lesson.getSubject().getName());
                                values.put(LibrusDbContract.LessonsTable.COLUMN_NAME_TEACHER_ID, lesson.getTeacher().getId());
                                values.put(LibrusDbContract.LessonsTable.COLUMN_NAME_TEACHER_FIRST_NAME, lesson.getTeacher().getFirstName());
                                values.put(LibrusDbContract.LessonsTable.COLUMN_NAME_TEACHER_LAST_NAME, lesson.getTeacher().getLastName());
                                values.put(LibrusDbContract.LessonsTable.COLUMN_NAME_SUBSTITUTION, lesson.isSubstitutionClass() ? 1 : 0);
                                values.put(LibrusDbContract.LessonsTable.COLUMN_NAME_CANCELED, lesson.isCanceled() ? 1 : 0);
                                values.put(LibrusDbContract.LessonsTable.COLUMN_NAME_ORG_SUBJECT_ID, lesson.getOrgSubjectId());
                                values.put(LibrusDbContract.LessonsTable.COLUMN_NAME_ORG_TEACHER_ID, lesson.getOrgTeacherId());
                                db.insert(LibrusDbContract.LessonsTable.TABLE_NAME, null, values);
                            }
                        }
                    }
                });
        return deferred.promise();
    }
}
