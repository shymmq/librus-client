package pl.librus.client.api;

import android.annotation.SuppressLint;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.HashMap;

public class SchoolDay implements Serializable {

    private LocalDate date = LocalDate.now();
    private boolean empty = true;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Lesson> lessons = new HashMap<>();

    SchoolDay(LocalDate date) {
        this.date = date;
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

    void setLesson(int number, Lesson lesson) {
        lessons.put(number, lesson);
        empty = false;
    }

    public LocalDate getDate() {
        return date;
    }

    Lesson getLastLesson() {
        return lessons.get(lessons.size());
    }

    public Lesson getLesson(int i) {
        return lessons.get(i);
    }

    public boolean isEmpty() {
        return empty;
    }

    void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public int size() {
        return lessons.size();
    }
}
