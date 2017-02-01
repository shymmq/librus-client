package pl.librus.client.sql;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.sql.SQLException;

public final class LocalDateType extends BaseDataType {

    private final static DateTimeFormatter DTF = ISODateTimeFormat.date();

    public LocalDateType() {
        super(SqlType.STRING, new Class[]{LocalDate.class});
    }

    @Override
    public boolean isEscapedValue() {
        return true;
    }

    @Override
    public int getDefaultWidth() {
        return 10;
    }

    @Override
    public String parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        return javaToSqlArg(fieldType, DTF.parseLocalDate(defaultStr));
    }

    @Override
    public String resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getString(columnPos);
    }

    @Override
    public String javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        return ((LocalDate) javaObject).toString(DTF);
    }

    @Override
    public LocalDate sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        return LocalDate.parse((String) sqlArg, DTF);
    }
}