package pl.librus.client.api;

/**
 * Created by szyme on 17.12.2016. librus-client
 */
interface Changeable<T> {
    Change getChanges(T t);
}
