package com.example.android.coachescorner.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Content Provider Helper for Coaches Corner
 */

public class CoachesCornerDBHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_GAME_TABLE =
            "CREATE TABLE " + CoachesCornerDBContract.GameDBEntry.TABLE_NAME + " (" +
                    CoachesCornerDBContract.GameDBEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CoachesCornerDBContract.GameDBEntry.COLUMN_GAMEDATE + " TEXT NOT NULL, " +
                    CoachesCornerDBContract.GameDBEntry.COLUMN_GAMETIME + " TEXT NOT NULL, " +
                    CoachesCornerDBContract.GameDBEntry.COLUMN_OPPONENTNAME + " TEXT NOT NULL, " +
                    CoachesCornerDBContract.GameDBEntry.COLUMN_OPPONENTSCORE + " INTEGER DEFAULT 0, " +
                    CoachesCornerDBContract.GameDBEntry.COLUMN_HOMEORAWAY + " TEXT, " +
                    CoachesCornerDBContract.GameDBEntry.COLUMN_GAMENOTE + " TEXT, " +
                    CoachesCornerDBContract.GameDBEntry.COLUMN_FIELDLOCATION + " TEXT);";

    private static final String SQL_CREATE_PLAYER_TABLE =
            "CREATE TABLE " + CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + " (" +
                    CoachesCornerDBContract.PlayerDBEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERFIRSTNAME + " TEXT NOT NULL, " +
                    CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERLASTNAME + " TEXT NOT NULL, " +
                    CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERNUM + " TEXT, " +
                    CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERPIC + " BLOB, " +
                    CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERNOTE + " TEXT);";

    private static final String SQL_CREATE_SCORE_TABLE =
            "CREATE TABLE " + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME + " (" +
                    CoachesCornerDBContract.ScoreDBEntry.COLUMN_GAMEID + " INTEGER NOT NULL, " +
                    CoachesCornerDBContract.ScoreDBEntry.COLUMN_PLAYERID + " INTEGER NOT NULL, " +
                    CoachesCornerDBContract.ScoreDBEntry.COLUMN_GOALCOUNT + " INTEGER DEFAULT 0, " +
                    CoachesCornerDBContract.ScoreDBEntry.COLUMN_ASSITCOUNT + " INTEGER DEFAULT 0, " +
                    CoachesCornerDBContract.ScoreDBEntry.COLUMN_SAVESCOUNT + " INTEGER DEFAULT 0, " +
                    "PRIMARY KEY (" + CoachesCornerDBContract.ScoreDBEntry.COLUMN_GAMEID + ", " +
                                        CoachesCornerDBContract.ScoreDBEntry.COLUMN_PLAYERID + "), " +
                    "UNIQUE (" + CoachesCornerDBContract.ScoreDBEntry.COLUMN_GAMEID + ", " +
                    CoachesCornerDBContract.ScoreDBEntry.COLUMN_PLAYERID + ") ON CONFLICT REPLACE);";

    private static final String SQL_DELETE_GAME_TABLE = "DROP TABLE IF EXISTS " + CoachesCornerDBContract.GameDBEntry.TABLE_NAME;
    private static final String SQL_DELETE_PLAYER_TABLE = "DROP TABLE IF EXISTS " + CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME;
    private static final String SQL_DELETE_SCORE_TABLE = "DROP TABLE IF EXISTS " + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME;

    private static final String DATABASE_NAME = "CoachesCorner.db";
    private static final int DATABASE_VER = 14;

    public CoachesCornerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_GAME_TABLE);
        db.execSQL(SQL_CREATE_PLAYER_TABLE);
        db.execSQL(SQL_CREATE_SCORE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_GAME_TABLE);
        db.execSQL(SQL_DELETE_PLAYER_TABLE);
        db.execSQL(SQL_DELETE_SCORE_TABLE);
        onCreate(db);
    }
}
