package pl.librus.client.grades;

/**
 * Created by szyme on 11.12.2016. librus-client
 */

public abstract class GradeEntry<T extends GradeEntry<T>> implements Comparable<T> {
    public abstract String getSubjectId();

    public abstract boolean equals(Object o);

    public abstract int hashCode();

}
