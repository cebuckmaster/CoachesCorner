package com.example.android.coachescorner.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
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
import android.widget.ProgressBar;
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

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class GameDayActivity extends AppCompatActivity
        implements UpdatePlayerScoreDialogFragment.UpdatePlayerScoreDialogListener,
            ScoreCardAdapter.ScoreCardAdapterOnClickHandler,
            EasyPermissions.PermissionCallbacks {

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
    @BindView(R.id.pb_rules_loading) ProgressBar mLoadingProgressBar;
    @BindView(R.id.game_day_adView) AdView mAdView;


    private static final int ID_PLAYER_LOADER = 3;

    private static final int EDITPLAYERREQUESTCODE = 6;
    private static final String PARCELABLE_PLAYER_KEY = "Players";
    private static final String TIMER_IS_RUNNING_KEY = "TimerIsRunning";
    private static final String SUBTIMER_IS_RUNNING_KEY = "SubTimerIsRunning";
    private static final String REMAIN_TIME = "Timer";
    private static final String REMAIN_SUBTIME = "SUBTimer";

    private static final int WRITE_REQUEST_CODE = 600;

    private ScoreCardAdapter mScoreCardAdapter;
    public ArrayList<Player> mPlayers;


    private static final String EMAILFILEATTACHMENT = "ScoreCard.html";
    private String mRuleFileName;
    private String mRulesURLPath;
    private String mRuleFullPathFileName;


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

        MenuItem rulesMenuItem = menu.findItem(R.id.gd_menu_get_rules_submenu);

        inflater.inflate(R.menu.rules_submenu, rulesMenuItem.getSubMenu());

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
                if (mGame.getFieldLocation() != null && !mGame.getFieldLocation().isEmpty()) {
                    Intent intentFieldLocation = new Intent(mContext, FieldLocationActivity.class);
                    intentFieldLocation.putExtra("Location", mGame.getFieldLocation());
                    startActivity(intentFieldLocation);
                } else {
                    Toast.makeText(mContext, getString(R.string.enter_field_location), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.gd_menu_edit_game_details:
                Intent intentEditGame = new Intent(mContext, EditGameActivity.class);
                intentEditGame.putExtra("Game", mGame);
                startActivityForResult(intentEditGame, EDITPLAYERREQUESTCODE);
                break;
            case R.id.gd_menu_8u_rules:
                mRuleFileName = "8UCondensedRules.pdf";
                mRulesURLPath = "https://bsbproduction.s3.amazonaws.com/portals/1870/docs/rulebook/8ucondensedrules.pdf";
                getRulesFile(mRulesURLPath);
                break;
            case R.id.gd_menu_10u_rules:
                mRuleFileName = "10UCondensedRules.pdf";
                mRulesURLPath = "https://bsbproduction.s3.amazonaws.com/portals/1870/docs/rulebook/10ucondensedrules.pdf";
                getRulesFile(mRulesURLPath);
                break;
            case R.id.gd_menu_12u_rules:
                mRuleFileName = "12UCondensedRules.pdf";
                mRulesURLPath = "https://bsbproduction.s3.amazonaws.com/portals/1870/docs/rulebook/12ucondensedrules.pdf";
                getRulesFile(mRulesURLPath);
                break;
            case R.id.gd_menu_14u_rules:
                mRuleFileName = "14UCondensedRules.pdf";
                mRulesURLPath = "https://bsbproduction.s3.amazonaws.com/portals/1870/docs/rulebook/14ucondensedrules.pdf";
                getRulesFile(mRulesURLPath);
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
                Toast.makeText(mContext, getString(R.string.timer_up), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, getString(R.string.sub_timer_up), Toast.LENGTH_SHORT).show();
                if (mRunningGameTimer > mSubTime) {
                    mSubCountDownTimer.cancel();
                    mSubCountDownTimer.start();
                } else {
                    mSubCountDownTimer.cancel();
                    mSubTimerTextView.setText(getString(R.string.zero_game_timer));
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
            Toast.makeText(this, getString(R.string.failed_to_update_game), Toast.LENGTH_SHORT).show();
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


    private boolean getRulesFile(String url) {

        if (checkForSDCard()) {

            if (EasyPermissions.hasPermissions(GameDayActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new DownloadFileFromURL().execute(url);

            } else {
                EasyPermissions.requestPermissions(GameDayActivity.this, getString(R.string.write_file), WRITE_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        } else {
            Toast.makeText(mContext, getString(R.string.sd_card_not_found), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, GameDayActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Download the file once permission is granted
        new DownloadFileFromURL().execute(mRulesURLPath);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(GameDayActivity.class.getSimpleName(), "Permission has been denied");
    }


    public boolean checkForSDCard() {
        //Method to Check If SD Card is mounted or not
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    private void openRuleFile() {

        try {
            File rulesFile = new File(mRuleFullPathFileName);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            Uri apkURI = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", rulesFile);
            intent.setDataAndType(apkURI, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error Opening Rules File" + e.toString());
            Toast.makeText(mContext, getString(R.string.file_warning), Toast.LENGTH_SHORT).show();
        }

    }

    public class DownloadFileFromURL extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            mLoadingProgressBar.setProgress(0);
            Toast.makeText(mContext, getString(R.string.starting_download), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            int count;
            try {

                URL url = new URL(params[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();

                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);

                mRuleFullPathFileName = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name) + "/";

                File directory = new File(mRuleFullPathFileName);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                mRuleFullPathFileName = mRuleFullPathFileName + mRuleFileName;

                OutputStream output = new FileOutputStream(mRuleFullPathFileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                    publishProgress((int) (total * 100 / lengthOfFile));
                }

                output.flush();
                output.close();
                inputStream.close();
                return getString(R.string.downloaded_at) + mRuleFullPathFileName;
            } catch (Exception e) {
                Log.e(GameDayActivity.class.getSimpleName(), "Error Downloading Rules" + e.getMessage());
            }

            return getString(R.string.download_error);
        }

        @Override
        protected void onPostExecute(String message) {

            mLoadingProgressBar.setVisibility(View.INVISIBLE);

            // Display File path after downloading
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            if (!message.equals(getString(R.string.download_error))) {
                openRuleFile();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
            mLoadingProgressBar.setProgress(values[0]);
        }
    }

}
