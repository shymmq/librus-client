package pl.librus.client.sql;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import pl.librus.client.LibrusUtils;
import pl.librus.client.R;
import pl.librus.client.api.Attendance;
import pl.librus.client.api.AttendanceCategory;
import pl.librus.client.api.Grade;
import pl.librus.client.api.GradeCategory;
import pl.librus.client.api.GradeComment;
import pl.librus.client.api.Lesson;
import pl.librus.client.api.LuckyNumber;
import pl.librus.client.api.PlainLesson;
import pl.librus.client.api.Subject;
import pl.librus.client.api.Teacher;
import pl.librus.client.sql.LibrusDbContract.AttendanceCategories;
import pl.librus.client.sql.LibrusDbContract.Attendances;
import pl.librus.client.sql.LibrusDbContract.GradeCategories;
import pl.librus.client.sql.LibrusDbContract.GradeComments;
import pl.librus.client.sql.LibrusDbContract.LuckyNumbers;
import pl.librus.client.sql.LibrusDbContract.PlainLessons;

import static pl.librus.client.sql.LibrusDbContract.Account;
import static pl.librus.client.sql.LibrusDbContract.DB_NAME;
import static pl.librus.client.sql.LibrusDbContract.DB_VERSION;
import static pl.librus.client.sql.LibrusDbContract.Grades;
import static pl.librus.client.sql.LibrusDbContract.Subjects;
import static pl.librus.client.sql.LibrusDbContract.Teachers;
import static pl.librus.client.sql.LibrusDbContract.TimetableLessons;

public class LibrusDbHelper extends SQLiteOpenHelper {
    private final Context context;

