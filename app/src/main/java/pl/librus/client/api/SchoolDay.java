package pl.librus.client.api;

import android.annotation.SuppressLint;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.text.ParseException;
import java.util.HashMap;

public class SchoolDay implements Serializable {

    private LocalDate date = LocalDate.now();
    private boolean empty = true;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Lesson> lessons = new HashMap<>();

    public SchoolDay(HashMap<Integer, Lesson> lessons, LocalDate date) {
        this.lessons = lessons;
        this.date = date;
    }


    SchoolDay(JSONArray data, LocalDate date) {
        this.date = date;
//        Log.d(TAG, "SchoolDay: parsing json: " + date.toString() + data.toString());
        for (int i = 0; i < data.length(); i++) {
            try {
                if (data.getJSONArray(i).length() == 0) {
//                    Log.d(TAG, "SchoolDay: Creating empty Lesson");
                    lessons.put(i, null);
                } else {
                    lessons.put(i, new Lesson(data.getJSONArray(i).getJSONObject(0), i, date));
                    empty = false;
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
        cleanUp();
    }

    private void cleanUp() {
        if (lessons.containsKey(0)) {
            lessons.remove(0);
        }
        int index = 10;
        while (lessons.get(index) == null && index >= 0) {
            lessons.remove(index);
            index--;
        }
//        Log.d(TAG, "cleanUp: DONE: " + lessons.toString());
    }

    public void setLesson(int number, Lesson lesson) {
        lessons.put(number, lesson);
        empty = false;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    Lesson getLastLesson() {
        return lessons.get(lessons.size());
    }

    HashMap<Integer, Lesson> getLessons() {
        return lessons;
    }

    public Lesson getLesson(int i) {
        return lessons.get(i);
    }

    public boolean isEmpty() {
        return empty;
    }

    public int size() {
        return lessons.size();
    }

}
