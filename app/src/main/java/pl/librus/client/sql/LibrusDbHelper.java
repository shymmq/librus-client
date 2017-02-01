package pl.librus.client.sql;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pl.librus.client.LibrusUtils;
import pl.librus.client.R;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceType;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.GradeCategory;
import pl.librus.client.datamodel.GradeComment;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.LibrusAccount;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.sql.LibrusDbContract.AttendanceCategories;
import pl.librus.client.sql.LibrusDbContract.Attendances;
import pl.librus.client.sql.LibrusDbContract.PlainLessons;

import static pl.librus.client.sql.LibrusDbContract.Subjects;

public class LibrusDbHelper extends OrmLiteSqliteOpenHelper {
    private static final String DB_NAME = "librus-client.db";
    private static final int DB_VERSION = 16;
    private final Context context;

    private Class[] tables = {
            Subject.class,
            LuckyNumber.class,
            LibrusAccount.class,
            Lesson.class,
            Teacher.class,
            Grade.class,
            GradeCategory.class,
            PlainLesson.class,
            GradeComment.class
    };

    public LibrusDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            for (Class c : tables) {
                TableUtils.createTable(connectionSource, c);
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(context.getString(R.string.last_update), -1L);   //reset last update to indicate that database is empty
            editor.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            for (Class c : tables) {
                TableUtils.dropTable(connectionSource, c, true);
            }
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<LuckyNumber, LocalDate> getLuckyNumberDao() throws SQLException {
        return getDao(LuckyNumber.class);
    }

    public Dao<LibrusAccount, String> getLibrusAccountDao() throws SQLException {
        return getDao(LibrusAccount.class);
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
                    attendanceCursor.getString(attendanceCursor.getColumnIndexOrThrow(Attendances.COLUMN_NAME_STUDENT_ID)),
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

    public AttendanceType getAttendanceCategory(String id) {
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
            AttendanceType category = new AttendanceType(
                    cursor.getString(cursor.getColumnIndexOrThrow(AttendanceCategories.COLUMN_NAME_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AttendanceCategories.COLUMN_NAME_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AttendanceCategories.COLUMN_NAME_COLOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AttendanceCategories.COLUMN_NAME_SHORT_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AttendanceCategories.COLUMN_NAME_STANDARD)) > 0,
                    cursor.getInt(cursor.getColumnIndexOrThrow(AttendanceCategories.COLUMN_NAME_PRESENCE)) > 0,
                    cursor.getInt(cursor.getColumnIndexOrThrow(AttendanceCategories.COLUMN_NAME_ORDER))
            );
            cursor.close();
            return category;
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
