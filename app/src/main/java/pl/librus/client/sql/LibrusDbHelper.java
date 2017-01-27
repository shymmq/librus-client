package pl.librus.client.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import pl.librus.client.api.Grade;
import pl.librus.client.api.GradeCategory;
import pl.librus.client.api.LuckyNumber;
import pl.librus.client.sql.LibrusDbContract.GradeCategories;
import pl.librus.client.sql.LibrusDbContract.LuckyNumbers;

import static pl.librus.client.sql.LibrusDbContract.Account;
import static pl.librus.client.sql.LibrusDbContract.DB_NAME;
import static pl.librus.client.sql.LibrusDbContract.DB_VERSION;
import static pl.librus.client.sql.LibrusDbContract.Grades;
import static pl.librus.client.sql.LibrusDbContract.Lessons;
import static pl.librus.client.sql.LibrusDbContract.Subjects;
import static pl.librus.client.sql.LibrusDbContract.Teachers;

/**
 * Created by szyme on 27.01.2017.
 */

public class LibrusDbHelper extends SQLiteOpenHelper {
    public LibrusDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Lessons.CREATE_TABLE);
        db.execSQL(Account.CREATE_TABLE);
        db.execSQL(Teachers.CREATE_TABLE);
        db.execSQL(Subjects.CREATE_TABLE);
        db.execSQL(Grades.CREATE_TABLE);
        db.execSQL(GradeCategories.CREATE_TABLE);
        db.execSQL(LuckyNumbers.CREATE_TABLE);
    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Lessons.DELETE_TABLE);
        db.execSQL(Account.DELETE_TABLE);
        db.execSQL(Teachers.DELETE_TABLE);
        db.execSQL(Subjects.DELETE_TABLE);
        db.execSQL(Grades.DELETE_TABLE);
        db.execSQL(GradeCategories.DELETE_TABLE);
        db.execSQL(LuckyNumbers.DELETE_TABLE);
        onCreate(db);
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

    public GradeCategory getGradeCategory(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(GradeCategories.TABLE_NAME,
                null,
                GradeCategories.COLUMN_NAME_ID + " = ?",
                new String[]{id},
                null, null, null);
        if (cursor.getCount() == 0) {
            throw new UnsupportedOperationException("No category with id " + id);
        }
        cursor.moveToFirst();
        GradeCategory category = new GradeCategory(
                cursor.getString(cursor.getColumnIndex(GradeCategories.COLUMN_NAME_ID)),
                cursor.getString(cursor.getColumnIndex(GradeCategories.COLUMN_NAME_NAME)),
                cursor.getInt(cursor.getColumnIndex(GradeCategories.COLUMN_NAME_WEIGHT))
        );
        cursor.close();
        return category;
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
        if (cursor.getCount() == 0) return new LuckyNumber(0, LocalDate.now());
        cursor.moveToFirst();
        LuckyNumber luckyNumber = new LuckyNumber(
                cursor.getInt(cursor.getColumnIndexOrThrow(LuckyNumbers.COLUMN_NAME_LUCKY_NUMBER)),
                new LocalDate(cursor.getLong(cursor.getColumnIndexOrThrow(LuckyNumbers.COLUMN_NAME_DATE)))
        );
        cursor.close();
        return luckyNumber;
    }
}
