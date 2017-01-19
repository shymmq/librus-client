package pl.librus.client.cache;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.librus.client.api.SchoolWeek;

public class TimetableCache implements Serializable, LibrusCache {
    private static final long serialVersionUID = 8210338107907304477L;
    private List<SchoolWeek> schoolWeeks = new ArrayList<>();

    public TimetableCache(List<SchoolWeek> schoolWeeks) {
        this.schoolWeeks = schoolWeeks;
    }

    public List<SchoolWeek> getSchoolWeeks() {
        return schoolWeeks;
    }

    public TimetableCache addSchoolWeek(SchoolWeek schoolWeek){
        schoolWeeks.add(schoolWeek);
        Collections.sort(schoolWeeks);
        return this;
    }
    @Override
    public long getExpirationPeriod() {
        return Long.MAX_VALUE;
    }
}
