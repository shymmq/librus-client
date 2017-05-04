package pl.librus.client.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.google.common.base.Optional;

import java.util.Random;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import pl.librus.client.MainApplication;
import pl.librus.client.R;
import pl.librus.client.UserComponent;
import pl.librus.client.data.LibrusData;
import pl.librus.client.data.UpdateHelper;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.ui.MainActivity;

/**
 * Created by robwys on 04/05/2017.
 */

public class LuckyNumberWidgetProvider extends AppWidgetProvider {

    @Inject
    LibrusData data;

    public static void updateAll(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, LuckyNumberWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget);
        Intent intent = new Intent(context, LuckyNumberWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(intent);
    }

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
        if(userComponent.isPresent()) {
            remoteViews.setViewVisibility(R.id.lucky_number, View.VISIBLE);
            userComponent.get().inject(this);
            String text = data.findLuckyNumber()
                    .map(this::toString)
                    .blockingGet();
            remoteViews.setTextViewText(R.id.lucky_number, text);
        } else {
            remoteViews.setViewVisibility(R.id.lucky_number, View.GONE);
            remoteViews.setViewVisibility(R.id.log_in, View.VISIBLE);
        }

        return remoteViews;
    }

    private String toString(Optional<LuckyNumber> oln) {
        return oln.transform(LuckyNumber::luckyNumber)
                .transform(Object::toString)
                .or("Brak");
    }
}
