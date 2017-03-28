package pl.librus.client.domain;

import io.requery.Persistable;

/**
 * Created by szyme on 05.02.2017.
 */

public interface Identifiable extends Persistable{
    String id();
}
