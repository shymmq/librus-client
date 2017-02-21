package pl.librus.client.db;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.concurrent.ExecutionException;

import java8.util.concurrent.CompletableFuture;
import pl.librus.client.api.APIClient;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.ImmutableGrade;
import pl.librus.client.sql.ImmutableEntityChange;
import pl.librus.client.sql.UpdateHelper;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
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
        APIClient client = mockApiClient(newGrade);

        //when
        new UpdateHelper(client).reload(Grade.class)
                .thenAccept(changed -> assertThat(changed, contains(
                        ImmutableEntityChange.of(ADDED, newGrade))))
                .get();
    }

    @Test
    public void shouldNotDiscoverAnything() throws ExecutionException, InterruptedException {
        //given
        Grade grade = EntityTemplates.grade();
        APIClient client = mockApiClient(grade);
        data.upsert(grade);

        //when
        new UpdateHelper(client).reload(Grade.class)
                .thenAccept(changed -> assertThat(changed, empty()))
                .get();
    }

    @Test
    public void shouldDiscoverChange() throws ExecutionException, InterruptedException {
        //given
        ImmutableGrade oldGrade = EntityTemplates.grade()
                .withGrade("4");
        data.upsert(oldGrade);
        Grade newGrade = oldGrade.withGrade("5");
        APIClient client = mockApiClient(newGrade);

        //when
        new UpdateHelper(client).reload(Grade.class)
                .thenAccept(changed -> assertThat(changed, contains(
                        ImmutableEntityChange.of(CHANGED, newGrade))))
                .get();
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

        APIClient client = mockApiClient(newGrade, changedGrade);

        //when
        new UpdateHelper(client).reload(Grade.class)
                .thenAccept(changed -> assertThat(changed, containsInAnyOrder(
                        ImmutableEntityChange.of(CHANGED, changedGrade),
                        ImmutableEntityChange.of(ADDED, newGrade))))
                .get();
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

        APIClient client = mockApiClient(grade);

        //when
        new UpdateHelper(client).reload(Grade.class)
                .thenAccept(changed -> assertThat(changed, empty()))
                .get();
    }

    @Test
    public void shouldUpdateDB() throws ExecutionException, InterruptedException {
        //given
        ImmutableGrade oldGrade = EntityTemplates.grade()
                .withGrade("4");
        data.upsert(oldGrade);
        Grade newGrade = oldGrade.withGrade("5");
        APIClient client = mockApiClient(newGrade);

        //when
        new UpdateHelper(client).reload(Grade.class).get();

        //then
        List<Grade> res = data.select(Grade.class).get().toList();
        assertThat(res, contains(newGrade));

    }

    private APIClient mockApiClient(Grade... grades) {
        APIClient mock = mock(APIClient.class);
        Mockito.when(mock.getAll(eq(Grade.class)))
                .thenReturn(CompletableFuture.completedFuture(Lists.newArrayList(grades)));
        return mock;
    }
}
