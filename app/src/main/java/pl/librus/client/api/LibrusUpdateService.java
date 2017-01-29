package pl.librus.client.api;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

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

import pl.librus.client.LibrusUtils;
import pl.librus.client.sql.LibrusDbContract;
import pl.librus.client.sql.LibrusDbContract.AttendanceCategories;
import pl.librus.client.sql.LibrusDbContract.Attendances;
import pl.librus.client.sql.LibrusDbContract.GradeComments;
import pl.librus.client.sql.LibrusDbContract.Grades;
import pl.librus.client.sql.LibrusDbContract.PlainLessons;
import pl.librus.client.sql.LibrusDbContract.Subjects;
import pl.librus.client.sql.LibrusDbContract.Teachers;
import pl.librus.client.sql.LibrusDbHelper;

/**
 * This class allows to update all data asynchronously and save it to the database
 */

public class LibrusUpdateService {
    private final Context context;
    private List<OnUpdateCompleteListener> onUpdateCompleteListeners = new ArrayList<>();
    private List<OnProgressListener> onProgressListeners = new ArrayList<>();
    private boolean loading = false;
    private int progress = 100;
    private List<Promise> tasks = new ArrayList<>();
    private APIClient client;
    private SQLiteDatabase db;

    public LibrusUpdateService(Context context) {
        this.context = context;
        this.client = new APIClient(context);
        LibrusDbHelper dbHelper = new LibrusDbHelper(context);
        this.db = dbHelper.getReadableDatabase();
    }

