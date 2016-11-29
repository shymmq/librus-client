package pl.librus.client.api;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Adam on 2016-11-06.
 */

public class LuckyNumber implements Serializable {
    private int luckyNumber;
    private LocalDate luckyNumberDay;

    LuckyNumber(JSONObject data) throws JSONException {
        JSONObject luckyNumber = data.getJSONObject("LuckyNumber");
        this.luckyNumber = luckyNumber.getInt("LuckyNumber");
        this.luckyNumberDay = LocalDate.parse(luckyNumber.getString("LuckyNumberDay"));
    }

    public int getLuckyNumber() {
        return luckyNumber;
    }

    public LocalDate getLuckyNumberDay() {
        return luckyNumberDay;
    }
}
