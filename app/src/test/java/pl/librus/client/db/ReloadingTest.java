package pl.librus.client.db;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import pl.librus.client.data.ImmutableEntityChange;
import pl.librus.client.data.LastUpdate;
import pl.librus.client.domain.LibrusColor;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.domain.grade.ImmutableGrade;
import pl.librus.client.data.EntityChange;
import pl.librus.client.data.UpdateHelper;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static pl.librus.client.data.EntityChange.Type.ADDED;
import static pl.librus.client.data.EntityChange.Type.CHANGED;

@SuppressWarnings("unchecked")
@RunWith(RobolectricTestRunner.class)
public class ReloadingTest extends BaseDBTest {

    private UpdateHelper updateHelper;

    @Before
    public void setUpdateHelper() {
        updateHelper = new UpdateHelper(databaseManager, apiClient);
    }

    @Test
    public void shouldDiscoverNewEntity() throws ExecutionException, InterruptedException {
        //given
        Grade newGrade = EntityTemplates.grade();
        mockApiClient(newGrade);

        //when
        List<EntityChange<Grade>> result = updateHelper.reload(Grade.class)
                .toList()
                .blockingGet();

        //then
        assertThat(result, contains(ImmutableEntityChange.of(ADDED, newGrade)));
    }

    @Test
    public void shouldNotDiscoverAnything() throws ExecutionException, InterruptedException {
        //given
        Grade grade = EntityTemplates.grade();
        data.upsert(grade);
        mockApiClient(grade);

        //when
        List<EntityChange<Grade>> result = updateHelper.reload(Grade.class)
                .toList()
                .blockingGet();

        //then
        assertThat(result, empty());
    }

    @Test
    public void shouldDiscoverChange() throws ExecutionException, InterruptedException {
        //given
        ImmutableGrade oldGrade = EntityTemplates.grade()
                .withGrade("4");
        data.upsert(oldGrade);
        Grade newGrade = oldGrade.withGrade("5");
        mockApiClient(newGrade);

        //when
        List<EntityChange<Grade>> result = updateHelper.reload(Grade.class)
                .toList()
                .blockingGet();

        //then
        assertThat(result, contains(ImmutableEntityChange.of(CHANGED, newGrade)));
    }

    @Test
    public void shouldDiscoverChangeAndAddition() throws ExecutionException, InterruptedException {
        //given
        ImmutableGrade oldGrade = EntityTemplates.grade()
                .withId("1")
                .withGrade("4");
        data.upsert(oldGrade);
        Grade changedGrade = oldGrade.withGrade("5");
        Grade newGrade = oldGrade.withId("2");

        mockApiClient(newGrade, changedGrade);

        //when
        List<EntityChange<Grade>> result = updateHelper.reload(Grade.class)
                .toList()
                .blockingGet();

        //then
        assertThat(result, containsInAnyOrder(
                ImmutableEntityChange.of(CHANGED, changedGrade),
                ImmutableEntityChange.of(ADDED, newGrade)));
    }

    @Test
    public void shouldNotFailOnDeletion() throws ExecutionException, InterruptedException {
        //given
        ImmutableGrade grade = EntityTemplates.grade()
                .withId("1");
        ImmutableGrade deletedGrade = EntityTemplates.grade()
                .withId("2");
        data.upsert(grade);
        data.upsert(deletedGrade);

        mockApiClient(grade);

        //when
        List<EntityChange<Grade>> result = updateHelper.reload(Grade.class)
                .toList()
                .blockingGet();

        //then
        assertThat(result, empty());
    }

    @Test
    public void shouldUpdateDB() throws ExecutionException, InterruptedException {
        //given
        ImmutableGrade oldGrade = EntityTemplates.grade()
                .withGrade("4");
        data.upsert(oldGrade);
        Grade newGrade = oldGrade.withGrade("5");
        mockApiClient(newGrade);

        //when
        updateHelper.reload(Grade.class)
                .toList()
                .blockingGet();

        //then
        List<Grade> res = data.select(Grade.class).get().toList();
        assertThat(res, contains(newGrade));
    }

    @Test
    public void shouldConsiderUpdatable() throws InterruptedException {
        LastUpdate lastUpdate = LastUpdate.of(LibrusColor.class, LocalDate.now().minusDays(40));
        data.upsert(lastUpdate);

        updateHelper.shouldUpdate(LibrusColor.class)
                .test()
                .await()
                .assertValueCount(1);
    }

    @Test
    public void shouldNotConsiderUpdatable() throws InterruptedException {
        LastUpdate lastUpdate = LastUpdate.of(LibrusColor.class, LocalDate.now().minusDays(20));
        data.upsert(lastUpdate);

        updateHelper.shouldUpdate(LibrusColor.class)
                .test()
                .await()
                .assertValueCount(0);
    }

    private void mockApiClient(Grade... grades) {
        Mockito.when(apiClient.getAll(eq(Grade.class)))
                .thenReturn(Observable.fromArray(grades));
    }
}
