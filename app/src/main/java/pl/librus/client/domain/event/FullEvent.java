package pl.librus.client.domain.event;

import org.immutables.value.Value;

import pl.librus.client.domain.Teacher;

/**
 * Created by szyme on 07.05.2017.
 */

@Value.Immutable
public abstract class FullEvent extends BaseEvent {

    public abstract EventCategory category();

    public abstract Teacher addedBy();

    public String lessonId() {
        return date().toString() + ":" + lessonNo();
    }

}
