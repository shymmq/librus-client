package pl.librus.client.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import pl.librus.client.datamodel.HasId;

/**
 * Created by szyme on 08.12.2016. librus-client
 */
@DatabaseTable(tableName = "averages")
public class Average {

    @DatabaseField
    @JsonProperty("Semester1")
    private double semester1;
    @DatabaseField
    @JsonProperty("Semester2")
    private double semester2;
    @DatabaseField
    @JsonProperty("FullYear")
    private double fullYear;
    @DatabaseField(id = true)
    @JsonProperty("Subject")
    private HasId subject;

    public Average() {
    }

    public double getFullYear() {
        return fullYear;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Average average = (Average) o;

        return Double.compare(average.semester1, semester1) == 0 &&
                Double.compare(average.semester2, semester2) == 0 &&
                Double.compare(average.fullYear, fullYear) == 0 &&
                subject.equals(average.subject);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(semester1);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(semester2);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(fullYear);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + subject.hashCode();
        return result;
    }

}
