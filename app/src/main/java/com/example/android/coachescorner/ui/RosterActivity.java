package com.example.android.coachescorner.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.adapter.PlayerAdapter;
import com.example.android.coachescorner.common.Utils;
import com.example.android.coachescorner.data.CoachesCornerDBContract;
import com.example.android.coachescorner.data.Player;
import com.example.android.coachescorner.helpers.PlayerRecyclerItemTouchHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RosterActivity extends AppCompatActivity
    implements PlayerRecyclerItemTouchHelper.PlayerItemTouchHelperListener,
        PlayerAdapter.PlayerAdapterOnClickHandler {

    private static final int ID_PLAYER_LOADER = 6;


    private static final String PARCELABLEKEY = "Players";

    private PlayerAdapter mPlayerAdapter;
    public ArrayList<Player> mPlayers;
    @BindView(R.id.recyclerview_player) RecyclerView mPlayerRecyclerView;
    @BindView(R.id.tv_no_players_added) TextView mNoPlayerMsgTextView;
    @BindView(R.id.fab_add_player) FloatingActionButton mAddPlayerFAB;
    @BindView(R.id.player_view) CoordinatorLayout mCoordinatorLayout;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roster);
        ButterKnife.bind(this);

        mContext = this;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mPlayerRecyclerView.setLayoutManager(layoutManager);
        mPlayerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mPlayerRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        mPlayerAdapter = new PlayerAdapter(mContext, this);
        mPlayerRecyclerView.setAdapter(mPlayerAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new PlayerRecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mPlayerRecyclerView);

        mAddPlayerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addPlayerIntent = new Intent(mContext, PlayerAddActivity.class);
                startActivity(addPlayerIntent);
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(PARCELABLEKEY)) {
                mPlayers = savedInstanceState.getParcelableArrayList(PARCELABLEKEY);
                mPlayerAdapter.setPlayers(mPlayers);
            } else {
                loadPlayers();
            }
        } else {
            loadPlayers();
        }


    }

    private void loadPlayers() {
        mPlayers = new ArrayList<>();
        getSupportLoaderManager().initLoader(ID_PLAYER_LOADER, null, loaderCallbackCursorLoader);
    }

    private void showPlayerData() {
        mPlayerRecyclerView.setVisibility(View.VISIBLE);
        mNoPlayerMsgTextView.setVisibility(View.INVISIBLE);
    }

    private void showAddPlayerMsg() {
        mPlayerRecyclerView.setVisibility(View.INVISIBLE);
        mNoPlayerMsgTextView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(PARCELABLEKEY, mPlayers);
    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbackCursorLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

            Uri playerUri = CoachesCornerDBContract.PlayerDBEntry.CONTENT_URI;


            return new CursorLoader(mContext,
                    playerUri,
                    null,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

            if (cursor == null || cursor.getCount() < 1) {
                showAddPlayerMsg();
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
                showPlayerData();
                mPlayerAdapter.setPlayers(mPlayers);
            } else {
                showAddPlayerMsg();
            }

        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }
    };


    @Override
    public void onClick(Player player) {
        Intent intentPlayerEdit = new Intent(mContext, PlayerDetailActivity.class);
        intentPlayerEdit.putExtra("Player", player);
        startActivity(intentPlayerEdit);

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof PlayerAdapter.PlayerViewHolder) {

            mAddPlayerFAB.setVisibility(View.GONE);

            String playerName = mPlayers.get(viewHolder.getAdapterPosition()).getPlayerFirstName();

            final Player deletePlayer = mPlayers.get(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            mPlayerAdapter.removePlayer(viewHolder.getAdapterPosition());
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, playerName + " removed from list!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPlayerAdapter.restorePlayer(deletePlayer, deleteIndex);
                }
            });
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        boolean success = Utils.deletePlayerFromDatabasebyID(mContext, deletePlayer.getPlayerId());
                        if (!success) {
                            Log.e(RosterActivity.class.getSimpleName(), "Error Deleting Player from Database");
                        }
                        success = false;
                        success = Utils.deleteScoreFromDatabasebyPlayerID(mContext, deletePlayer.getPlayerId());
                        if (!success) {
                            Log.e(RosterActivity.class.getSimpleName(), "Error Deleting Score by PlayerID from Database");
                        }
                    }
                    mAddPlayerFAB.setVisibility(View.VISIBLE);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }
}
