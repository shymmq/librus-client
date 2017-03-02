package pl.librus.client;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.common.collect.Iterables;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ResourceHelper;
import org.robolectric.shadows.ShadowNotification;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Single;
import java8.util.function.Consumer;
import java8.util.stream.IntStreams;
import pl.librus.client.api.APIClient;
import pl.librus.client.api.IAPIClient;
import pl.librus.client.api.LibrusGcmListenerService;
import pl.librus.client.api.NotificationService;
import pl.librus.client.datamodel.Announcement;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.db.BaseDBTest;
import pl.librus.client.db.EntityTemplates;
import pl.librus.client.sql.UpdateHelper;

import static com.google.common.collect.Lists.newArrayList;
import static java8.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.LOLLIPOP)
public class NotificationTest extends BaseDBTest {

    private NotificationManager notificationManager;

    @Before
    public void setUp() {
        notificationManager = (NotificationManager) RuntimeEnvironment.application.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Test
    public void shouldNotifyAboutNewGrade() throws ExecutionException, InterruptedException {
        //given
        Subject subject = EntityTemplates.subject();
        Grade newGrade = EntityTemplates.grade()
                .withSubject(subject.id());
        LibrusGcmListenerService service = serviceWithMockClient();
        addMockGrades(newGrade);
        data.insert(subject);

        //when
        service.onMessageReceived(null, mockBundle());
        service.getReloads().blockingIterable();

        //then
        ShadowNotification notification = shadowOf(singleNotification());
        assertThat(notification.getContentTitle(), is("Nowa ocena"));
        assertThat(notification.getContentText(), is("Matematyka 4+"));
    }

    @Test
    public void shouldNotifyAboutMultipleGrades() throws ExecutionException, InterruptedException {
        //given
        Subject matematyka = EntityTemplates.subject()
                .withId("1")
                .withName("Matematyka");
        Grade grade1 = EntityTemplates.grade()
                .withGrade("4+")
                .withSubject(matematyka.id());

        Subject informatyka = EntityTemplates.subject()
                .withId("2")
                .withName("Informatyka");
        Grade grade2 = EntityTemplates.grade()
                .withGrade("5")
                .withSubject(informatyka.id());

        LibrusGcmListenerService service = serviceWithMockClient();
        addMockGrades(grade1, grade2);
        data.insert(matematyka);
        data.insert(informatyka);

        //when
        service.onMessageReceived(null, mockBundle());
        service.getReloads().blockingIterable();

        //then
        Notification realNotification = singleNotification();
        ShadowNotification notification = shadowOf(realNotification);

        assertThat(notification.getContentTitle(), is("2 nowe oceny"));
        assertThat(notification.getContentText().toString(), is("Matematyka, Informatyka"));
        assertThat(notification.getBigContentTitle(), is("2 nowe oceny"));
        assertThat(getInboxLine(realNotification, 0), is("Matematyka 4+"));
        assertThat(getInboxLine(realNotification, 1), is("Informatyka 5"));
    }

    @Test
    public void shouldNotifyAboutNewAnnouncement() throws ExecutionException, InterruptedException {
        //given
        Announcement newAnnouncement = EntityTemplates.announcement();
        LibrusGcmListenerService service = serviceWithMockClient();
        addMockAnnouncements(newAnnouncement);

        //when
        service.onMessageReceived(null, mockBundle());
        service.getReloads().blockingIterable();

        //then
        ShadowNotification notification = shadowOf(singleNotification());
        assertThat(notification.getContentTitle(), is("Tytuł ogłoszenia"));
        assertThat(notification.getContentText(), is("Treść ogłoszenia"));
        assertThat(notification.getBigContentTitle(), is("Tytuł ogłoszenia"));
        assertThat(notification.getBigText(), is("Treść ogłoszenia"));
    }

    @Test
    public void shouldNotifyAboutNewAnnouncements() throws ExecutionException, InterruptedException {
        //given
        Announcement[] newAnnouncements = IntStreams.range(1, 11)
                .mapToObj(index -> EntityTemplates.announcement()
                        .withId(String.valueOf(index))
                        .withSubject("Ogłoszenie #" + index))
                .toArray(Announcement[]::new);
        LibrusGcmListenerService service = serviceWithMockClient();

        addMockAnnouncements(newAnnouncements);

        //when
        service.onMessageReceived(null, mockBundle());
        service.getReloads().blockingIterable();

        //then
        Notification realNotification = singleNotification();
        ShadowNotification notification = shadowOf(realNotification);

        assertThat(notification.getContentTitle(), is("10 nowych ogłoszeń"));
        assertThat(notification.getContentText().toString(), startsWith("Ogłoszenie #1, Ogłoszenie #2"));
        assertThat(notification.getBigContentTitle(), is("10 nowych ogłoszeń"));
        assertThat(getInboxLine(realNotification, 0), is("Ogłoszenie #1"));
        assertThat(getInboxLine(realNotification, 1), is("Ogłoszenie #2"));
    }

    @Test
    public void shouldNotifyAboutLuckyNumber() throws ExecutionException, InterruptedException {
        //given
        LuckyNumber luckyNumber = EntityTemplates.luckyNumber();
        LibrusGcmListenerService service = serviceWithMockClient();

        addMockLuckyNumber(luckyNumber);

        //when
        service.onMessageReceived(null, mockBundle());
        service.getReloads().blockingIterable();

        //then
        ShadowNotification notification = shadowOf(singleNotification());

        assertThat(notification.getContentTitle(), is("Szczęśliwy numerek: 17"));
        assertThat(notification.getContentText().toString(), is("środa, 14 czerwca 2017"));
    }

    @Test
    public void shouldNotifyAboutNewEvent() throws ExecutionException, InterruptedException {
        //given
        Teacher teacher = EntityTemplates.teacher();

        Event event = EntityTemplates.event()
                .withAddedBy(teacher.id());

        LibrusGcmListenerService service = serviceWithMockClient();
        addMockEvents(event);
        data.insert(teacher);

        //when
        service.onMessageReceived(null, mockBundle());
        service.getReloads().blockingIterable();

        //then
        ShadowNotification notification = shadowOf(singleNotification());
        assertThat(notification.getContentTitle(), is("Nowe wydarzenie"));
        assertThat(notification.getContentText(), is("Praca klasowa - piątek, 7 października 2016"));
    }

    @Test
    public void shouldNotifyAboutMultipleEvents() throws ExecutionException, InterruptedException {
        //given
        Teacher teacher1 = EntityTemplates.teacher()
                .withId("1")
                .withFirstName("Ala").withLastName("Makota");

        Event event1 = EntityTemplates.event()
                .withContent("Praca klasowa")
                .withDate(LocalDate.parse("2016-10-07"))
                .withAddedBy(teacher1.id());

        Teacher teacher2 = EntityTemplates.teacher()
                .withId("2")
                .withFirstName("Tomasz").withLastName("Problem");

        Event event2 = EntityTemplates.event()
                .withContent("Kartkówka")
                .withDate(LocalDate.parse("2016-10-10"))
                .withAddedBy(teacher2.id());

        LibrusGcmListenerService service = serviceWithMockClient();
        addMockEvents(event1, event2);
        data.insert(teacher1);
        data.insert(teacher2);

        //when
        service.onMessageReceived(null, mockBundle());
        service.getReloads().blockingIterable();

        //then

        Notification realNotification = singleNotification();
        ShadowNotification notification = shadowOf(realNotification);

        assertThat(notification.getContentTitle(), is("2 nowe wydarzenia"));
        assertThat(notification.getContentText(), is("Ala Makota, Tomasz Problem"));
        assertThat(getInboxLine(realNotification, 0), is("Praca klasowa - piątek, 7 października 2016"));
        assertThat(getInboxLine(realNotification, 1), is("Kartkówka - poniedziałek, 10 października 2016"));
    }

    private String getInboxLine(Notification n, int line) {
        //noinspection deprecation
        View view = n.bigContentView.apply(RuntimeEnvironment.application, new FrameLayout(RuntimeEnvironment.application));
        int internalResourceId = ResourceHelper.getInternalResourceId("inbox_text" + line);
        TextView lineView = (TextView) view.findViewById(internalResourceId);
        return lineView.getText().toString();
    }

    private Notification singleNotification() {
        List<Notification> all = shadowOf(notificationManager).getAllNotifications();
        return Iterables.getOnlyElement(all);
    }

    private LibrusGcmListenerService serviceWithMockClient() {
        LibrusGcmListenerService service = new LibrusGcmListenerService();
        service.setFirebaseLogger(mock(Consumer.class));
        service.setNotificationService(new NotificationService(RuntimeEnvironment.application));
        when(apiClient.getAll(any()))
                .thenReturn(Single.just(Collections.emptyList()));
        return service;
    }

    private void addMockGrades(Grade... grades) {
        when(apiClient.getAll(eq(Grade.class)))
                .thenReturn(Single.just(newArrayList(grades)));
    }

    private void addMockAnnouncements(Announcement... announcements) {
        when(apiClient.getAll(eq(Announcement.class)))
                .thenReturn(Single.just(newArrayList(announcements)));
    }

    private void addMockLuckyNumber(LuckyNumber luckyNumber) {
        when(apiClient.getAll(eq(LuckyNumber.class)))
                .thenReturn(Single.just(newArrayList(luckyNumber)));
    }

    private void addMockEvents(Event... events) {
        when(apiClient.getAll(eq(Event.class)))
                .thenReturn(Single.just(newArrayList(events)));
    }

    private Bundle mockBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("objectT", "mock");
        return bundle;
    }
}
