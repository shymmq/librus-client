package pl.librus.client.sql;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import org.joda.time.LocalTime;

import java.sql.SQLException;

public final class LocalTimeType extends BaseDataType {

    public LocalTimeType() {
        super(SqlType.INTEGER, new Class<?>[]{LocalTime.class});
    }

    @Override
    public boolean isEscapedValue() {
        return false;
    }

    @Override
    public Integer parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        return javaToSqlArg(fieldType, LocalTime.parse(defaultStr));
    }

    @Override
    public Integer resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getInt(columnPos);
    }

    @Override
    public Integer javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        return ((LocalTime) javaObject).getMillisOfDay();
    }

    @Override
    public LocalTime sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        return LocalTime.fromMillisOfDay((Integer) sqlArg);
    }
}