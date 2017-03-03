package pl.librus.client.datamodel.subject;

import org.immutables.value.Value;

import io.requery.Column;
import io.requery.Key;
import io.requery.Superclass;
import pl.librus.client.datamodel.Identifiable;

@Superclass
public abstract class BaseSubject implements Identifiable {

    @Key
    @Value.Parameter
    public abstract String id();

    @Value.Parameter
    @Column
    public abstract String name();
}
