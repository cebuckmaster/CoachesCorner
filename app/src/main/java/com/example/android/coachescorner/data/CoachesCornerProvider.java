package com.example.android.coachescorner.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Content Provider used to access the sqlite database for Coaches Corner
 */

public class CoachesCornerProvider extends ContentProvider {

    private static final int COACHESCORNERGAME = 100;
    private static final int COACHESCORNERGAME_ID = 101;
    private static final int COACHESCORNERPLAYER = 200;
    private static final int COACHESCORNERPLAYER_ID = 201;
    private static final int COACHESCORNERSCORE = 300;
    private static final int COACHESCORNERPLAYERSGAMEDAY = 306;
    private static final int COACHESCORNERSCORE_ID = 301;
    private static final int COACHESCORNERSCORE_GAMEID = 302;
    private static final int COACHESCORNERSCORE_PLAYERID = 303;
    private static final int COACHESCORNERSCORE_GAMEANDPLAYERID = 304;

    private static final UriMatcher sUriMather = buildUriMather();

    public static final String TAG = CoachesCornerProvider.class.getSimpleName();

    private CoachesCornerDBHelper mCoachesCornerDBHelper;

    public static UriMatcher buildUriMather() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CoachesCornerDBContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, CoachesCornerDBContract.PATH_GAME, COACHESCORNERGAME);
        matcher.addURI(authority, CoachesCornerDBContract.PATH_GAME + "/#", COACHESCORNERGAME_ID);
        matcher.addURI(authority, CoachesCornerDBContract.PATH_PLAYER, COACHESCORNERPLAYER);
        matcher.addURI(authority, CoachesCornerDBContract.PATH_PLAYER + "/#", COACHESCORNERPLAYER_ID);
        matcher.addURI(authority, CoachesCornerDBContract.PATH_SCORE, COACHESCORNERSCORE);
        matcher.addURI(authority, CoachesCornerDBContract.PLAYERGAMEDAY, COACHESCORNERPLAYERSGAMEDAY);
        matcher.addURI(authority, CoachesCornerDBContract.PATH_SCORE + "/#", COACHESCORNERSCORE_ID);
        matcher.addURI(authority, CoachesCornerDBContract.SCOREPLAYERID + "/#", COACHESCORNERSCORE_PLAYERID);

        return matcher;

    }

    @Override
    public boolean onCreate() {
        mCoachesCornerDBHelper = new CoachesCornerDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mCoachesCornerDBHelper.getReadableDatabase();

        String sql = "";

        Cursor cursor;

        int match = sUriMather.match(uri);

        switch (match) {
            case COACHESCORNERGAME:
                sql = "SELECT " + CoachesCornerDBContract.GameDBEntry.TABLE_NAME +  "." + CoachesCornerDBContract.GameDBEntry._ID + ", " +
                        CoachesCornerDBContract.GameDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.GameDBEntry.COLUMN_GAMEDATE + ", " +
                        CoachesCornerDBContract.GameDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.GameDBEntry.COLUMN_GAMETIME + ", " +
                        CoachesCornerDBContract.GameDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.GameDBEntry.COLUMN_OPPONENTNAME + ", " +
                        CoachesCornerDBContract.GameDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.GameDBEntry.COLUMN_OPPONENTSCORE + ", " +
                        CoachesCornerDBContract.GameDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.GameDBEntry.COLUMN_HOMEORAWAY + ", " +
                        CoachesCornerDBContract.GameDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.GameDBEntry.COLUMN_GAMENOTE + ", " +
                        CoachesCornerDBContract.GameDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.GameDBEntry.COLUMN_FIELDLOCATION + ", " +
                        "IFNULL(SUM(" + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.ScoreDBEntry.COLUMN_GOALCOUNT + "), 0) AS " +
                        CoachesCornerDBContract.ScoreDBEntry.COLUMN_GOALCOUNT +
                        " FROM " + CoachesCornerDBContract.GameDBEntry.TABLE_NAME +
                        " LEFT JOIN " + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME +
                        " ON " + CoachesCornerDBContract.GameDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.GameDBEntry._ID +
                        " = " + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.ScoreDBEntry.COLUMN_GAMEID +
                        " GROUP BY " + CoachesCornerDBContract.GameDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.GameDBEntry._ID +
                        " ORDER BY " + CoachesCornerDBContract.GameDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.GameDBEntry.COLUMN_GAMEDATE + " ASC;";

                cursor = db.rawQuery(sql, null);
                break;
            case COACHESCORNERGAME_ID:
                selection = CoachesCornerDBContract.GameDBEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(CoachesCornerDBContract.GameDBEntry.TABLE_NAME,
                                    projection,
                                    selection,
                                    selectionArgs,
                                    null,
                                    null,
                                    sortOrder);
                break;
            case COACHESCORNERPLAYER:
                sql = "SELECT " + CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry._ID + ", " +
                        CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERFIRSTNAME + ", " +
                        CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERLASTNAME + ", " +
                        CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERNUM + ", " +
                        CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERPIC + ", " +
                        "IFNULL(SUM(" + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.ScoreDBEntry.COLUMN_GOALCOUNT + "), 0) AS " +
                        CoachesCornerDBContract.ScoreDBEntry.COLUMN_GOALCOUNT + ", " +
                        "IFNULL(SUM(" + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.ScoreDBEntry.COLUMN_ASSITCOUNT + "), 0) AS " +
                        CoachesCornerDBContract.ScoreDBEntry.COLUMN_ASSITCOUNT + ", " +
                        "IFNULL(SUM(" + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.ScoreDBEntry.COLUMN_SAVESCOUNT + "), 0) AS " +
                        CoachesCornerDBContract.ScoreDBEntry.COLUMN_SAVESCOUNT + ", " +
                        CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERNOTE +
                        " FROM " + CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME +
                        " LEFT JOIN " + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME +
                        " ON " + CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry._ID +
                        " = " + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.ScoreDBEntry.COLUMN_PLAYERID +
                        " GROUP BY " + CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry._ID + ";";
                cursor = db.rawQuery(sql, null);

                break;
            case COACHESCORNERPLAYER_ID:
                selection = CoachesCornerDBContract.PlayerDBEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME,
                                    projection,
                                    selection,
                                    selectionArgs,
                                    null,
                                    null,
                                    sortOrder);
                break;
            case COACHESCORNERPLAYERSGAMEDAY:
                int gameId = Integer.parseInt(selection);
                sql = "SELECT " + CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry._ID + ", " +
                        CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERFIRSTNAME + ", " +
                        CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERLASTNAME + ", " +
                        CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERNUM + ", " +
                        CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERPIC + ", " +
                        "IFNULL(" + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.ScoreDBEntry.COLUMN_GOALCOUNT + ", 0) AS " +
                        CoachesCornerDBContract.ScoreDBEntry.COLUMN_GOALCOUNT + ", " +
                        "IFNULL(" + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.ScoreDBEntry.COLUMN_ASSITCOUNT + ", 0) AS " +
                        CoachesCornerDBContract.ScoreDBEntry.COLUMN_ASSITCOUNT + ", " +
                        "IFNULL(" + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.ScoreDBEntry.COLUMN_SAVESCOUNT + ", 0) AS " +
                        CoachesCornerDBContract.ScoreDBEntry.COLUMN_SAVESCOUNT + ", " +
                        CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERNOTE +
                        " FROM " + CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME +
                        " LEFT JOIN " + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME +
                        " ON " + CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.PlayerDBEntry._ID +
                        " = " + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.ScoreDBEntry.COLUMN_PLAYERID +
                        " AND " + CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME + "." + CoachesCornerDBContract.ScoreDBEntry.COLUMN_GAMEID + " = " + gameId + ";";
                cursor = db.rawQuery(sql, null);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI + + uri");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);


        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMather.match(uri);

        switch (match) {
            case COACHESCORNERGAME:
                return CoachesCornerDBContract.GameDBEntry.CONTENT_LIST_TYPE;
            case COACHESCORNERGAME_ID:
                return CoachesCornerDBContract.GameDBEntry.CONTENT_ITEM_TYPE;
            case COACHESCORNERPLAYER:
                return CoachesCornerDBContract.PlayerDBEntry.CONTENT_LIST_TYPE;
            case COACHESCORNERPLAYER_ID:
                return CoachesCornerDBContract.PlayerDBEntry.CONTENT_ITEM_TYPE;
            case COACHESCORNERSCORE:
                return CoachesCornerDBContract.ScoreDBEntry.CONTENT_LIST_TYPE;
            case COACHESCORNERSCORE_ID:
                return CoachesCornerDBContract.ScoreDBEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = mCoachesCornerDBHelper.getWritableDatabase();

        int match = sUriMather.match(uri);
        long id = 0;

        Uri returnUri = null;

        switch (match) {
            case COACHESCORNERGAME:
                id = db.insert(CoachesCornerDBContract.GameDBEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(CoachesCornerDBContract.GameDBEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into Game Table -  " + uri);
                }
                break;
            case COACHESCORNERPLAYER:
                id = db.insert(CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(CoachesCornerDBContract.PlayerDBEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into Player Table " + uri);
                }
                break;
            case COACHESCORNERSCORE:
                id = db.insertWithOnConflict(CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(CoachesCornerDBContract.ScoreDBEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into Score Table " + uri);
                }
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mCoachesCornerDBHelper.getWritableDatabase();

        int rowsDeleted;

        int match = sUriMather.match(uri);

        switch (match) {
            case COACHESCORNERGAME:
                rowsDeleted = db.delete(CoachesCornerDBContract.GameDBEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COACHESCORNERGAME_ID:
                selection = CoachesCornerDBContract.GameDBEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(CoachesCornerDBContract.GameDBEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COACHESCORNERPLAYER:
                rowsDeleted = db.delete(CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COACHESCORNERPLAYER_ID:
                selection = CoachesCornerDBContract.PlayerDBEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COACHESCORNERSCORE:
                rowsDeleted = db.delete(CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COACHESCORNERSCORE_ID:
                selection = CoachesCornerDBContract.ScoreDBEntry.COLUMN_GAMEID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COACHESCORNERSCORE_PLAYERID:
                selection = CoachesCornerDBContract.ScoreDBEntry.COLUMN_PLAYERID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
        if (rowsDeleted !=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mCoachesCornerDBHelper.getWritableDatabase();

        int count;

        int match = sUriMather.match(uri);

        switch (match) {
            case COACHESCORNERGAME_ID:
                selection = CoachesCornerDBContract.GameDBEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                count = db.update(CoachesCornerDBContract.GameDBEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case COACHESCORNERPLAYER_ID:
                selection = CoachesCornerDBContract.PlayerDBEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                count = db.update(CoachesCornerDBContract.PlayerDBEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case COACHESCORNERSCORE_GAMEANDPLAYERID:
                // content://com.example.android.coachescorner/score?game_Id=#&player_Id=#
                selection = CoachesCornerDBContract.ScoreDBEntry.COLUMN_GAMEID + "=?&"+ CoachesCornerDBContract.ScoreDBEntry.COLUMN_PLAYERID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                count = db.update(CoachesCornerDBContract.ScoreDBEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }
}
