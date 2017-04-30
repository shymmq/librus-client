package pl.librus.client.db;

import com.google.common.base.Optional;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;

import io.reactivex.Observable;
import io.reactivex.Single;
import pl.librus.client.domain.ImmutableLuckyNumber;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.lesson.FullLesson;
import pl.librus.client.domain.lesson.ImmutableFullLesson;
import pl.librus.client.domain.lesson.Lesson;

/**
 * Created by robwys on 30/04/2017.
 */

public class LibrusDataTest extends BaseDBTest {

    @Test
    public void shouldHandleMissingLuckyNumber() throws InterruptedException {
        //given
        Mockito.when(apiClient.getAll(LuckyNumber.class)).thenReturn(Observable.empty());

        //when
        librusData.findLuckyNumber()
                .test()
                .await()
                //then
                .assertValue(Optional.absent());
    }

    @Test
    public void shouldHandleDatabaseLuckyNumber() throws InterruptedException {
        //given
        LuckyNumber luckyNumber = ImmutableLuckyNumber.of(LocalDate.now(), 12);

        Mockito.when(apiClient.getAll(LuckyNumber.class)).thenReturn(Observable.empty());

        data.upsert(luckyNumber);

        //when
        librusData.findLuckyNumber()
                .test()
                .await()
                //then
                .assertValue(Optional.of(luckyNumber));
    }

    @Test
    public void shouldHandleServerLuckyNumber() throws InterruptedException {
        //given
        LuckyNumber luckyNumber = ImmutableLuckyNumber.of(LocalDate.now(), 16);

        Mockito.when(apiClient.getAll(LuckyNumber.class)).thenReturn(Observable.just(luckyNumber));

        //when
        librusData.findLuckyNumber()
                .test()
                .await()
                //then
                .assertValue(Optional.of(luckyNumber));
    }

    @Test
    public void shouldHandleMissingDatabaseId() throws InterruptedException {
        //given
        String teacherId = "123";
        Teacher t = EntityTemplates.teacher()
                .withId(teacherId);

        Lesson l = EntityTemplates.lesson()
                .withOrgTeacherId(teacherId);

        Mockito.when(apiClient.getById(Mockito.eq(Teacher.class), Mockito.anyString()))
                .thenReturn(Single.just(t));

        FullLesson expected = ImmutableFullLesson.builder()
                .from(l)
                .date(l.date())
                .orgTeacher(t)
                .build();

        //when
        librusData.makeFullLesson(l)
                .test()
                .await()
                //then
                .assertValue(expected);
    }
}