    public LibrusDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TimetableLessons.CREATE_TABLE);
        db.execSQL(Account.CREATE_TABLE);
        db.execSQL(Teachers.CREATE_TABLE);
        db.execSQL(Subjects.CREATE_TABLE);
        db.execSQL(Grades.CREATE_TABLE);
        db.execSQL(GradeCategories.CREATE_TABLE);
        db.execSQL(LuckyNumbers.CREATE_TABLE);
        db.execSQL(GradeComments.CREATE_TABLE);
        db.execSQL(Attendances.CREATE_TABLE);
        db.execSQL(AttendanceCategories.CREATE_TABLE);
        db.execSQL(PlainLessons.CREATE_TABLE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(context.getString(R.string.last_update), -1L);   //reset last update to indicate that database is empty
        editor.apply();
    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TimetableLessons.DELETE_TABLE);
        db.execSQL(Account.DELETE_TABLE);
        db.execSQL(Teachers.DELETE_TABLE);
        db.execSQL(Subjects.DELETE_TABLE);
        db.execSQL(Grades.DELETE_TABLE);
        db.execSQL(GradeCategories.DELETE_TABLE);
        db.execSQL(LuckyNumbers.DELETE_TABLE);
        db.execSQL(GradeComments.DELETE_TABLE);
        db.execSQL(Attendances.DELETE_TABLE);
        db.execSQL(AttendanceCategories.DELETE_TABLE);
        db.execSQL(PlainLessons.DELETE_TABLE);
        onCreate(db);
    }

    public List<Lesson> getLessonsForDate(LocalDate date) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Lesson> result = new ArrayList<>();
        final long dateMillis = date.toDateTimeAtStartOfDay().getMillis();
        Cursor cursor = db.query(
                TimetableLessons.TABLE_NAME,
                null,
                TimetableLessons.COLUMN_NAME_DATE + " = " + dateMillis,
                null,
                null,
                null,
                TimetableLessons.COLUMN_NAME_LESSON_NUMBER
        );
        if (cursor.getCount() == 0) {
            return null;
        } else {
            while (cursor.moveToNext()) {

                int lessonNumber = cursor.getInt(cursor.getColumnIndexOrThrow(TimetableLessons.COLUMN_NAME_LESSON_NUMBER));
                boolean substitution = cursor.getInt(cursor.getColumnIndexOrThrow(TimetableLessons.COLUMN_NAME_SUBSTITUTION)) > 0;
                boolean canceled = cursor.getInt(cursor.getColumnIndexOrThrow(TimetableLessons.COLUMN_NAME_CANCELED)) > 0;

                Subject subject = new Subject(
                        cursor.getString(cursor.getColumnIndexOrThrow(TimetableLessons.COLUMN_NAME_SUBJECT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TimetableLessons.COLUMN_NAME_SUBJECT_NAME)));

                Teacher teacher = new Teacher(
                        cursor.getString(cursor.getColumnIndexOrThrow(TimetableLessons.COLUMN_NAME_TEACHER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TimetableLessons.COLUMN_NAME_TEACHER_FIRST_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TimetableLessons.COLUMN_NAME_TEACHER_LAST_NAME)));

                String id = cursor.getString(cursor.getColumnIndexOrThrow(TimetableLessons.COLUMN_NAME_ID));

                LocalTime startTime = new LocalTime(
                        cursor.getLong(
                                cursor.getColumnIndexOrThrow(TimetableLessons.COLUMN_NAME_START_TIME)));
                LocalTime endTime = new LocalTime(
                        cursor.getLong(
                                cursor.getColumnIndexOrThrow(TimetableLessons.COLUMN_NAME_END_TIME)));
                Lesson lesson;

                if (canceled) {
                    lesson = new Lesson(id, lessonNumber, date, startTime, endTime, subject, teacher, true);
                } else if (substitution) {
                    lesson = new Lesson(id, lessonNumber, date, startTime, endTime, subject, teacher,
                            cursor.getString(cursor.getColumnIndexOrThrow(TimetableLessons.COLUMN_NAME_ORG_SUBJECT_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(TimetableLessons.COLUMN_NAME_ORG_TEACHER_ID)));
                } else {
                    lesson = new Lesson(id, lessonNumber, date, startTime, endTime, subject, teacher);
                }
                result.add(lesson);
            }
            cursor.close();
            return result;
        }
    }

    public GradeComment getGradeComment(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                GradeComments.TABLE_NAME,
                null,
                GradeComments.COLUMN_NAME_ID + " = ?",
                new String[]{id},
                null, null, null);
        if (cursor.getCount() == 0) {
            throw new UnsupportedOperationException("No grade comment with id " + id);
        } else if (cursor.getCount() > 1) {
            throw new UnsupportedOperationException(cursor.getCount() + " grade comments with same id " + id);
        } else {
            cursor.moveToFirst();
            GradeComment gc = new GradeComment(
                    cursor.getString(cursor.getColumnIndexOrThrow(GradeComments.COLUMN_NAME_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(GradeComments.COLUMN_NAME_ADDED_BY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(GradeComments.COLUMN_NAME_GRADE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(GradeComments.COLUMN_NAME_TEXT))
            );
            cursor.close();
            return gc;
        }
    }

    public List<Grade> getGrades() {
        List<Grade> grades = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor gradeCursor = db.rawQuery("SELECT * FROM " + Grades.TABLE_NAME, null);
        while (gradeCursor.moveToNext()) {
            Grade g = new Grade(
                    gradeCursor.getString(gradeCursor.getColumnIndexOrThrow(Grades.COLUMN_NAME_ID)),
                    gradeCursor.getString(gradeCursor.getColumnIndexOrThrow(Grades.COLUMN_NAME_GRADE)),
                    gradeCursor.getString(gradeCursor.getColumnIndexOrThrow(Grades.COLUMN_NAME_LESSON_ID)),
                    gradeCursor.getString(gradeCursor.getColumnIndexOrThrow(Grades.COLUMN_NAME_SUBJECT_ID)),
                    gradeCursor.getString(gradeCursor.getColumnIndexOrThrow(Grades.COLUMN_NAME_CATEGORY_ID)),
                    gradeCursor.getString(gradeCursor.getColumnIndexOrThrow(Grades.COLUMN_NAME_ADDED_BY_ID)),
                    gradeCursor.getString(gradeCursor.getColumnIndexOrThrow(Grades.COLUMN_NAME_COMMENT_ID)),
                    gradeCursor.getInt(gradeCursor.getColumnIndexOrThrow(Grades.COLUMN_NAME_SEMESTER)),
                    new LocalDate(gradeCursor.getLong(gradeCursor.getColumnIndexOrThrow(Grades.COLUMN_NAME_DATE))),
                    new LocalDateTime(gradeCursor.getLong(gradeCursor.getColumnIndexOrThrow(Grades.COLUMN_NAME_ADD_DATE))),
                    Grade.Type.values()[gradeCursor.getInt(gradeCursor.getColumnIndexOrThrow(Grades.COLUMN_NAME_TYPE))]
            );
            grades.add(g);
        }
        gradeCursor.close();
        return grades;
    }

    public List<Attendance> getAttendances() {
        List<Attendance> attendances = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor attendanceCursor = db.query(
                Attendances.TABLE_NAME,
                null,
                Attendances.COLUMN_NAME_TYPE_ID + " IS NOT 100",
                null,
                null, null, null
        );
        while (attendanceCursor.moveToNext()) {
            Attendance a = new Attendance(
                    attendanceCursor.getString(attendanceCursor.getColumnIndexOrThrow(Attendances.COLUMN_NAME_ID)),
                    attendanceCursor.getString(attendanceCursor.getColumnIndexOrThrow(Attendances.COLUMN_NAME_LESSON_ID)),
                    new LocalDate(attendanceCursor.getLong(attendanceCursor.getColumnIndexOrThrow(Attendances.COLUMN_NAME_DATE))),
                    new LocalDateTime(attendanceCursor.getLong(attendanceCursor.getColumnIndexOrThrow(Attendances.COLUMN_NAME_ADD_DATE))),
                    attendanceCursor.getInt(attendanceCursor.getColumnIndexOrThrow(Attendances.COLUMN_NAME_LESSON_NUMBER)),
                    attendanceCursor.getInt(attendanceCursor.getColumnIndexOrThrow(Attendances.COLUMN_NAME_SEMESTER)),
                    attendanceCursor.getString(attendanceCursor.getColumnIndexOrThrow(Attendances.COLUMN_NAME_TYPE_ID)),
                    attendanceCursor.getString(attendanceCursor.getColumnIndexOrThrow(Attendances.COLUMN_NAME_ADDED_BY_ID))
            );
            attendances.add(a);
        }
        attendanceCursor.close();
        return attendances;
    }

    public AttendanceCategory getAttendanceCategory(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                AttendanceCategories.TABLE_NAME,
                null,
                AttendanceCategories.COLUMN_NAME_ID + " = ?",
                new String[]{id},
                null, null, null);
        if (cursor.getCount() == 0) {
            throw new UnsupportedOperationException("No attendance category with id " + id);
        } else if (cursor.getCount() > 1) {
            throw new UnsupportedOperationException(cursor.getCount() + " attendance categories with same id " + id);
        } else {
            cursor.moveToFirst();
            AttendanceCategory category = new AttendanceCategory(
                    cursor.getString(cursor.getColumnIndexOrThrow(AttendanceCategories.COLUMN_NAME_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AttendanceCategories.COLUMN_NAME_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AttendanceCategories.COLUMN_NAME_SHORT_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AttendanceCategories.COLUMN_NAME_STANDARD)) > 0,
                    cursor.getString(cursor.getColumnIndexOrThrow(AttendanceCategories.COLUMN_NAME_COLOR)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AttendanceCategories.COLUMN_NAME_PRESENCE)) > 0,
                    cursor.getInt(cursor.getColumnIndexOrThrow(AttendanceCategories.COLUMN_NAME_ORDER))
            );
            cursor.close();
            return category;
        }
    }

    public GradeCategory getGradeCategory(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                GradeCategories.TABLE_NAME,
                null,
                GradeCategories.COLUMN_NAME_ID + " = ?",
                new String[]{id},
                null, null, null);
        if (cursor.getCount() == 0) {
            return null;
        } else if (cursor.getCount() > 1) {
            throw new UnsupportedOperationException(cursor.getCount() + " grade categories with same id " + id);
        } else {
            cursor.moveToFirst();
            GradeCategory category = new GradeCategory(
                    cursor.getString(cursor.getColumnIndex(GradeCategories.COLUMN_NAME_ID)),
                    cursor.getString(cursor.getColumnIndex(GradeCategories.COLUMN_NAME_NAME)),
                    cursor.getInt(cursor.getColumnIndex(GradeCategories.COLUMN_NAME_WEIGHT))
            );
            cursor.close();
            return category;
        }
    }

    public LuckyNumber getLastLuckyNumber() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(LuckyNumbers.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                LuckyNumbers.COLUMN_NAME_DATE);
        if (cursor.getCount() == 0) {
            return null;
        } else {
            cursor.moveToFirst();
            LuckyNumber luckyNumber = new LuckyNumber(
                    cursor.getInt(cursor.getColumnIndexOrThrow(LuckyNumbers.COLUMN_NAME_LUCKY_NUMBER)),
                    new LocalDate(cursor.getLong(cursor.getColumnIndexOrThrow(LuckyNumbers.COLUMN_NAME_DATE)))
            );
            cursor.close();
            return luckyNumber;
        }

    }

    public Teacher getTeacher(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                Teachers.TABLE_NAME,
                null,
                Teachers.COLUMN_NAME_ID + " = ?",
                new String[]{id},
                null, null, null);
        if (cursor.getCount() == 0) {
            return null;
        } else if (cursor.getCount() > 1) {
            throw new UnsupportedOperationException(cursor.getCount() + " teachers with same id " + id);
        } else {
            cursor.moveToFirst();
            Teacher teacher = new Teacher(
                    cursor.getString(cursor.getColumnIndexOrThrow(Teachers.COLUMN_NAME_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Teachers.COLUMN_NAME_FIRST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Teachers.COLUMN_NAME_LAST_NAME))
            );
            cursor.close();
            return teacher;
        }
    }

    public PlainLesson getLesson(String lessonId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                true,
                PlainLessons.TABLE_NAME,
                null,
                PlainLessons.COLUMN_NAME_ID + " = ?",
                new String[]{lessonId},
                null, null, null, null
        );
        if (cursor.getCount() >= 1) {
            if (cursor.getCount() > 1)
                LibrusUtils.log(String.valueOf(cursor.getCount()) + " lessons with id " + lessonId, Log.WARN);
            cursor.moveToFirst();

            PlainLesson lesson = new PlainLesson(
                    cursor.getString(cursor.getColumnIndexOrThrow(PlainLessons.COLUMN_NAME_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PlainLessons.COLUMN_NAME_TEACHER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PlainLessons.COLUMN_NAME_SUBJECT_ID))
            );
            cursor.close();
            return lesson;
        } else throw new UnsupportedOperationException("No lesson with id " + lessonId);

    }

    public Subject getSubject(String subjectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                Subjects.TABLE_NAME,
                null,
                Subjects.COLUMN_NAME_ID + " = ?",
                new String[]{subjectId},
                null, null, null);
        if (cursor.getCount() == 0) {
            return null;
        } else if (cursor.getCount() > 1) {
            throw new UnsupportedOperationException(cursor.getCount() + " subjects with same id " + subjectId);
        } else {
            cursor.moveToFirst();
            Subject subject = new Subject(
                    cursor.getString(cursor.getColumnIndexOrThrow(Subjects.COLUMN_NAME_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Subjects.COLUMN_NAME_NAME))
            );
            cursor.close();
            return subject;
        }
    }
}
