package pl.librus.client.api;

import android.annotation.SuppressLint;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.HashMap;

public class SchoolDay implements Serializable {
    static final long serialVersionUID = -8357220840792654725L;
    private LocalDate date = LocalDate.now();
    private boolean empty = true;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Lesson> lessons = new HashMap<>();
    private int lastLesson = 0;

    SchoolDay(LocalDate date) {
        this.date = date;
    }

    void setLesson(int number, Lesson lesson) {
        lessons.put(number, lesson);
        empty = false;
        lastLesson = number > lastLesson ? number : lastLesson;
    }

    public int getLastLesson() {
        return lastLesson;
    }

    public LocalDate getDate() {
        return date;
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

    public void removeLesson(int i) {
        lessons.remove(i);
    }
}
