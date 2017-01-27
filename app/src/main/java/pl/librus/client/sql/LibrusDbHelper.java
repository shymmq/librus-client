package pl.librus.client.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by szyme on 27.01.2017.
 */

public class LibrusDbHelper extends SQLiteOpenHelper {
    public LibrusDbHelper(Context context) {
        super(context, LibrusDbContract.DB_NAME, null, LibrusDbContract.DB_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LibrusDbContract.Lessons.CREATE_TABLE);
        db.execSQL(LibrusDbContract.Account.CREATE_TABLE);
        db.execSQL(LibrusDbContract.Teachers.CREATE_TABLE);
        db.execSQL(LibrusDbContract.Subjects.CREATE_TABLE);
    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LibrusDbContract.Lessons.DELETE_TABLE);
        db.execSQL(LibrusDbContract.Account.DELETE_TABLE);
        db.execSQL(LibrusDbContract.Teachers.DELETE_TABLE);
        db.execSQL(LibrusDbContract.Subjects.DELETE_TABLE);
        onCreate(db);
    }
}
