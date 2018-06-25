package com.example.android.coachescorner.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.adapter.ScoreCardAdapter;
import com.example.android.coachescorner.common.Utils;
import com.example.android.coachescorner.data.CoachesCornerDBContract;
import com.example.android.coachescorner.data.Game;
import com.example.android.coachescorner.data.Player;
import com.example.android.coachescorner.ui.dialogs.UpdatePlayerScoreDialogFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameDayActivity extends AppCompatActivity
        implements UpdatePlayerScoreDialogFragment.UpdatePlayerScoreDialogListener,
            ScoreCardAdapter.ScoreCardAdapterOnClickHandler {

    private Game mGame;
    private String mTeamName;
    private String mHeadCoachName;
    private String mFirstAssistantCoachName;
    private String mSecondAssistantCoachName;
    private long mGameTime;
    private long mSubTime;
    private long mRunningSubTime;
    private long mRunningGameTimer;
    private CountDownTimer mGameCountDownTimer;
    private CountDownTimer mSubCountDownTimer;
    private Boolean isTimerRunning = false;
    private Boolean isSubTimerRunning = false;

    private Context mContext;

    @BindView(R.id.tv_game_day_home_team_name) TextView mHomeTeamName;
    @BindView(R.id.tv_game_day_home_team_score) TextView mHomeTeamScore;
    @BindView(R.id.tv_game_day_away_team_name) TextView mAwayTeamName;
    @BindView(R.id.tv_game_day_away_team_score) TextView mAwayTeamScore;
    @BindView(R.id.ib_increase_opponent_score) ImageButton mIncreaseOpponentScore;
    @BindView(R.id.ib_decrease_opponent_score) ImageButton mDecreaseOpponentScore;
    @BindView(R.id.tv_timer) TextView mTimerTextView;
    @BindView(R.id.btn_start_timer) Button mBtnStartTimer;
    @BindView(R.id.tv_sub_timer) TextView mSubTimerTextView;
    @BindView(R.id.recyclerview_scorecard) RecyclerView mScoreCardRecyclerView;
    @BindView(R.id.game_day_adView) AdView mAdView;


    private static final int ID_PLAYER_LOADER = 3;

    private static final int EDITPLAYERREQUESTCODE = 6;
    private static final String PARCELABLE_PLAYER_KEY = "Players";
    private static final String TIMER_IS_RUNNING_KEY = "TimerIsRunning";
    private static final String SUBTIMER_IS_RUNNING_KEY = "SubTimerIsRunning";
    private static final String REMAIN_TIME = "Timer";
    private static final String REMAIN_SUBTIME = "SUBTimer";

    private ScoreCardAdapter mScoreCardAdapter;
    public ArrayList<Player> mPlayers;


    private static final String EMAILFILEATTACHMENT = "ScoreCard.html";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_day);
        ButterKnife.bind(this);

        mContext = getApplicationContext();


        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra("Game")) {
            mGame = intentThatStartedThisActivity.getParcelableExtra("Game");
        }

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Utils.CC_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(Utils.PREF_TEAM_NAME)) {
            mTeamName = sharedPreferences.getString(Utils.PREF_TEAM_NAME, Utils.DEFAULT_TEAM_NAME);
            mHeadCoachName = sharedPreferences.getString(Utils.PREF_COACH_NAME, "");
            mFirstAssistantCoachName = sharedPreferences.getString(Utils.PREF_ASSIST1_NAME, "");
            mSecondAssistantCoachName = sharedPreferences.getString(Utils.PREF_ASSIST2_NAME, "");
            mGameTime = sharedPreferences.getInt(Utils.PREF_GAME_TIME, 30);
            mSubTime = sharedPreferences.getInt(Utils.PREF_SUB_TIME, 6);
        }

        mTimerTextView.setText(String.valueOf(mGameTime));
        mSubTimerTextView.setText(String.valueOf(mSubTime));

        setGameDetails();

        mIncreaseOpponentScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGame.addOpponentScore();
                updateOpponentScore();
            }
        });

        mDecreaseOpponentScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGame.subtractOpponentScore();
                updateOpponentScore();
            }
        });


        mBtnStartTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTimerRunning = true;
                isSubTimerRunning = true;
                mBtnStartTimer.setEnabled(false);
                startTimer(TimeUnit.MINUTES.toMillis(mGameTime));
                startSubTimer(TimeUnit.MINUTES.toMillis(mSubTime));
            }
        });


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mScoreCardRecyclerView.setLayoutManager(layoutManager);
        mScoreCardRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mScoreCardRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        mScoreCardAdapter = new ScoreCardAdapter(mContext, this);
        mScoreCardRecyclerView.setAdapter(mScoreCardAdapter);


        // Using Sample APP ID - Replace with your own
        MobileAds.initialize(mContext, "ca-app-pub-3940256099942544~3347511713");

        //Used for testing on a emulator
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        //Used for my Real Test Device
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("A436B21D2FB9B512F8E02EC492650749").build();
        mAdView.loadAd(adRequest);

        if (savedInstanceState == null || !savedInstanceState.containsKey(PARCELABLE_PLAYER_KEY)) {
            mPlayers = new ArrayList<>();
            loadPlayers();
        } else {
            isTimerRunning = savedInstanceState.getBoolean(TIMER_IS_RUNNING_KEY);
            isSubTimerRunning = savedInstanceState.getBoolean(SUBTIMER_IS_RUNNING_KEY);
            mRunningGameTimer = savedInstanceState.getLong(REMAIN_TIME);
            mRunningSubTime = savedInstanceState.getLong(REMAIN_SUBTIME);
            if (isTimerRunning) {
                startTimer(mRunningGameTimer);
                mBtnStartTimer.setEnabled(false);
            }
            if (isSubTimerRunning) {
                startSubTimer(mRunningSubTime);
            }
            mPlayers = savedInstanceState.getParcelableArrayList(PARCELABLE_PLAYER_KEY);
            mScoreCardAdapter.setPlayers(mPlayers);
        }


    }

    private void setGameDetails() {
        if (mGame.getHomeOrAway().equals("Home")) {
            mHomeTeamName.setText(mTeamName);
            mHomeTeamScore.setText(String.valueOf(mGame.getTeamScore()));
            mAwayTeamName.setText(mGame.getOpponentName());
            mAwayTeamScore.setText(String.valueOf(mGame.getOpponentScore()));
        } else {
            mHomeTeamName.setText(mGame.getOpponentName());
            mHomeTeamScore.setText(String.valueOf(mGame.getOpponentScore()));
            mAwayTeamName.setText(mTeamName);
            mAwayTeamScore.setText(String.valueOf(mGame.getTeamScore()));
        }

    }

    private void loadPlayers() {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> scoreDBLoader = loaderManager.getLoader(ID_PLAYER_LOADER);
        if (scoreDBLoader == null) {
            getSupportLoaderManager().initLoader(ID_PLAYER_LOADER, null, loaderCallBackCursorLoader);
        } else {
            getSupportLoaderManager().restartLoader(ID_PLAYER_LOADER, null, loaderCallBackCursorLoader);
        }
    }



    private void updateOpponentScore() {
        if (mGame.getHomeOrAway().equals("Home")) {
            mAwayTeamScore.setText(String.valueOf(mGame.getOpponentScore()));
        } else {
            mHomeTeamScore.setText(String.valueOf(mGame.getOpponentScore()));
        }
        updateGameDetails();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == EDITPLAYERREQUESTCODE) {
            mGame = data.getParcelableExtra("Game");
            setGameDetails();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //This inflates the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gameday_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.gd_menu_reset_timer:
                if (isTimerRunning) {
                    if (mGameCountDownTimer != null) {
                        mGameCountDownTimer.cancel();
                    }
                    if (mSubCountDownTimer != null) {
                        mSubCountDownTimer.cancel();
                    }
                    mTimerTextView.setText(String.valueOf(mGameTime));
                    mSubTimerTextView.setText(String.valueOf(mSubTime));
                    mBtnStartTimer.setEnabled(true);
                    isTimerRunning = false;
                    isSubTimerRunning = false;
                }
                break;
            case R.id.gd_menu_share_score_card:
                boolean success = createHtmlFile();
                if (success) {
                    final Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Game Score Sheet - " + mTeamName + " against " + mGame.getOpponentName());
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "See Email Attachment");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(this.getExternalFilesDir(null), EMAILFILEATTACHMENT)));
                    if (emailIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(Intent.createChooser(emailIntent, "Send email...."));
                    }
                }
                break;
            case R.id.gd_menu_field_location:
                Intent intentFieldLocation = new Intent(mContext, FieldLocationActivity.class);
                intentFieldLocation.putExtra("Location", mGame.getFieldLocation());
                startActivity(intentFieldLocation);
                break;
            case R.id.gd_menu_edit_game_details:
                Intent intentEditGame = new Intent(mContext, EditGameActivity.class);
                intentEditGame.putExtra("Game", mGame);
                startActivityForResult(intentEditGame, EDITPLAYERREQUESTCODE);
                break;

            case android.R.id.home:
                if (mGameCountDownTimer != null) {
                    mGameCountDownTimer.cancel();
                }
                if (mSubCountDownTimer != null) {
                    mSubCountDownTimer.cancel();
                }
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void startTimer(long gameTimer) {

        mGameCountDownTimer = new CountDownTimer(gameTimer, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                mRunningGameTimer = millisUntilFinished;
                String timer = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                mTimerTextView.setText(timer);
            }

            @Override
            public void onFinish() {
                mBtnStartTimer.setEnabled(true);
                mTimerTextView.setText(String.valueOf(mGameTime));
                Toast.makeText(mContext, "Timer is up", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void startSubTimer(long subTime) {
        mSubCountDownTimer = new CountDownTimer(subTime, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                mRunningSubTime = millisUntilFinished;
                String subTimer = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                mSubTimerTextView.setText(subTimer);
            }

            @Override
            public void onFinish() {
                isSubTimerRunning = false;
                Toast.makeText(mContext, "Sub Timer is up", Toast.LENGTH_SHORT).show();
                if (mRunningGameTimer > mSubTime) {
                    mSubCountDownTimer.cancel();
                    mSubCountDownTimer.start();
                } else {
                    mSubCountDownTimer.cancel();
                    mSubTimerTextView.setText("0:00");
                }
                playNotification();
            }
        }.start();
    }

    private void playNotification() {

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

     private boolean createHtmlFile() {

        try {
            File scoreCard = new File(this.getExternalFilesDir(null), EMAILFILEATTACHMENT);
            if (scoreCard.exists()) {
                scoreCard.delete();
            }
            scoreCard.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(scoreCard, true));
            writer.write(createHTMLTags());
            writer.close();
            return  true;
        } catch (IOException e) {
            Log.e(GameDayActivity.class.getSimpleName(), "Unable to write to the " + EMAILFILEATTACHMENT + " file");
            return false;
        }


    }


    private String createHTMLTags() {

        String htmlFile;

        htmlFile = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table {\n" +
                "    font-family: arial, sans-serif;\n" +
                "    border-collapse: collapse;\n" +
                "}\n" +
                "\n" +
                "td, th {\n" +
                "    border: 2px solid #000000;\n" +
                "    text-align: left;\n" +
                "    padding: 8px;\n" +
                "}\n" +
                "\n" +
                "</style>\n" +
                "\n" +
                "\n" +
                "</head>\n" +
                "<body>";

        htmlFile += "<table> " +
                "<tr> " +
                "<th>"+ mContext.getResources().getString(R.string.coaches_label) +"</td> " +
                "<th></th> " +
                "<th colspan=\"3\">"+ mGame.getGameDate() + " " + mGame.getGameTime() + "</th> " +
                "</tr> " +
                "<tr> " +
                "<td>"+mHeadCoachName+"</td>" +
                "<th>"+ mContext.getResources().getString(R.string.team_label_html)+"</th>" +
                "<th>"+ mContext.getResources().getString(R.string.score_label_html)+"</th>" +
                "<th>"+ mContext.getResources().getString(R.string.team_label_html)+"</th>" +
                "<th>"+ mContext.getResources().getString(R.string.score_label_html)+"</th>" +
                "</tr> " +
                "<tr>" +
                "<td>"+mFirstAssistantCoachName+"</td> ";
        if (mGame.getHomeOrAway().equals("Home")) {
            htmlFile += "<td rowspan=\"2\">"+mGame.getOpponentName()+"</td>" +
                    "<td rowspan=\"2\">"+mGame.getOpponentScore()+"</td>" +
                    "<td rowspan=\"2\">"+mTeamName+"</td>" +
                    "<td rowspan=\"2\">"+mHomeTeamScore.getText().toString()+"</td>";
        } else {
            htmlFile += "<td rowspan=\"2\">"+mTeamName+"</td>" +
                    "<td rowspan=\"2\">"+mHomeTeamScore.getText().toString()+"</td>" +
                    "<td rowspan=\"2\">"+mGame.getOpponentName()+"</td>" +
                    "<td rowspan=\"2\">"+mGame.getOpponentScore()+"</td>";
        }
        htmlFile += "</tr>" +
                "<tr>" +
                "<td>"+mSecondAssistantCoachName+"</td>"+
                "</tr>" +
                "<tr> " +
                "<td colspan=\"5\" style=\"background-color:black;\">></td>"+
                "</tr> "+
                "<tr>"+
                "<th>"+mContext.getResources().getString(R.string.player_name_label)+"</th>"+
                "<th>"+mContext.getResources().getString(R.string.number_label)+"</th>" +
                "<th>"+mContext.getResources().getString(R.string.goals_label)+"</th>" +
                "<th>"+mContext.getResources().getString(R.string.assists_label)+"</th>" +
                "<th>"+mContext.getResources().getString(R.string.saves_label)+"</th>"+
                "</tr>";
        for (int cntr=0;cntr<mPlayers.size();cntr++) {
            htmlFile += "<tr>" +
                    "<td>"+mPlayers.get(cntr).getPlayerFirstName() + " " + mPlayers.get(cntr).getPlayerLastName()+"</td>"+
                    "<td>"+mPlayers.get(cntr).getPlayerNum()+"</td>" +
                    "<td>"+mPlayers.get(cntr).getGoalCount()+"</td>" +
                    "<td>"+mPlayers.get(cntr).getAssitCount()+"</td>"+
                    "<td>"+mPlayers.get(cntr).getSaveCount()+"</td>"+
                    "</tr>";
        }

        htmlFile += "</table></body></html>";

        Log.d(GameDayActivity.class.getSimpleName(), htmlFile);

        return htmlFile;

    }


    public boolean updateGameDetails() {

        ContentValues values = new ContentValues();
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_GAMEDATE, mGame.getGameDate());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_GAMETIME, mGame.getGameTime());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_OPPONENTNAME, mGame.getOpponentName());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_OPPONENTSCORE, mGame.getOpponentScore());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_HOMEORAWAY, mGame.getHomeOrAway());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_GAMENOTE, mGame.getGameNote());
        values.put(CoachesCornerDBContract.GameDBEntry.COLUMN_FIELDLOCATION, mGame.getFieldLocation());
        String selection = Integer.toString(mGame.getGameId());
        Uri updateUri = CoachesCornerDBContract.GameDBEntry.buildGameUriWithId(mGame.getGameId());

        int count = getContentResolver().update(updateUri, values, selection, null);
        if (count == 0) {
            Toast.makeText(this, "Failed to update Game Table", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PARCELABLE_PLAYER_KEY, mPlayers);
        outState.putBoolean(TIMER_IS_RUNNING_KEY, isTimerRunning);
        outState.putBoolean(SUBTIMER_IS_RUNNING_KEY, isSubTimerRunning);
        outState.putLong(REMAIN_TIME, mRunningGameTimer);
        outState.putLong(REMAIN_SUBTIME, mRunningSubTime);
        super.onSaveInstanceState(outState);
    }


    private LoaderManager.LoaderCallbacks<Cursor> loaderCallBackCursorLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

            Uri gameDayUri = CoachesCornerDBContract.ScoreDBEntry.GAME_DAY_CONTENT_URI;

            return new CursorLoader(mContext,
                    gameDayUri,
                    null,
                    String.valueOf(mGame.getGameId()),
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

            if (cursor == null || cursor.getCount() < 1) {
                return;
            }

            mPlayers.clear();

            while (cursor.moveToNext()) {
                int playerId = cursor.getInt(cursor.getColumnIndex(CoachesCornerDBContract.PlayerDBEntry._ID));
                String playerFirstName = cursor.getString(cursor.getColumnIndex(CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERFIRSTNAME));
                String playerLastName = cursor.getString(cursor.getColumnIndex(CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERLASTNAME));
                String playerNum = cursor.getString(cursor.getColumnIndex(CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERNUM));
                byte[] playerPic = cursor.getBlob(cursor.getColumnIndex(CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERPIC));
                int goalScored = cursor.getInt(cursor.getColumnIndex(CoachesCornerDBContract.ScoreDBEntry.COLUMN_GOALCOUNT));
                int assistMade = cursor.getInt(cursor.getColumnIndex(CoachesCornerDBContract.ScoreDBEntry.COLUMN_ASSITCOUNT));
                int savesMade = cursor.getInt(cursor.getColumnIndex(CoachesCornerDBContract.ScoreDBEntry.COLUMN_SAVESCOUNT));
                String note = cursor.getString(cursor.getColumnIndex(CoachesCornerDBContract.PlayerDBEntry.COLUMN_PLAYERNOTE));

                mPlayers.add(new Player(playerId, playerFirstName, playerLastName, playerNum, playerPic, goalScored, assistMade, savesMade, note));
            }

            if (!mPlayers.isEmpty()) {
                mScoreCardAdapter.setPlayers(mPlayers);
            }

        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }
    };

    @Override
    public void onClick(Player player) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        UpdatePlayerScoreDialogFragment updatePlayerScoreDialog = new UpdatePlayerScoreDialogFragment();
        updatePlayerScoreDialog.setPlayer(player);
        updatePlayerScoreDialog.setDialogTitle("Update Player Score");
        updatePlayerScoreDialog.show(fragmentManager, "Update Score");

    }

    @Override
    public void onFinishedUpdatePlayerScore(int playerId, int currentGoalCount, int goals, int assists, int saves) {

        int score;

        if (playerId != 0 && mGame != null) {
            if (mGame.getHomeOrAway().equals("Home")) {
                score = Integer.parseInt(mHomeTeamScore.getText().toString());
                score = newScoreCalculator(score, goals, currentGoalCount);
                mHomeTeamScore.setText(String.valueOf(score));
            } else {
                score = Integer.parseInt(mAwayTeamScore.getText().toString());
                score = newScoreCalculator(score, goals, currentGoalCount);
                mAwayTeamScore.setText(String.valueOf(score));
            }
            boolean loadedSuccessfully = Utils.insertPlayerScore(this, mGame.getGameId(), playerId, goals, assists, saves);
            if (loadedSuccessfully) {
                loadPlayers();
                Log.d(GameDayActivity.class.getSimpleName(), "Loaded Player Score into Score Table");
            } else {
                Log.d(GameDayActivity.class.getSimpleName(), "Problem loading Player Score into Score Table");
            }
        }

    }

    private int newScoreCalculator(int currentTeamScore, int playerGoalsAtSave, int playerGoalsAtStart) {

        if (playerGoalsAtSave >= playerGoalsAtStart) {
            currentTeamScore = currentTeamScore + (playerGoalsAtSave - playerGoalsAtStart);
        } else if (playerGoalsAtSave < playerGoalsAtStart) {
            currentTeamScore = currentTeamScore - (playerGoalsAtStart - playerGoalsAtSave);
        }

        return currentTeamScore;

    }

}
