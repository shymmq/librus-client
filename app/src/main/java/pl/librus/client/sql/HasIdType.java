package pl.librus.client.sql;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;

import java.sql.SQLException;

import pl.librus.client.datamodel.HasId;

/**
 * Created by szyme on 01.02.2017.
 */

public class HasIdType extends StringType {

    public HasIdType() {
        super(SqlType.STRING, new Class[]{HasId.class});
    }

    @Override
    public HasId sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        return new HasId((String) sqlArg);
    }

    @Override
    public String javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        return ((HasId) javaObject).getId();
    }
}