    public Promise<Void, Void, Void> updateAll() {
        final Deferred<Void, Void, Void> deferred = new DeferredObject<>();
        LibrusUtils.log("Starting update...");
        tasks.clear();
        loading = true;
        progress = 0;
        final long startTime = System.currentTimeMillis();
        tasks.add(getGrades());
        tasks.add(getGradeCategories());
        tasks.add(getSubjects());
        tasks.add(getLastLuckyNumber());
        tasks.add(getGradeComments());
        tasks.add(getAttendanceCategories());
        tasks.add(getTeachers());
        tasks.add(getSchoolWeek(LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY)));
        tasks.add(getAttendances());
        tasks.add(getPlainLessons());
        tasks.add(getAccount());
        new DefaultDeferredManager().when(tasks.toArray(new Promise[tasks.size()])).done(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults result) {
                loading = false;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = preferences.edit();
                long currentTimeMillis = System.currentTimeMillis();
                LibrusUtils.log("Update completed in " + (currentTimeMillis - startTime) + " ms");
                editor.putLong("last_update", currentTimeMillis);
                editor.apply();
                for (OnUpdateCompleteListener listener : onUpdateCompleteListeners) {
                    listener.onUpdateComplete();
                }
                deferred.resolve(null);
            }
        });
        return deferred.promise();
    }

    private Promise<List<Grade>, ?, ?> getGrades() {
        return client.getGrades().done(new DoneCallback<List<Grade>>() {
            @Override
            public void onDone(List<Grade> result) {
                LibrusUtils.log("Saving " + result.size() + " grades to database");
                db.beginTransaction();
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
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<List<GradeCategory>, ?, ?> getGradeCategories() {
        return client.getGradeCategories().done(new DoneCallback<List<GradeCategory>>() {
            @Override
            public void onDone(List<GradeCategory> result) {
                LibrusUtils.log("Saving " + result.size() + " grade categories to database");
                db.beginTransaction();
                db.delete(LibrusDbContract.GradeCategories.TABLE_NAME, null, null);
                for (GradeCategory gc : result) {
                    ContentValues values = new ContentValues();
                    values.put(LibrusDbContract.GradeCategories.COLUMN_NAME_ID, gc.getId());
                    values.put(LibrusDbContract.GradeCategories.COLUMN_NAME_NAME, gc.getName());
                    values.put(LibrusDbContract.GradeCategories.COLUMN_NAME_WEIGHT, gc.getWeight());
                    db.insert(LibrusDbContract.GradeCategories.TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<List<Subject>, ?, ?> getSubjects() {
        return client.getSubjects().done(new DoneCallback<List<Subject>>() {
            @Override
            public void onDone(List<Subject> result) {
                LibrusUtils.log("Saving " + result.size() + " subjects to database");
                db.beginTransaction();
                db.delete(Subjects.TABLE_NAME, null, null);
                for (Subject s : result) {
                    ContentValues values = new ContentValues();
                    values.put(Subjects.COLUMN_NAME_ID, s.getId());
                    values.put(Subjects.COLUMN_NAME_NAME, s.getName());
                    db.insert(Subjects.TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<LuckyNumber, ?, ?> getLastLuckyNumber() {
        return client.getLuckyNumber().done(new DoneCallback<LuckyNumber>() {
            @Override
            public void onDone(LuckyNumber result) {
                LibrusUtils.log("Saving " + result.getLuckyNumber() + " lucky number to database");
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(LibrusDbContract.LuckyNumbers.COLUMN_NAME_DATE, result.getLuckyNumberDay().toDateTimeAtStartOfDay().getMillis());
                values.put(LibrusDbContract.LuckyNumbers.COLUMN_NAME_LUCKY_NUMBER, result.getLuckyNumber());
                db.replace(LibrusDbContract.LuckyNumbers.TABLE_NAME, null, values);
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<List<GradeComment>, ?, ?> getGradeComments() {
        return client.getComments().done(new DoneCallback<List<GradeComment>>() {
            @Override
            public void onDone(List<GradeComment> result) {
                LibrusUtils.log("Saving " + result.size() + " grade comments to database");
                db.beginTransaction();
                db.delete(GradeComments.TABLE_NAME, null, null);
                for (GradeComment gc : result) {
                    ContentValues values = new ContentValues();
                    values.put(GradeComments.COLUMN_NAME_ID, gc.getId());
                    values.put(GradeComments.COLUMN_NAME_ADDED_BY_ID, gc.getAddedById());
                    values.put(GradeComments.COLUMN_NAME_GRADE_ID, gc.getGradeId());
                    values.put(GradeComments.COLUMN_NAME_TEXT, gc.getText());
                    db.insert(GradeComments.TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<List<AttendanceCategory>, ?, ?> getAttendanceCategories() {
        return client.getAttendanceCategories().done(new DoneCallback<List<AttendanceCategory>>() {
            @Override
            public void onDone(List<AttendanceCategory> result) {
                LibrusUtils.log("Saving " + result.size() + " attendance categories to database");
                db.beginTransaction();
                db.delete(AttendanceCategories.TABLE_NAME, null, null);
                for (AttendanceCategory ac : result) {
                    ContentValues values = new ContentValues();
                    values.put(AttendanceCategories.COLUMN_NAME_ID, ac.getId());
                    values.put(AttendanceCategories.COLUMN_NAME_NAME, ac.getName());
                    values.put(AttendanceCategories.COLUMN_NAME_SHORT_NAME, ac.getShortName());
                    values.put(AttendanceCategories.COLUMN_NAME_COLOR, ac.getColorRGB());
                    values.put(AttendanceCategories.COLUMN_NAME_STANDARD, ac.isStandard() ? 1 : 0);
                    values.put(AttendanceCategories.COLUMN_NAME_PRESENCE, ac.isPresenceKind() ? 1 : 0);
                    values.put(AttendanceCategories.COLUMN_NAME_ORDER, ac.getOrder());
                    LibrusUtils.log("Attendance category: " + values.toString());
                    db.insert(AttendanceCategories.TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<List<Teacher>, ?, ?> getTeachers() {
        return client.getTeachers().done(new DoneCallback<List<Teacher>>() {
            @Override
            public void onDone(List<Teacher> result) {
                LibrusUtils.log("Saving " + result.size() + " teachers to database");
                db.beginTransaction();
                db.delete(Teachers.TABLE_NAME, null, null);
                for (Teacher t : result) {
                    ContentValues values = new ContentValues();
                    values.put(Teachers.COLUMN_NAME_ID, t.getId());
                    values.put(Teachers.COLUMN_NAME_FIRST_NAME, t.getFirstName());
                    values.put(Teachers.COLUMN_NAME_LAST_NAME, t.getLastName());
                    db.insert(Teachers.TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    public Promise<SchoolWeek, ?, ?> getSchoolWeek(final LocalDate weekStart) {
        return client.getSchoolWeek(weekStart).done(new DoneCallback<SchoolWeek>() {
            @Override
            public void onDone(SchoolWeek result) {
                LibrusUtils.log("Saving " + result.getWeekStart() + " school week to database");
                long weekStartMillis = weekStart.toDateTimeAtStartOfDay().getMillis();
                db.beginTransaction();
                for (SchoolDay schoolDay : result.getSchoolDays()) {
                    LocalDate day = schoolDay.getDate();
                    long dayMillis = day.toDateTimeAtStartOfDay().getMillis();
                    for (Map.Entry<Integer, Lesson> entry : schoolDay.getLessons().entrySet()) {
                        Lesson lesson = entry.getValue();
                        ContentValues values = new ContentValues();
                        values.put(LibrusDbContract.TimetableLessons.COLUMN_NAME_DATE, dayMillis);
                        values.put(LibrusDbContract.TimetableLessons.COLUMN_NAME_ID, lesson.getId());
                        values.put(LibrusDbContract.TimetableLessons.COLUMN_NAME_UNIQUE_ID, lesson.getUniqueId());
                        values.put(LibrusDbContract.TimetableLessons.COLUMN_NAME_LESSON_NUMBER, lesson.getLessonNumber());
                        values.put(LibrusDbContract.TimetableLessons.COLUMN_NAME_SUBJECT_ID, lesson.getSubject().getId());
                        values.put(LibrusDbContract.TimetableLessons.COLUMN_NAME_SUBJECT_NAME, lesson.getSubject().getName());
                        values.put(LibrusDbContract.TimetableLessons.COLUMN_NAME_TEACHER_ID, lesson.getTeacher().getId());
                        values.put(LibrusDbContract.TimetableLessons.COLUMN_NAME_TEACHER_FIRST_NAME, lesson.getTeacher().getFirstName());
                        values.put(LibrusDbContract.TimetableLessons.COLUMN_NAME_TEACHER_LAST_NAME, lesson.getTeacher().getLastName());
                        values.put(LibrusDbContract.TimetableLessons.COLUMN_NAME_SUBSTITUTION, lesson.isSubstitutionClass() ? 1 : 0);
                        values.put(LibrusDbContract.TimetableLessons.COLUMN_NAME_CANCELED, lesson.isCanceled() ? 1 : 0);
                        values.put(LibrusDbContract.TimetableLessons.COLUMN_NAME_ORG_SUBJECT_ID, lesson.getOrgSubjectId());
                        values.put(LibrusDbContract.TimetableLessons.COLUMN_NAME_ORG_TEACHER_ID, lesson.getOrgTeacherId());
                        db.insert(LibrusDbContract.TimetableLessons.TABLE_NAME, null, values);
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<List<Attendance>, ?, ?> getAttendances() {
        return client.getAttendances().done(new DoneCallback<List<Attendance>>() {
            @Override
            public void onDone(List<Attendance> result) {
                LibrusUtils.log("Saving " + result.size() + " attendances to database");
                db.beginTransaction();
                db.delete(Attendances.TABLE_NAME, null, null);
                for (Attendance a : result) {
                    ContentValues values = new ContentValues();
                    values.put(Attendances.COLUMN_NAME_ID, a.getId());
                    values.put(Attendances.COLUMN_NAME_ADD_DATE, a.getAddDate().toDateTime().getMillis());
                    values.put(Attendances.COLUMN_NAME_ADDED_BY_ID, a.getAddedById());
                    values.put(Attendances.COLUMN_NAME_DATE, a.getDate().toDateTimeAtStartOfDay().getMillis());
                    values.put(Attendances.COLUMN_NAME_LESSON_ID, a.getLessonId());
                    values.put(Attendances.COLUMN_NAME_LESSON_NUMBER, a.getLessonNumber());
                    values.put(Attendances.COLUMN_NAME_SEMESTER, a.getSemesterNumber());
                    values.put(Attendances.COLUMN_NAME_TYPE_ID, a.getTypeId());
                    db.insert(Attendances.TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<List<PlainLesson>, ?, ?> getPlainLessons() {
        return client.getPlainLessons().done(new DoneCallback<List<PlainLesson>>() {
            @Override
            public void onDone(List<PlainLesson> result) {
                LibrusUtils.log("Saving " + result.size() + " lessons to database");
                db.beginTransaction();
                db.delete(PlainLessons.TABLE_NAME, null, null);
                for (PlainLesson pl : result) {
                    ContentValues values = new ContentValues();
                    values.put(PlainLessons.COLUMN_NAME_ID, pl.getId());
                    values.put(PlainLessons.COLUMN_NAME_SUBJECT_ID, pl.getSubjectId());
                    values.put(PlainLessons.COLUMN_NAME_TEACHER_ID, pl.getId());
                    db.insert(PlainLessons.TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<LibrusAccount, ?, ?> getAccount() {
        return client.getAccount().done(new DoneCallback<LibrusAccount>() {
            @Override
            public void onDone(LibrusAccount result) {
                LibrusUtils.log("Saving account to database");
                db.beginTransaction();
                db.delete(LibrusDbContract.Account.TABLE_NAME, null, null);
                ContentValues values = new ContentValues();
                values.put(LibrusDbContract.Account.COLUMN_NAME_ID, result.getId());
                values.put(LibrusDbContract.Account.COLUMN_NAME_CLASS_ID, result.getClassId());
                values.put(LibrusDbContract.Account.COLUMN_NAME_FIRST_NAME, result.getFirstName());
                values.put(LibrusDbContract.Account.COLUMN_NAME_LAST_NAME, result.getLastName());
                values.put(LibrusDbContract.Account.COLUMN_NAME_USERNAME, result.getLogin());
                values.put(LibrusDbContract.Account.COLUMN_NAME_EMAIL, result.getEmail());
                db.insert(LibrusDbContract.Account.TABLE_NAME, null, values);
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private DoneCallback updateProgress = new DoneCallback() {
        @Override
        public void onDone(Object result) {
            progress += 100 / tasks.size();
            for (OnProgressListener listener : onProgressListeners) {
                listener.onProgress(progress);
            }
        }
    };


    public void addOnProgressListener(OnProgressListener listener) {
        onProgressListeners.add(listener);
    }

    public void addOnUpdateCompleteListener(OnUpdateCompleteListener listener) {
        onUpdateCompleteListeners.add(listener);
    }

    public boolean isLoading() {
        return loading;
    }

    public interface OnUpdateCompleteListener {
        void onUpdateComplete();
    }

    public interface OnProgressListener {
        void onProgress(int progress);
    }
}
