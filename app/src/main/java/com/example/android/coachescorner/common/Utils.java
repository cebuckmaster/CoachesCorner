package com.example.android.coachescorner.common;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.data.CoachesCornerDBContract;
import com.example.android.coachescorner.data.Game;
import com.example.android.coachescorner.data.Player;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by cebuc on 4/28/2018.
 */

public class Utils {

    public static final String CC_PREFERENCES = "coachesCornerPreferences";
    public static final String PREF_TEAM_NAME = "teamName";
    public static final String PREF_COACH_NAME = "coachName";
    public static final String PREF_ASSIST1_NAME = "assist1Name";
    public static final String PREF_ASSIST2_NAME = "assist2Name";
    public static final String PREF_AGE_GROUP = "ageGroup";
    public static final String PREF_GAME_FORMAT = "gameFormat";
    public static final String PREF_GAME_TIME = "gameTime";
    public static final String PREF_GAME_TYPE = "gameType";
    public static final String PREF_SUB_TIME = "subTime";
    public static final String DEFAULT_TEAM_NAME = "Team Name";


    public static boolean addPlayerToDatabase(Context context, Player player) {

        ContentValues values = new ContentValues();
        values.put(CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERFIRSTNAME, player.getPlayerFirstName());
        values.put(CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERLASTNAME, player.getPlayerLastName());
        values.put(CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERNUM, player.getPlayerNum());
        values.put(CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERPIC, player.getPlayerPic());

        Uri newUri = context.getContentResolver().insert(CoachesCornerDBContract.PlayerDBEntry.CONTENT_URI, values);

        if (newUri == null) {
            return false;
        }
        return true;

    }

    public static boolean insertPlayerScore(Context context, int gameId, int playerId, int goals, int assists, int saves) {
        ContentValues values = new ContentValues();
        values.put(CoachesCornerDBContract.ScoreDBEntry.COLUMN_GAMEID, gameId);
        values.put(CoachesCornerDBContract.ScoreDBEntry.COLUMN_PLAYERID, playerId);
        values.put(CoachesCornerDBContract.ScoreDBEntry.COLUMN_GOALCOUNT, goals);
        values.put(CoachesCornerDBContract.ScoreDBEntry.COLUMN_ASSITCOUNT, assists);
        values.put(CoachesCornerDBContract.ScoreDBEntry.COLUMN_SAVESCOUNT, saves);

        Uri scoreUri = context.getContentResolver().insert(CoachesCornerDBContract.ScoreDBEntry.CONTENT_URI, values);

        if (scoreUri == null) {
            return false;
        }
        return true;
    }

