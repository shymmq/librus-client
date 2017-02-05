package pl.librus.client.sql;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.sql.Configuration;
import io.requery.sql.ConfigurationBuilder;
import io.requery.sql.EntityDataStore;
import io.requery.sql.GenericMapping;
import io.requery.sql.platform.SQLite;
import pl.librus.client.datamodel.Models;

/**
 * Created by robwys on 05/02/2017.
 */

public class SqlHelper {

    public static EntityDataStore<Persistable> getDataStore(DatabaseSource source) {
        Configuration configuration = new ConfigurationBuilder(source, Models.DEFAULT)
                .setMapping(new MainMapping())
                .build();
        return new EntityDataStore<Persistable>(configuration);
    }

    private static class MainMapping extends GenericMapping {

        public MainMapping() {
            super(new SQLite());
            addConverter(new LocalDateConverter());
            addConverter(new LocalTimeConverter());
            addConverter(new LocalDateTimeConverter());
        }
    }
}
