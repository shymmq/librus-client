package pl.librus.client.sql;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.sql.SQLException;

public final class LocalDateTimeType extends BaseDataType {

    public LocalDateTimeType() {
        super(SqlType.LONG, new Class[]{LocalDateTime.class});
    }

    @Override
    public boolean isEscapedValue() {
        return false;
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        return javaToSqlArg(fieldType, LocalDateTime.parse(defaultStr));
    }

    @Override
    public Long resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getLong(columnPos);
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        LocalDateTime localDateTime = (LocalDateTime) javaObject;
        return localDateTime.toDateTime(DateTimeZone.UTC).getMillis();
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        return new LocalDateTime(((Long) sqlArg).longValue(), DateTimeZone.UTC);
    }
}