package pl.librus.client.api;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.HashMap;

public class SchoolDay implements Serializable, Comparable<SchoolDay> {
    private static final long serialVersionUID = -8357220840792654725L;
    private LocalDate date = LocalDate.now();
    private boolean empty = true;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Lesson> lessons = new HashMap<>();
    private int lastLesson = 0;

    public SchoolDay(LocalDate date) {
        this.date = date;
    }

    void setLesson(int number, Lesson lesson) {
        lessons.put(number, lesson);
        empty = false;
        lastLesson = number > lastLesson ? number : lastLesson;
    }

    public HashMap<Integer, Lesson> getLessons() {
        return lessons;
    }

    public LocalDate getDate() {
        return date;
    }

    public Lesson getLesson(int i) {
        return lessons.get(i);
    }

    public int getLastLesson() {
        return lastLesson;
    }

    public boolean isEmpty() {
        return empty;
    }

    void setEmpty(boolean empty) {
        this.empty = empty;
    }

    @Override
    public int compareTo(@NonNull SchoolDay schoolDay) {
        return date.compareTo(schoolDay.getDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SchoolDay schoolDay = (SchoolDay) o;

        return date.equals(schoolDay.date);

    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }
}
