package pl.librus.client.api;

/**
 * Created by szyme on 16.12.2016. librus-client
 */
public class Change {

    private Action a;
    private ObjectType t;
    private String id;

    Change(String id, Action a, ObjectType t) {
        this.a = a;
        this.t = t;
        this.id = id;
    }

    public ObjectType getType() {
        return t;
    }

    public Action getAction() {
        return a;
    }

    enum Action {
        ADD, CHANGE, REMOVE
    }

    enum ObjectType {
        GRADE, ANNOUNCEMENT,
        EVENT, EVENT_DATE,
        LESSON_CANCEL,
        LESSON_SUBSTITUTE_SUBJECT,
        LESSON_SUBSTITUTE_TEACHER
        //TODO add attendances
    }
}
