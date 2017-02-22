package pl.librus.client.api;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.LibrusUtils;
import pl.librus.client.R;
import pl.librus.client.datamodel.Announcement;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.ui.MainActivity;
import pl.librus.client.ui.MainApplication;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by szyme on 17.12.2016. librus-client
 */

public class NotificationService {
    private final Context context;

    public NotificationService(Context context) {
        this.context = context;
    }

    private void sendNotification(
            @NonNull CharSequence title,
            @NonNull CharSequence text,
            int iconResource,
            @Nullable Notification.Style style,
            String fragment) {
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(iconResource)
                .setAutoCancel(true);
        if (style != null) builder.setStyle(style);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.INITIAL_FRAGMENT, fragment);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        builder.setContentIntent(PendingIntent.getActivity(context, 42, intent, PendingIntent.FLAG_UPDATE_CURRENT));

        Notification notification = builder.build();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }

    NotificationService addAnnouncements(List<Announcement> announcements) {
        int size = announcements.size();
        if (size == 1) {
            Announcement announcement = announcements.get(0);
            Notification.BigTextStyle style = new Notification.BigTextStyle()
                    .setBigContentTitle(announcement.subject())
                    .bigText(announcement.content());
            sendNotification(
                    announcement.subject(),
                    announcement.content(),
                    R.drawable.ic_announcement_black_48dp,
                    style,
                    MainActivity.FRAGMENT_ANNOUNCEMENTS);
        } else if (size > 1) {
            String title = size +
                    LibrusUtils.getPluralForm(size, " nowe ogłoszenie", " nowe ogłoszenia", " nowych ogłoszeń");
            Notification.InboxStyle style = new Notification.InboxStyle()
                    .setBigContentTitle(title);
            List<String> subjects = StreamSupport.stream(announcements)
                    .map(Announcement::subject)
                    .collect(Collectors.toList());
            StreamSupport.stream(subjects)
                    .forEach(style::addLine);
            String text = TextUtils.join(", ", subjects);
            sendNotification(title,
                    text,
                    R.drawable.ic_announcement_black_48dp,
                    style,
                    MainActivity.FRAGMENT_ANNOUNCEMENTS);
        }
        return this;
    }

    NotificationService addGrades(List<Grade> grades) {
        //Create notification
        int size = grades.size();
        if (size == 1) {
            Grade grade = grades.get(0);
            String subject = MainApplication.getData().findByKey(Subject.class, grade.subject()).name();
            sendNotification(
                    "Nowa ocena",
                    subject + " " + grade.grade(),
                    R.drawable.ic_assignment_black_48dp,
                    null,
                    MainActivity.FRAGMENT_GRADES);
        } else if (size > 1) {
            String title;
            List<String> subjects = new ArrayList<>();
            if (2 <= size && size <= 4) title = size + " nowe oceny";
            else if (5 <= size) title = size + " nowych ocen";
            else title = "Nowe oceny: " + size;
            Notification.InboxStyle style = new Notification.InboxStyle()
                    .setBigContentTitle(title);
            for (Grade g : grades) {
                String subject = MainApplication.getData().findByKey(Subject.class, g.subject()).name();
                style.addLine(subject + " " + g.grade());
                if (!subjects.contains(subject))
                    subjects.add(subject);
            }
            sendNotification(title,
                    TextUtils.join(", ", subjects),
                    R.drawable.ic_assignment_black_48dp,
                    style,
                    MainActivity.FRAGMENT_GRADES);
        }
        return this;
    }

    NotificationService addEvents(List<Event> events) {

        int size = events.size();
        if (size == 1) {
            Event event = events.get(0);
            String date = event.date().toString("EEEE, d MMMM yyyy", new Locale("pl"));
            sendNotification("Nowe wydarzenie",
                    event.content() + " - " + date,
                    R.drawable.ic_event_black_24dp,
                    null,
                    null);
        } else if (size > 1) {
            String title;
            List<String> authorNames = new ArrayList<>();
            if (2 <= size && size <= 4) title = size + " nowe wydarzenia";
            else if (5 <= size) title = size + " nowych wydarzeń";
            else title = "Nowe wydarzenia: " + size;

            Notification.InboxStyle style = new Notification.InboxStyle()
                    .setBigContentTitle(title);
            for (Event e : events) {
                String date = e.date().toString("EEEE, d MMMM yyyy", new Locale("pl"));
                style.addLine(e.content() + " - " + date);
                String name = MainApplication.getData().findByKey(Teacher.class, e.addedBy()).name();
                if (!authorNames.contains(name)) authorNames.add(name);
            }

            sendNotification(title,
                    TextUtils.join(", ", authorNames),
                    R.drawable.ic_event_black_24dp,
                    style,
                    null);
        }
        return this;
    }

    NotificationService addLuckyNumber(List<LuckyNumber> luckyNumbers) {
        if (luckyNumbers == null || luckyNumbers.isEmpty()) return this;
        LuckyNumber ln = Iterables.getOnlyElement(luckyNumbers);
        sendNotification(
                "Szczęśliwy numerek: " + ln.luckyNumber(),
                ln.day().toString("EEEE, d MMMM yyyy", new Locale("pl")),
                R.drawable.ic_sentiment_very_satisfied_black_24dp, null, null);
        return this;
    }
}
