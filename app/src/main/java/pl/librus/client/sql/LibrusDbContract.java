package pl.librus.client.sql;

import android.provider.BaseColumns;

/**
 * Created by szyme on 27.01.2017.
 */

public class LibrusDbContract {

    private LibrusDbContract() {
    }

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "database.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String NOT_NULL = " NOT NULL";

    public static abstract class TimetableTable implements BaseColumns {

        public static final String TABLE_NAME = "timetable";

        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_SUBJECT_NAME = "subject_name";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_ID + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                COLUMN_NAME_SUBJECT_NAME + TEXT_TYPE + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class GradeTable implements BaseColumns {

        public static final String TABLE_NAME = "grades";

        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_GRADE = "grade";
        public static final String COLUMN_NAME_SUBJECT_ID = "subject_id";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_ID + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                COLUMN_NAME_SUBJECT_ID + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_GRADE + TEXT_TYPE + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
