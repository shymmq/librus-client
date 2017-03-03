package pl.librus.client.db;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Single;
import pl.librus.client.datamodel.grade.Grade;
import pl.librus.client.datamodel.grade.ImmutableGrade;
import pl.librus.client.sql.EntityChange;
import pl.librus.client.sql.ImmutableEntityChange;
import pl.librus.client.sql.UpdateHelper;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static pl.librus.client.sql.EntityChange.Type.ADDED;
import static pl.librus.client.sql.EntityChange.Type.CHANGED;

@SuppressWarnings("unchecked")
@RunWith(RobolectricTestRunner.class)
public class ReloadingTest extends BaseDBTest {
    @Test
    public void shouldDiscoverNewEntity() throws ExecutionException, InterruptedException {
        //given
        Grade newGrade = EntityTemplates.grade();
        mockApiClient(newGrade);

        //when
        List<EntityChange<Grade>> result = UpdateHelper.reload(Grade.class).blockingGet();

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
        List<EntityChange<Grade>> result = UpdateHelper.reload(Grade.class).blockingGet();

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
        List<EntityChange<Grade>> result = UpdateHelper.reload(Grade.class).blockingGet();

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
        List<EntityChange<Grade>> result = UpdateHelper.reload(Grade.class).blockingGet();

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
        List<EntityChange<Grade>> result = UpdateHelper.reload(Grade.class).blockingGet();

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
        UpdateHelper.reload(Grade.class).blockingGet();

        //then
        List<Grade> res = data.select(Grade.class).get().toList();
        assertThat(res, contains(newGrade));

    }

    private void mockApiClient(Grade... grades) {
        Mockito.when(apiClient.getAll(eq(Grade.class)))
                .thenReturn(Single.just(Lists.newArrayList(grades)));
    }
}
