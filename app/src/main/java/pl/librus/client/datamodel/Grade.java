package pl.librus.client.datamodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Created by szyme on 08.12.2016. librus-client
 */
public class Grade extends HasId {
    private HasId lesson, subject, student, category, addedBy;
    private String grade;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime addDate;

    private int semester;

    @JsonProperty("IsSemester")
    private boolean semesterType;
    @JsonProperty("IsSemesterProposition")
    private boolean semesterPropositionType;
    @JsonProperty("IsFinal")
    private boolean finalType;
    @JsonProperty("IsFinalProposition")
    private boolean finalPropositionType;

    public Grade() {
    }

    public Grade(String id, HasId lesson, HasId subject, HasId student, HasId category, HasId addedBy,
                 String grade, LocalDate date, LocalDateTime addDate,
                 int semester,
                 boolean semesterType, boolean semesterPropositionType, boolean finalType, boolean finalPropositionType) {
        super(id);
        this.lesson = lesson;
        this.subject = subject;
        this.student = student;
        this.category = category;
        this.addedBy = addedBy;
        this.grade = grade;
        this.date = date;
        this.addDate = addDate;
        this.semester = semester;
        this.semesterType = semesterType;
        this.semesterPropositionType = semesterPropositionType;
        this.finalType = finalType;
        this.finalPropositionType = finalPropositionType;
    }

    public HasId getLesson() {
        return lesson;
    }

    public HasId getSubject() {
        return subject;
    }

    public HasId getStudent() {
        return student;
    }

    public HasId getCategory() {
        return category;
    }

    public HasId getAddedBy() {
        return addedBy;
    }

    public String getGrade() {
        return grade;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getAddDate() {
        return addDate;
    }

    public int getSemester() {
        return semester;
    }

    public boolean isSemesterType() {
        return semesterType;
    }

    public boolean isSemesterPropositionType() {
        return semesterPropositionType;
    }

    public boolean isFinalType() {
        return finalType;
    }

    public boolean isFinalPropositionType() {
        return finalPropositionType;
    }

    public Type getType() {
        if (semesterPropositionType) return Type.SEMESTER_PROPOSITION;
        else if (semesterType) return Type.SEMESTER;
        else if (finalPropositionType) return Type.FINAL_PROPOSITION;
        else if (finalType) return Type.FINAL;
        else return Type.NORMAL;
    }

    public enum Type {
        NORMAL, SEMESTER_PROPOSITION, SEMESTER, FINAL_PROPOSITION, FINAL
    }
}
