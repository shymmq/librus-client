package pl.librus.client.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.google.common.base.Optional;

import javax.inject.Inject;

import pl.librus.client.MainApplication;
import pl.librus.client.R;
import pl.librus.client.UserComponent;
import pl.librus.client.data.LibrusData;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.ui.MainActivity;

/**
 * Created by robwys on 04/05/2017.
 */

public class LuckyNumberWidgetProvider extends AppWidgetProvider {

    @Inject
    LibrusData data;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];

            appWidgetManager.updateAppWidget(widgetId, prepareWidget(context));
        }
    }

    private RemoteViews prepareWidget(Context context) {
        Optional<UserComponent> userComponent = MainApplication.getOrCreateUserComponent(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_lucky_number);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
        if (userComponent.isPresent()) {
            setViewsVisibility(remoteViews, View.VISIBLE, View.GONE);

            userComponent.get().inject(this);
            Optional<LuckyNumber> luckyNumberOptional = data.findLuckyNumber()
                    .blockingGet();
            if (luckyNumberOptional.isPresent()) {
                remoteViews.setTextViewText(R.id.lucky_number, String.valueOf(luckyNumberOptional.get().luckyNumber()));
                remoteViews.setTextViewText(R.id.lucky_number_date, luckyNumberOptional.get().day().toString("EEEE"));
            } else {
                setViewsVisibility(remoteViews, View.GONE, View.VISIBLE);
                remoteViews.setTextViewText(R.id.log_in, context.getString(R.string.no_lucky_number));
            }
        } else {
            setViewsVisibility(remoteViews, View.GONE, View.VISIBLE);
            remoteViews.setTextViewText(R.id.log_in, context.getString(R.string.log_in));
        }

        return remoteViews;
    }

    private void setViewsVisibility(RemoteViews remoteViews, int normalViews, int errorViews) {
        remoteViews.setViewVisibility(R.id.lucky_number, normalViews);
        remoteViews.setViewVisibility(R.id.lucky_number_icon, normalViews);
        remoteViews.setViewVisibility(R.id.lucky_number_date, normalViews);
        remoteViews.setViewVisibility(R.id.log_in, errorViews);
    }

    private String toString(Optional<LuckyNumber> oln) {
        return oln.transform(LuckyNumber::luckyNumber)
                .transform(Object::toString)
                .or("Brak");
    }
}
