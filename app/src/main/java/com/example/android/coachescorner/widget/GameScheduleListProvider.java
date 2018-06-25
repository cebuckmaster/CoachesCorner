package com.example.android.coachescorner.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.common.Utils;
import com.example.android.coachescorner.data.CoachesCornerDBContract;
import com.example.android.coachescorner.data.Game;

import java.util.ArrayList;

/**
 * Created by cebuc on 6/24/2018.
 */

public class GameScheduleListProvider implements RemoteViewsService.RemoteViewsFactory {


    private ArrayList<Game> mGames;
    private String mTeamName;
    private Context mContext = null;

    public GameScheduleListProvider(Context context, Intent intent) {
        mContext = context;

        loadGameSchedule();
    }

    private void loadGameSchedule() {

        mTeamName = Utils.getTeamName(mContext);

        Uri gameUri = CoachesCornerDBContract.GameDBEntry.CONTENT_URI;

        Cursor cursor = mContext.getContentResolver().query(
                gameUri,
                null,
                null,
                null,
                null);

        mGames = new ArrayList<Game>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int gameIdIndex = cursor.getColumnIndex(CoachesCornerDBContract.GameDBEntry._ID);
            int gameDateIndex = cursor.getColumnIndex(CoachesCornerDBContract.GameDBEntry.COLUMN_GAMEDATE);
            int gameTimeIndex = cursor.getColumnIndex(CoachesCornerDBContract.GameDBEntry.COLUMN_GAMETIME);
            int opponentNameIndex = cursor.getColumnIndex(CoachesCornerDBContract.GameDBEntry.COLUMN_OPPONENTNAME);
            int opponentScoreIndex = cursor.getColumnIndex(CoachesCornerDBContract.GameDBEntry.COLUMN_OPPONENTSCORE);
            int homeOrAwayIndex = cursor.getColumnIndex(CoachesCornerDBContract.GameDBEntry.COLUMN_HOMEORAWAY);
            int teamScoreIndex = cursor.getColumnIndex(CoachesCornerDBContract.ScoreDBEntry.COLUMN_GOALCOUNT);

            mGames.add(new Game(cursor.getInt(gameIdIndex),
                    cursor.getString(gameDateIndex),
                    cursor.getString(gameTimeIndex),
                    cursor.getString(opponentNameIndex),
                    cursor.getInt(opponentScoreIndex),
                    cursor.getInt(teamScoreIndex),
                    cursor.getString(homeOrAwayIndex),
                    "",
                    ""));

            while (cursor.moveToNext()) {
                mGames.add(new Game(cursor.getInt(gameIdIndex),
                        cursor.getString(gameDateIndex),
                        cursor.getString(gameTimeIndex),
                        cursor.getString(opponentNameIndex),
                        cursor.getInt(opponentScoreIndex),
                        cursor.getInt(teamScoreIndex),
                        cursor.getString(homeOrAwayIndex),
                        "",
                        ""));
            }

            cursor.close();
        }


    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mGames == null) {
            return 0;
        }
        return mGames.size();

    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.game_schedule_list_item);

        remoteViews.setTextViewText(R.id.tv_widget_game_date, mGames.get(position).getGameDate());
        remoteViews.setTextViewText(R.id.tv_widget_game_time, mGames.get(position).getGameTime());
        if (mGames.get(position).getHomeOrAway().equals("Home")) {
            remoteViews.setTextViewText(R.id.tv_widget_home_team_name, mTeamName);
            remoteViews.setTextViewText(R.id.tv_widget_away_team_name, mGames.get(position).getOpponentName());
        } else {
            remoteViews.setTextViewText(R.id.tv_widget_home_team_name, mGames.get(position).getOpponentName());
            remoteViews.setTextViewText(R.id.tv_widget_away_team_name, mTeamName);
        }

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
