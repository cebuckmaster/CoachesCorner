package com.example.android.coachescorner.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.adapter.GameAdapter;
import com.example.android.coachescorner.common.Utils;
import com.example.android.coachescorner.data.CoachesCornerDBContract;
import com.example.android.coachescorner.data.Game;
import com.example.android.coachescorner.helpers.GameRecyclerItemTouchHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


public class CoachesCornerActivity extends AppCompatActivity
            implements GameAdapter.GameAdapterOnClickHandler,
                GameRecyclerItemTouchHelper.GameItemTouchHelperListener {

    private static final int ID_GAME_LOADER = 3;

    private static final String PARCELABLEKEY = "Games";

    private GameAdapter mGameAdapter;
    private ArrayList<Game> mGames;


    @BindView(R.id.recyclerview_schedule) RecyclerView mGameRecyclerView;
    @BindView(R.id.tv_no_games_added) TextView mNoGameMsgTextView;
    @BindView(R.id.fab_add_game) FloatingActionButton mAddGameFAB;
    @BindView(R.id.schedule_view) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.cc_adView) AdView mAdView;
    @BindView(R.id.collapsing_toolBar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.app_bar_layout) AppBarLayout mAppBarLayout;
    @BindView(R.id.toolBar) Toolbar mToolBar;

    private String mTeamName;
    private Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setTheme(R.style.AppTheme_CustomToolBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coaches_corner);
        ButterKnife.bind(this);

        mContext = this;

        mTeamName = Utils.getTeamName(this);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if ((scrollRange + verticalOffset) == 0) {
                    mCollapsingToolbarLayout.setTitle(mContext.getString(R.string.app_name));
                    mCollapsingToolbarLayout.setCollapsedTitleTextColor(getColor(R.color.colorText));
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }

            }
        });

        setSupportActionBar(mToolBar);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mGameRecyclerView.setLayoutManager(layoutManager);

        mGameAdapter = new GameAdapter(mContext, this);
        mGameRecyclerView.setAdapter(mGameAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new GameRecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mGameRecyclerView);

        mAddGameFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addGameIntent = new Intent(mContext, GameAddActivity.class);
                startActivity(addGameIntent);
            }
        });

        // Using Sample APP ID - Replace with your own
        MobileAds.initialize(mContext, "ca-app-pub-3940256099942544~3347511713");

        //Used for testing on a emulator
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        //Used for my Real Test Device
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice("A436B21D2FB9B512F8E02EC492650749").build();
        mAdView.loadAd(adRequest);

        if (savedInstanceState == null || !savedInstanceState.containsKey(PARCELABLEKEY)) {
            mGames = new ArrayList<>();
            loadGames();
        } else {
            mGames = savedInstanceState.getParcelableArrayList(PARCELABLEKEY);
            showGameData();
            mGameAdapter.setGames(mGames);
        }

    }

    @Override
    protected void onRestart() {
        mTeamName = Utils.getTeamName(this);
        loadGames();
        super.onRestart();
    }

    private void loadGames() {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> gameDBLoader = loaderManager.getLoader(ID_GAME_LOADER);
        if (gameDBLoader == null) {
            getSupportLoaderManager().initLoader(ID_GAME_LOADER, null, loaderCallbackCursorLoader);
        } else {
            getSupportLoaderManager().restartLoader(ID_GAME_LOADER, null, loaderCallbackCursorLoader);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PARCELABLEKEY,mGames);
        super.onSaveInstanceState(outState);
    }

    //This inflates the Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.coachescorner_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.cc_menu_settings:
                Intent intentShowSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentShowSettings, null);
                return true;
            case R.id.cc_menu_player:
                Intent intentShowRoster = new Intent(this, RosterActivity.class);
                startActivity(intentShowRoster);
                return true;
            case R.id.cc_menu_reset_all:
                new AlertDialog.Builder(mContext)
                        .setTitle("Confirm Reset All")
                        .setMessage("Please confirm you want to reset all data?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                boolean success;
                                success = Utils.deleteAllFromScoreTable(mContext);
                                if (!success) {
                                    Log.d(CoachesCornerActivity.class.getSimpleName(), "Failed to delete all from Score Table");
                                }
                                success = Utils.deleteAllFromPlayerTable(mContext);
                                if (!success) {
                                    Log.d(CoachesCornerActivity.class.getSimpleName(), "Failed to delete all from Player Table");
                                }
                                success = Utils.deleteAllFromGameTable(mContext);
                                if (!success) {
                                    Log.d(CoachesCornerActivity.class.getSimpleName(), "Failed to delete all from Game Table");
                                }
                                Utils.clearTeamSettings(mContext);
                                Toast.makeText(mContext, "Delete All Databases", Toast.LENGTH_SHORT).show();
                                mGameAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(mContext, "Nothing was reset", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbackCursorLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            Uri gameUri = CoachesCornerDBContract.GameDBEntry.CONTENT_URI;

            return new CursorLoader(mContext,
                    gameUri,
                    null,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

            if (cursor == null || cursor.getCount() < 1) {
                showAddGameMsg();
                return;
            }

            mGames.clear();

            while (cursor.moveToNext()) {
                int gameId = cursor.getInt(cursor.getColumnIndex(CoachesCornerDBContract.GameDBEntry._ID));
                String gameDate = cursor.getString(cursor.getColumnIndex(CoachesCornerDBContract.GameDBEntry.COLUMN_GAMEDATE));
                String gameTime = cursor.getString(cursor.getColumnIndex(CoachesCornerDBContract.GameDBEntry.COLUMN_GAMETIME));
                String opponentName = cursor.getString(cursor.getColumnIndex(CoachesCornerDBContract.GameDBEntry.COLUMN_OPPONENTNAME));
                int opponentScore = cursor.getInt(cursor.getColumnIndex(CoachesCornerDBContract.GameDBEntry.COLUMN_OPPONENTSCORE));
                String homeOrAway = cursor.getString(cursor.getColumnIndex(CoachesCornerDBContract.GameDBEntry.COLUMN_HOMEORAWAY));
                int teamScore = cursor.getInt(cursor.getColumnIndex(CoachesCornerDBContract.ScoreDBEntry.COLUMN_GOALCOUNT));
                String gameNote = cursor.getString(cursor.getColumnIndex(CoachesCornerDBContract.GameDBEntry.COLUMN_GAMENOTE));
                String fieldLocation = cursor.getString(cursor.getColumnIndex(CoachesCornerDBContract.GameDBEntry.COLUMN_FIELDLOCATION));

                mGames.add(new Game(gameId, gameDate, gameTime, opponentName, opponentScore, teamScore, homeOrAway, gameNote, fieldLocation));

            }

            if (!mGames.isEmpty()) {
                showGameData();
                mGameAdapter.setGames(mGames);
            } else {
                showAddGameMsg();
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };
    private void showGameData() {
        mGameRecyclerView.setVisibility(View.VISIBLE);
        mNoGameMsgTextView.setVisibility(View.INVISIBLE);
    }

    private void showAddGameMsg() {
        mGameRecyclerView.setVisibility(View.INVISIBLE);
        mNoGameMsgTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Game game) {
        Intent intentGameDay = new Intent(mContext, GameDayActivity.class);
        intentGameDay.putExtra("Game", game);
        startActivity(intentGameDay);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof GameAdapter.GameViewHolder) {

            mAdView.setVisibility(View.GONE);

            String opponentName = mGames.get(viewHolder.getAdapterPosition()).getOpponentName();

            final Game deleteGame = mGames.get(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            mGameAdapter.removeGame(viewHolder.getAdapterPosition());
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, opponentName + " vs " + mTeamName + " removed from schedule!", Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGameAdapter.restoreGame(deleteGame, deleteIndex);
                    }
                });
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            boolean success = Utils.deleteGameFromDatabasebyID(mContext, deleteGame.getGameId());
                            if (!success) {
                                Log.d(CoachesCornerActivity.class.getSimpleName(), "Error Deleting Game from Databases");
                            }
                            success = false;
                            success = Utils.deleteScoreFromDatabasebyGameID(mContext, deleteGame.getGameId());
                            if (!success) {
                                Log.d(CoachesCornerActivity.class.getSimpleName(), "Error Deleting Score based on Game ID from Databases");
                            }
                        }
                        mAdView.setVisibility(View.VISIBLE);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
        }
    }

}
