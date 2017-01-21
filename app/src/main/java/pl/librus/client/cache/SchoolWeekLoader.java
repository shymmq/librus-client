package pl.librus.client.cache;

import android.content.Context;

import org.jdeferred.Promise;
import org.joda.time.LocalDate;

import pl.librus.client.api.APIClient;
import pl.librus.client.api.SchoolWeek;

/**
 * Created by szyme on 21.01.2017.
 * Czy to nie jest piękne?
 */

public class SchoolWeekLoader extends AbstractDataLoader<SchoolWeek, LocalDate> {


    public SchoolWeekLoader(Context context) {
        super(context);
    }

    @Override
    protected Promise<SchoolWeek, Void, SchoolWeek> download(APIClient client, LocalDate arg) {
        return client.getSchoolWeek(arg);
    }

    @Override
    protected String getFilename(LocalDate arg) {
        return "week" + arg.toString("xxww");
    }

}