    public static boolean addGameToDatabase(Context context, Game game) {

        ContentValues values = new ContentValues();
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_GAMEDATE, game.getGameDate());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_GAMETIME, game.getGameTime());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_OPPONENTNAME, game.getOpponentName());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_OPPONENTSCORE, game.getOpponentScore());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_HOMEORAWAY, game.getHomeOrAway());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_GAMENOTE, game.getGameNote());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_FIELDLOCATION, game.getFieldLocation());

        Uri newUri = context.getContentResolver().insert(CoachesCornerDBContract.GameDBEntry.CONTENT_URI, values);

        if (newUri == null) {
            return false;
        }
        return true;
    }

    public static boolean deletePlayerFromDatabasebyID(Context context, int playerID) {
        String selection = Integer.toString(playerID);
        Uri playerUri = CoachesCornerDBContract.PlayerDBEntry.buildPlayerUriWithId(playerID);
        int rowsDeleted = context.getContentResolver().delete(playerUri, selection, null);
        if (rowsDeleted == 0) {
            return false;
        }
        return true;

    }

    public static boolean deleteGameFromDatabasebyID(Context context, int gameID) {
        String selection = Integer.toString(gameID);
        Uri gameUri = CoachesCornerDBContract.GameDBEntry.buildGameUriWithId(gameID);
        int rowsDeleted = context.getContentResolver().delete(gameUri, selection, null);
        if (rowsDeleted == 0) {
            return false;
        }
        return true;

    }

    public static boolean deleteScoreFromDatabasebyGameID(Context context, int gameID) {
        String selection = Integer.toString(gameID);
        Uri scoreUri = CoachesCornerDBContract.ScoreDBEntry.buildScoreUriWithId(gameID);
        int rowsDeleted = context.getContentResolver().delete(scoreUri, selection, null);
        if (rowsDeleted == 0) {
            return false;
        }
        return true;

    }

    public static boolean deleteScoreFromDatabasebyPlayerID(Context context, int playerID) {
        String selection = Integer.toString(playerID);
        Uri scoreUri = CoachesCornerDBContract.ScoreDBEntry.buildScoreUriWithPlayerId(playerID);
        int rowsDeleted = context.getContentResolver().delete(scoreUri, selection, null);
        if (rowsDeleted == 0) {
            return false;
        }
        return true;

    }

    public static boolean deleteAllFromGameTable(Context context) {
        Uri gameUri = CoachesCornerDBContract.GameDBEntry.CONTENT_URI;
        int rowsDeleted = context.getContentResolver().delete(gameUri, null, null);
        if (rowsDeleted == 0) {
            return false;
        }
        return true;
    }

    public static boolean deleteAllFromPlayerTable(Context context) {
        Uri playerUri = CoachesCornerDBContract.PlayerDBEntry.CONTENT_URI;
        int rowsDeleted = context.getContentResolver().delete(playerUri, null, null);
        if (rowsDeleted == 0) {
            return false;
        }
        return true;
    }

    public static boolean deleteAllFromScoreTable(Context context) {
        Uri scoreUri = CoachesCornerDBContract.ScoreDBEntry.CONTENT_URI;
        int rowsDeleted = context.getContentResolver().delete(scoreUri, null, null);
        if (rowsDeleted == 0) {
            return false;
        }
        return true;
    }

    public static boolean updatePlayerToDatabase(Context context, Player player) {

        ContentValues values = new ContentValues();
        String selection = Integer.toString(player.getPlayerId());
        values.put(CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERFIRSTNAME, player.getPlayerFirstName());
        values.put(CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERLASTNAME, player.getPlayerLastName());
        values.put(CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERNUM, player.getPlayerNum());
        values.put(CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERPIC, player.getPlayerPic());
        values.put(CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERNOTE, player.getPlayerNote());
        Uri playerUri = CoachesCornerDBContract.PlayerDBEntry.buildPlayerUriWithId(player.getPlayerId());
        int count = context.getContentResolver().update(playerUri, values, selection, null);
        if (count == 0) {
            return false;
        }
        return true;
    }


    public static boolean updateGameToDatabase(Context context, Game game) {

        ContentValues values = new ContentValues();
        String selection = Integer.toString(game.getGameId());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_GAMEDATE, game.getGameDate());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_GAMETIME, game.getGameTime());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_OPPONENTNAME, game.getOpponentName());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_OPPONENTSCORE, game.getOpponentScore());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_HOMEORAWAY, game.getHomeOrAway());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_GAMENOTE, game.getGameNote());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_FIELDLOCATION, game.getFieldLocation());
        Uri gameUri = CoachesCornerDBContract.GameDBEntry.buildGameUriWithId(game.getGameId());
        int count = context.getContentResolver().update(gameUri, values, selection, null);
        if (count == 0) {
            return false;
        }
        return true;
    }

    public static int getStringArrayIndex(String[] stringArray, String value) {

        int position = 0;

        for (int cntr = 0; cntr < stringArray.length; cntr++) {
            if (stringArray[cntr] == value) {
                position = cntr;
                break;
            }
        }
        return position;

    }

    public static String getTeamName(Context context) {

        String teamName = DEFAULT_TEAM_NAME;

        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.CC_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(Utils.PREF_TEAM_NAME)) {
            teamName = sharedPreferences.getString(Utils.PREF_TEAM_NAME, Utils.DEFAULT_TEAM_NAME);
        }

        return teamName;

    }

    public static void clearTeamSettings(Context context) {

        String[] ageGroupArray = context.getResources().getStringArray(R.array.age_group);
        String[] gameFormatArray = context.getResources().getStringArray(R.array.gameformat);
        String[] halfQuarterArray = context.getResources().getStringArray(R.array.half_quarter);


        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.CC_PREFERENCES, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Utils.PREF_TEAM_NAME, DEFAULT_TEAM_NAME);
        editor.putString(Utils.PREF_COACH_NAME, "");
        editor.putString(Utils.PREF_ASSIST1_NAME, "");
        editor.putString(Utils.PREF_ASSIST2_NAME, "");
        editor.putString(Utils.PREF_AGE_GROUP, ageGroupArray[3]);
        editor.putString(Utils.PREF_GAME_FORMAT, gameFormatArray[0]);
        editor.putInt(Utils.PREF_GAME_TIME, context.getResources().getInteger(R.integer.default_game_time));
        editor.putString(Utils.PREF_GAME_TYPE, halfQuarterArray[0]);
        editor.putInt(Utils.PREF_SUB_TIME, context.getResources().getInteger(R.integer.default_sub_time));
        editor.commit();



    }

    public static Intent getPickImageChooserIntent(Context context) {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri(context);

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();


        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }


    /**
     * Get URI to image received from capture by camera.
     */
    public static Uri getCaptureImageOutputUri(Context context) {
        Uri outputFileUri = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    public static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    public static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    public static Uri getPickImageResultUri(Context context, Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
//        return isCamera ? getCaptureImageOutputUri() : data.getData();
        return isCamera ? getCaptureImageOutputUri(context) : getImageUriFromGallery(context, data.getData());
    }

    public static Uri getImageUriFromGallery(Context context, Uri uri) {

        Uri selectedImage = uri;
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return Uri.fromFile(new File(picturePath));
    }


    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
