package com.example.android.coachescorner.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.common.Utils;
import com.example.android.coachescorner.ui.CoachesCornerActivity;

/**
 * Created by cebuc on 6/24/2018.
 */

public class GameScheduleWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews remoteViews = updateWidgetListView(context, appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_listview_layout);

        Intent svcIntent = new Intent(context, GameScheduleWidgetService.class);
        remoteViews.setRemoteAdapter(R.id.listViewWidget, svcIntent);

        String teamName = Utils.getTeamName(context);

        if (teamName.equals(Utils.DEFAULT_TEAM_NAME)) {
            remoteViews.setTextViewText(R.id.tv_widget_team_name, context.getResources().getString(R.string.no_schedule));
        } else {
            remoteViews.setTextViewText(R.id.tv_widget_team_name, teamName + " Game Schedule ");
        }

        Intent appIntent = new Intent(context, CoachesCornerActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);

        remoteViews.setOnClickPendingIntent(R.id.tv_widget_team_name, appPendingIntent);

        remoteViews.setEmptyView(R.id.listViewWidget, R.id.tv_empty_widget_message);

        return remoteViews;


    }

}
