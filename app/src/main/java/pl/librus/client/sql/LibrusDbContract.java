package pl.librus.client.sql;

import android.provider.BaseColumns;

/**
 * Created by szyme on 27.01.2017.
 */

public class LibrusDbContract {

    public static final int DB_VERSION = 4;
    public static final String DB_NAME = "database.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String NOT_NULL = " NOT NULL";

    private LibrusDbContract() {
    }

    public static abstract class Lessons implements BaseColumns {

        public static final String TABLE_NAME = "lessons";

        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_LESSON_NUMBER = "lesson_number";
        public static final String COLUMN_NAME_SUBJECT_ID = "subject_id";
        public static final String COLUMN_NAME_SUBJECT_NAME = "subject_name";
        public static final String COLUMN_NAME_TEACHER_ID = "teacher_id";
        public static final String COLUMN_NAME_TEACHER_FIRST_NAME = "teacher_first_name";
        public static final String COLUMN_NAME_TEACHER_LAST_NAME = "teacher_last_name";
        public static final String COLUMN_NAME_SUBSTITUTION = "substitution";
        public static final String COLUMN_NAME_CANCELED = "canceled";
        public static final String COLUMN_NAME_ORG_TEACHER_ID = "org_teacher_id";
        public static final String COLUMN_NAME_ORG_SUBJECT_ID = "org_subject_id";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_ID + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                COLUMN_NAME_DATE + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                COLUMN_NAME_LESSON_NUMBER + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                COLUMN_NAME_SUBJECT_ID + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_SUBJECT_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_TEACHER_ID + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_TEACHER_FIRST_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_TEACHER_LAST_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_SUBSTITUTION + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_CANCELED + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_ORG_SUBJECT_ID + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_ORG_TEACHER_ID + TEXT_TYPE + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class Account implements BaseColumns {

        public static final String TABLE_NAME = "account";

        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_CLASS_ID = "class_id";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_EMAIL = "email";


        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_ID + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_CLASS_ID + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_FIRST_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LAST_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_USERNAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_EMAIL + TEXT_TYPE + " )";

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
