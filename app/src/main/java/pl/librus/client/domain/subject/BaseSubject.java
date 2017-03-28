package pl.librus.client.domain.subject;

import org.immutables.value.Value;

import io.requery.Column;
import io.requery.Key;
import io.requery.Superclass;

@Superclass
public abstract class BaseSubject {

    @Key
    @Value.Parameter
    public abstract String id();

    @Value.Parameter
    @Column
    public abstract String name();
}
