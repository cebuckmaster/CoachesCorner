package com.example.android.coachescorner.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by cebuc on 6/24/2018.
 */

public class GameScheduleWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return (new GameScheduleListProvider(this.getApplicationContext(), intent));

    }
}
