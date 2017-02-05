package pl.librus.client.timetable;

import android.support.annotation.NonNull;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import pl.librus.client.datamodel.Lesson;

public class SchoolDay {

    private LocalDate date;
    private SortedSet<Lesson> lessons = new TreeSet<>();

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

    public SortedSet<Lesson> getLessons() {
        return lessons;
    }

}
