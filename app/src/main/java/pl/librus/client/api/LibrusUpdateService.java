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
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.librus.client.LibrusUtils;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceType;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.GradeCategory;
import pl.librus.client.datamodel.GradeComment;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.sql.LibrusDbContract;
import pl.librus.client.sql.LibrusDbContract.AttendanceCategories;
import pl.librus.client.sql.LibrusDbContract.Attendances;
import pl.librus.client.sql.LibrusDbContract.GradeCategories;
import pl.librus.client.sql.LibrusDbContract.GradeComments;
import pl.librus.client.sql.LibrusDbContract.Grades;
import pl.librus.client.sql.LibrusDbContract.MeTable;
import pl.librus.client.sql.LibrusDbContract.PlainLessons;
import pl.librus.client.sql.LibrusDbContract.Subjects;
import pl.librus.client.sql.LibrusDbContract.Teachers;
import pl.librus.client.sql.LibrusDbContract.TimetableLessons;
import pl.librus.client.sql.LibrusDbHelper;
import pl.librus.client.timetable.TimetableUtils;

/**
 * This class allows to update all data asynchronously and save it to the database
 */

public class LibrusUpdateService {
    private final Context context;
    private OnUpdateCompleteListener onUpdateCompleteListener = null;
    private OnProgressListener onProgressListener = null;
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
        tasks.add(updateGrades());
        tasks.add(updateGradeCategories());
        tasks.add(updateSubjects());
        tasks.add(updateLastLuckyNumber());
        tasks.add(updateGradeComments());
        tasks.add(updateAttendanceCategories());
        tasks.add(updateTeachers());
        tasks.add(updateSchoolWeeks());
        tasks.add(updateAttendances());
        tasks.add(updatePlainLessons());
        tasks.add(updateMe());
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
                if (onUpdateCompleteListener != null) {         //if there is a listener hooked, run it
                    onUpdateCompleteListener.onUpdateComplete();
                    onUpdateCompleteListener = null;            //reset listener after update is complete
                }

