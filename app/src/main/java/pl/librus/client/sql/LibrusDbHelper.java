package pl.librus.client.sql;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import pl.librus.client.R;
import pl.librus.client.api.Event;
import pl.librus.client.api.EventCategory;
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

public class LibrusDbHelper extends OrmLiteSqliteOpenHelper {
    private static final String DB_NAME = "librus-client.db";
    private static final int DB_VERSION = 17;
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
            GradeComment.class,
            Event.class,
            EventCategory.class,
            Attendance.class,
            AttendanceType.class
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
            editor.apply();
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
}
