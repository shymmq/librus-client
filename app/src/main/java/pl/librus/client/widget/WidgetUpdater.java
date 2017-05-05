package pl.librus.client.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import pl.librus.client.R;

/**
 * Created by robwys on 05/05/2017.
 */

public class WidgetUpdater {

    private final Context context;

    @Inject
    public WidgetUpdater(Context context) {
        this.context = context;
    }

    public void updateLuckyNumber() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, LuckyNumberWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget);
        Intent intent = new Intent(context, LuckyNumberWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(intent);
    }
}
