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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.librus.client.R;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.ui.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by szyme on 17.12.2016. librus-client
 */

public class NotificationService {
    public static final String DEFAULT_POSITION = "NotificationService:redirect_fragment";
    private static final String TAG = "librus-client-log";
    private Context context;
    private LibrusData data;


    NotificationService(Context context, LibrusData data) {
        this.context = context;
        this.data = data;
    }

    private void sendNotification(@NonNull CharSequence title, @NonNull CharSequence text, int iconResource, @Nullable CharSequence subtext, @Nullable Notification.Style style, int fragment) {
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(iconResource)
                .setAutoCancel(true);
        if (subtext != null) builder.setSubText(subtext);
        if (style != null) builder.setStyle(style);

        Bundle bundle = new Bundle();
        bundle.putInt(DEFAULT_POSITION, fragment);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(DEFAULT_POSITION, fragment);
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
                    .setBigContentTitle(announcement.getSubject())
                    .bigText(announcement.getContent())
                    .setSummaryText(data.getAccount().getLogin() + " - " + data.getAccount().getName());
            sendNotification(announcement.getSubject(), announcement.getContent(), R.drawable.ic_announcement_black_48dp, null, style, MainActivity.FRAGMENT_ANNOUNCEMENTS_ID);
        } else if (size > 1) {
            String title;
            List<String> authors = new ArrayList<>();
            int authorsLength = 0;
            if (2 <= size && size <= 4) title = size + " nowe ogłoszenia";
            else if (5 <= size) title = size + " nowych ogłoszeń";
            else title = "Nowe ogłoszenia: " + size;
            Notification.InboxStyle style = new Notification.InboxStyle()
                    .setBigContentTitle(title)
                    .setSummaryText(data.getAccount().getLogin() + " - " + data.getAccount().getName());
            for (Announcement a : announcements) {
                style.addLine(a.getSubject());
                Teacher author = data.getTeacherMap().get(a.getAuthorId());
                if (authorsLength + author.getName().length() < 40) {
                    authors.add(author.getName());
                }
            }
            sendNotification(title, TextUtils.join(", ", authors), R.drawable.ic_announcement_black_48dp, null, style, MainActivity.FRAGMENT_ANNOUNCEMENTS_ID);
        }
        return this;
    }

    NotificationService addGrades(List<Grade> grades) {
        //Create notification
        int size = grades.size();
        if (size == 1) {
            Grade grade = grades.get(0);
            String subject = data.getSubjectMap().get(grade.getSubject().getId()).getName();
            sendNotification("Nowa ocena", subject + " " + grade.getGrade(), R.drawable.ic_assignment_black_48dp, null, null, MainActivity.FRAGMENT_GRADES_ID);
        } else if (size > 1) {
            String title;
            List<String> subjects = new ArrayList<>();
            if (2 <= size && size <= 4) title = size + " nowe oceny";
            else if (5 <= size) title = size + " nowych ocen";
            else title = "Nowe oceny: " + size;
            Notification.InboxStyle style = new Notification.InboxStyle()
                    .setBigContentTitle(title)
                    .setSummaryText(data.getAccount().getLogin() + " - " + data.getAccount().getName());
            for (Grade g : grades) {
//                String category = data.getGradeCategoriesMap().get(g.getCategoryId()).getName();
                String subject = data.getSubjectMap().get(g.getSubject().getId()).getName();
                style.addLine(g.getGrade() + " " + subject);
                if (!subjects.contains(subject))
                    subjects.add(subject);
            }
            sendNotification(title,
                    TextUtils.join(", ", subjects),
                    R.drawable.ic_assignment_black_48dp,
                    null, style
                    , MainActivity.FRAGMENT_GRADES_ID);
        }
        return this;
    }

    NotificationService addEvents(List<Event> events) {
        int size = events.size();
        if (size == 1) {
            Event event = events.get(0);
            sendNotification("Nowe wydarzenie",
                    event.getContent(),
                    R.drawable.ic_event_black_24dp,
                    event.getDate().toString("EEEE, d MMMM yyyy", new Locale("pl")),
                    null,
                    -1);
        } else if (size > 1) {
            String title;
            List<String> authorNames = new ArrayList<>();
            if (2 <= size && size <= 4) title = size + " nowe wydarzenia";
            else if (5 <= size) title = size + " nowych wydarzeń";
            else title = "Nowe wydarzenia: " + size;

            Notification.InboxStyle style = new Notification.InboxStyle()
                    .setBigContentTitle(title)
                    .setSummaryText(data.getAccount().getLogin() + " - " + data.getAccount().getName());
            Map<String, Teacher> teacherMap = data.getTeacherMap();
            for (Event e : events) {
                style.addLine(e.getContent());
                String name = teacherMap.get(e.getAddedBy().getId()).getName();
                if (!authorNames.contains(name)) authorNames.add(name);
            }

            sendNotification(title,
                    TextUtils.join(", ", authorNames),
                    R.drawable.ic_event_black_24dp,
                    null, style,
                    -1);
        }
        return this;
    }

    NotificationService addLuckyNumber(LuckyNumber ln) {
        if (ln == null) return this;
        sendNotification(
                "Szczęśliwy numerek: " + ln.getLuckyNumber(),
                ln.getLuckyNumberDay().toString("EEEE, d MMMM yyyy", new Locale("pl")),
                R.drawable.ic_sentiment_very_satisfied_black_24dp,
                null, null, -1);
        return this;
    }
}
