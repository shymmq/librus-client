package pl.librus.client.datamodel;

import android.support.annotation.NonNull;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class SchoolDay implements Comparable<SchoolDay> {

    private LocalDate date;
    private List<Lesson> lessons = new ArrayList<>();

    public SchoolDay(LocalDate date) {
        this.date = date;
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isEmpty() {
        return lessons.isEmpty();
    }

    public List<Lesson> getLessons() {
        return lessons;
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
