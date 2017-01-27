package pl.librus.client.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Lessons.DELETE_TABLE);
        db.execSQL(Account.DELETE_TABLE);
        db.execSQL(Teachers.DELETE_TABLE);
        db.execSQL(Subjects.DELETE_TABLE);
        db.execSQL(Grades.DELETE_TABLE);
        onCreate(db);
    }
}
