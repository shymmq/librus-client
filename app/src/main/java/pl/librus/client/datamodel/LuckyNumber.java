package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.LocalDate;

/**
 * Created by Adam on 2016-11-06.
 */

@DatabaseTable(tableName = "lucky_numbers")
public class LuckyNumber {
    @JsonProperty("LuckyNumber")
    @DatabaseField
    private int luckyNumber;
    @JsonProperty("LuckyNumberDay")
    @DatabaseField(id = true)
    private LocalDate luckyNumberDay;

    public LuckyNumber() {
    }

    public int getLuckyNumber() {
        return luckyNumber;
    }

    public LocalDate getLuckyNumberDay() {
        return luckyNumberDay;
    }

}
