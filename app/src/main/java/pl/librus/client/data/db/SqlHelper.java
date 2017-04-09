package pl.librus.client.data.db;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.cache.EntityCacheBuilder;
import io.requery.sql.Configuration;
import io.requery.sql.ConfigurationBuilder;
import io.requery.sql.EntityDataStore;
import io.requery.sql.GenericMapping;
import io.requery.sql.platform.SQLite;
import pl.librus.client.Models;

/**
 * Created by robwys on 05/02/2017.
 */

public class SqlHelper {

    public static EntityDataStore<Persistable> getDataStore(DatabaseSource source) {
        Configuration configuration = new ConfigurationBuilder(source, Models.DEFAULT)
                .setMapping(new MainMapping())
                .setStatementCacheSize(100)
                .setEntityCache(new EntityCacheBuilder(Models.DEFAULT)
                        .useReferenceCache(true)
                        .build())
                .build();
        return new EntityDataStore<>(configuration);
    }

    private static class MainMapping extends GenericMapping {

        MainMapping() {
            super(new SQLite());
            addConverter(new LocalDateConverter());
            addConverter(new LocalTimeConverter());
            addConverter(new LocalDateTimeConverter());
            addConverter(new MultipleIdsConverter());
            addConverter(new OptionalConverter());
        }

    }
}
