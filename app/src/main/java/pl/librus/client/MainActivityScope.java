package pl.librus.client;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by robwys on 27/03/2017.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface MainActivityScope {
}
