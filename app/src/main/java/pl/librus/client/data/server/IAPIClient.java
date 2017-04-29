package pl.librus.client.data.server;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import pl.librus.client.data.DataLoadStrategy;

public interface IAPIClient extends DataLoadStrategy {

    Single<String> login(String username, String password);

    <T> Observable<T> getAll(String endpoint, Class<T> clazz);

    Completable pushDevices(final String regToken);
}