                deferred.resolve(null);
            }
        });
        return deferred.promise();
    }

    private Promise updateSchoolWeeks() {
        List<Promise> tasks = new ArrayList<>();
        List<LocalDate> nextFullWeekStarts = TimetableUtils.getNextFullWeekStarts(LocalDate.now());
        db.delete(TimetableLessons.TABLE_NAME, null, null);
        for (LocalDate weekStart : nextFullWeekStarts) {
            tasks.add(client.getSchoolWeek(weekStart).done(new DoneCallback<SchoolWeek>() {
                @Override
                public void onDone(SchoolWeek result) {
                    LibrusUtils.log("Saving " + result.getWeekStart() + " school week to database");
                    db.beginTransaction();
                    for (SchoolDay schoolDay : result.getSchoolDays()) {
                        LocalDate day = schoolDay.getDate();
                        long dayMillis = day.toDateTimeAtStartOfDay().getMillis();
                        for (Map.Entry<Integer, Lesson> entry : schoolDay.getLessons().entrySet()) {
                            Lesson lesson = entry.getValue();
                            ContentValues values = new ContentValues();
                            values.put(TimetableLessons.COLUMN_NAME_DATE, dayMillis);
                            values.put(TimetableLessons.COLUMN_NAME_ID, lesson.getId());
                            values.put(TimetableLessons.COLUMN_NAME_UNIQUE_ID, lesson.getUniqueId());
                            values.put(TimetableLessons.COLUMN_NAME_LESSON_NUMBER, lesson.getLessonNumber());
                            values.put(TimetableLessons.COLUMN_NAME_START_TIME, lesson.getStartTime().getMillisOfDay());
                            values.put(TimetableLessons.COLUMN_NAME_END_TIME, lesson.getEndTime().getMillisOfDay());
                            values.put(TimetableLessons.COLUMN_NAME_SUBJECT_ID, lesson.getSubject().getId());
                            values.put(TimetableLessons.COLUMN_NAME_SUBJECT_NAME, lesson.getSubject().getName());
                            values.put(TimetableLessons.COLUMN_NAME_TEACHER_ID, lesson.getTeacher().getId());
                            values.put(TimetableLessons.COLUMN_NAME_TEACHER_FIRST_NAME, lesson.getTeacher().getFirstName());
                            values.put(TimetableLessons.COLUMN_NAME_TEACHER_LAST_NAME, lesson.getTeacher().getLastName());
                            values.put(TimetableLessons.COLUMN_NAME_SUBSTITUTION, lesson.isSubstitutionClass() ? 1 : 0);
                            values.put(TimetableLessons.COLUMN_NAME_CANCELED, lesson.isCanceled() ? 1 : 0);
                            values.put(TimetableLessons.COLUMN_NAME_ORG_SUBJECT_ID, lesson.getOrgSubjectId());
                            values.put(TimetableLessons.COLUMN_NAME_ORG_TEACHER_ID, lesson.getOrgTeacherId());
                            db.insert(TimetableLessons.TABLE_NAME, null, values);
                        }
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                }
            }));
        }
        return new DefaultDeferredManager().when(tasks.toArray(new Promise[tasks.size()]));
    }

    private Promise<List<Grade>, ?, ?> updateGrades() {
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
                    values.put(Grades.COLUMN_NAME_STUDENT_ID, g.getStudent().getId());
                    values.put(Grades.COLUMN_NAME_SUBJECT_ID, g.getSubject().getId());
                    values.put(Grades.COLUMN_NAME_LESSON_ID, g.getLesson().getId());
                    values.put(Grades.COLUMN_NAME_CATEGORY_ID, g.getCategory().getId());
                    values.put(Grades.COLUMN_NAME_ADDED_BY_ID, g.getAddedBy().getId());
                    values.put(Grades.COLUMN_NAME_SEMESTER, g.getSemester());
                    values.put(Grades.COLUMN_NAME_DATE, g.getDate().toDateTimeAtStartOfDay().getMillis());
                    values.put(Grades.COLUMN_NAME_ADD_DATE, g.getAddDate().toDateTime().getMillis());
                    values.put(Grades.COLUMN_NAME_SEMESTER_TYPE, g.isSemesterType());
                    values.put(Grades.COLUMN_NAME_SEMESTER_PROPOSITION_TYPE, g.isSemesterPropositionType());
                    values.put(Grades.COLUMN_NAME_FINAL_TYPE, g.isFinalType());
                    values.put(Grades.COLUMN_NAME_FINAL_PROPOSITION_TYPE, g.isFinalPropositionType());
                    db.insert(Grades.TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<List<GradeCategory>, ?, ?> updateGradeCategories() {
        return client.getGradeCategories().done(new DoneCallback<List<GradeCategory>>() {
            @Override
            public void onDone(List<GradeCategory> result) {
                LibrusUtils.log("Saving " + result.size() + " grade categories to database");
                db.beginTransaction();
                db.delete(GradeCategories.TABLE_NAME, null, null);
                for (GradeCategory gc : result) {
                    ContentValues values = new ContentValues();
                    values.put(GradeCategories.COLUMN_NAME_ID, gc.getId());
                    values.put(GradeCategories.COLUMN_NAME_NAME, gc.getName());
                    values.put(GradeCategories.COLUMN_NAME_WEIGHT, gc.getWeight());
                    db.insert(GradeCategories.TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<List<Subject>, ?, ?> updateSubjects() {
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

    private Promise<LuckyNumber, ?, ?> updateLastLuckyNumber() {
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

    private Promise<List<GradeComment>, ?, ?> updateGradeComments() {
        return client.getComments().done(new DoneCallback<List<GradeComment>>() {
            @Override
            public void onDone(List<GradeComment> result) {
                LibrusUtils.log("Saving " + result.size() + " grade comments to database");
                db.beginTransaction();
                db.delete(GradeComments.TABLE_NAME, null, null);
                for (GradeComment gc : result) {
                    ContentValues values = new ContentValues();
                    values.put(GradeComments.COLUMN_NAME_ID, gc.getId());
                    values.put(GradeComments.COLUMN_NAME_ADDED_BY_ID, gc.getAddedBy().getId());
                    values.put(GradeComments.COLUMN_NAME_GRADE_ID, gc.getGrade().getId());
                    values.put(GradeComments.COLUMN_NAME_TEXT, gc.getText());
                    db.insert(GradeComments.TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<List<AttendanceType>, ?, ?> updateAttendanceCategories() {
        return client.getAttendanceTypes().done(new DoneCallback<List<AttendanceType>>() {
            @Override
            public void onDone(List<AttendanceType> result) {
                LibrusUtils.log("Saving " + result.size() + " attendance categories to database");
                db.beginTransaction();
                db.delete(AttendanceCategories.TABLE_NAME, null, null);
                for (AttendanceType ac : result) {
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

    private Promise<List<Teacher>, ?, ?> updateTeachers() {
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
                db.beginTransaction();
                for (SchoolDay schoolDay : result.getSchoolDays()) {
                    LocalDate day = schoolDay.getDate();
                    long dayMillis = day.toDateTimeAtStartOfDay().getMillis();
                    for (Map.Entry<Integer, Lesson> entry : schoolDay.getLessons().entrySet()) {
                        Lesson lesson = entry.getValue();
                        ContentValues values = new ContentValues();
                        values.put(TimetableLessons.COLUMN_NAME_DATE, dayMillis);
                        values.put(TimetableLessons.COLUMN_NAME_ID, lesson.getId());
                        values.put(TimetableLessons.COLUMN_NAME_UNIQUE_ID, lesson.getUniqueId());
                        values.put(TimetableLessons.COLUMN_NAME_LESSON_NUMBER, lesson.getLessonNumber());
                        values.put(TimetableLessons.COLUMN_NAME_START_TIME, lesson.getStartTime().getMillisOfDay());
                        values.put(TimetableLessons.COLUMN_NAME_END_TIME, lesson.getEndTime().getMillisOfDay());
                        values.put(TimetableLessons.COLUMN_NAME_SUBJECT_ID, lesson.getSubject().getId());
                        values.put(TimetableLessons.COLUMN_NAME_SUBJECT_NAME, lesson.getSubject().getName());
                        values.put(TimetableLessons.COLUMN_NAME_TEACHER_ID, lesson.getTeacher().getId());
                        values.put(TimetableLessons.COLUMN_NAME_TEACHER_FIRST_NAME, lesson.getTeacher().getFirstName());
                        values.put(TimetableLessons.COLUMN_NAME_TEACHER_LAST_NAME, lesson.getTeacher().getLastName());
                        values.put(TimetableLessons.COLUMN_NAME_SUBSTITUTION, lesson.isSubstitutionClass() ? 1 : 0);
                        values.put(TimetableLessons.COLUMN_NAME_CANCELED, lesson.isCanceled() ? 1 : 0);
                        values.put(TimetableLessons.COLUMN_NAME_ORG_SUBJECT_ID, lesson.getOrgSubjectId());
                        values.put(TimetableLessons.COLUMN_NAME_ORG_TEACHER_ID, lesson.getOrgTeacherId());
                        db.insert(TimetableLessons.TABLE_NAME, null, values);
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<List<Attendance>, ?, ?> updateAttendances() {
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
                    values.put(Attendances.COLUMN_NAME_ADDED_BY_ID, a.getAddedBy().getId());
                    values.put(Attendances.COLUMN_NAME_DATE, a.getDate().toDateTimeAtStartOfDay().getMillis());
                    values.put(Attendances.COLUMN_NAME_LESSON_ID, a.getLesson().getId());
                    values.put(Attendances.COLUMN_NAME_LESSON_NUMBER, a.getLessonNumber());
                    values.put(Attendances.COLUMN_NAME_SEMESTER, a.getSemester());
                    values.put(Attendances.COLUMN_NAME_TYPE_ID, a.getType().getId());
                    db.insert(Attendances.TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<List<PlainLesson>, ?, ?> updatePlainLessons() {
        return client.getPlainLessons().done(new DoneCallback<List<PlainLesson>>() {
            @Override
            public void onDone(List<PlainLesson> result) {
                LibrusUtils.log("Saving " + result.size() + " lessons to database");
                db.beginTransaction();
                db.delete(PlainLessons.TABLE_NAME, null, null);
                for (PlainLesson pl : result) {
                    ContentValues values = new ContentValues();
                    values.put(PlainLessons.COLUMN_NAME_ID, pl.getId());
                    values.put(PlainLessons.COLUMN_NAME_SUBJECT_ID, pl.getSubject().getId());
                    values.put(PlainLessons.COLUMN_NAME_TEACHER_ID, pl.getId());
                    db.insert(PlainLessons.TABLE_NAME, null, values);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    private Promise<Me, ?, ?> updateMe() {
        return client.getMe().done(new DoneCallback<Me>() {
            @Override
            public void onDone(Me result) {
                LibrusUtils.log("Saving account to database");
                db.beginTransaction();
                db.delete(LibrusDbContract.MeTable.TABLE_NAME, null, null);
                ContentValues values = new ContentValues();
                values.put(MeTable.COLUMN_NAME_ID, result.getAccount().getId());
                values.put(LibrusDbContract.MeTable.COLUMN_NAME_CLASS_ID, result.getLibrusClass().getId());
                values.put(LibrusDbContract.MeTable.COLUMN_NAME_FIRST_NAME, result.getAccount().getFirstName());
                values.put(MeTable.COLUMN_NAME_LAST_NAME, result.getAccount().getLastName());
                values.put(LibrusDbContract.MeTable.COLUMN_NAME_USERNAME, result.getAccount().getLogin());
                values.put(LibrusDbContract.MeTable.COLUMN_NAME_EMAIL, result.getAccount().getEmail());
                db.insert(MeTable.TABLE_NAME, null, values);
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        });
    }

    public void addOnProgressListener(OnProgressListener listener) {
        this.onProgressListener = listener;
    }

    public void setOnUpdateCompleteListener(OnUpdateCompleteListener listener) {
        this.onUpdateCompleteListener = listener;
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
