package com.example.android.coachescorner.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.common.Utils;
import com.example.android.coachescorner.data.Game;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Game Adapter Used to display Games in RecyclerView
 */

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private static final String TAG = GameAdapter.class.getSimpleName();
    private ArrayList<Game> mGames;
    private Context mContext;
    private String mTeamName;

    private final GameAdapterOnClickHandler mClickHandler;

    public interface GameAdapterOnClickHandler {
        void onClick(Game game);
    }

    public GameAdapter(Context context, GameAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    public class GameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_game_date) TextView mGameDate;
        @BindView(R.id.tv_game_time) TextView mGameTime;
        @BindView(R.id.tv_game_card_home_team_name) TextView mHomeTeamName;
        @BindView(R.id.tv_game_card_home_team_score) TextView mHomeTeamScore;
        @BindView(R.id.tv_game_card_away_team_name) TextView mAwayTeamName;
        @BindView(R.id.tv_game_card_away_team_score) TextView mAwayTeamScore;
        @BindView(R.id.tv_win_lose_indicator) TextView mWinLose;
        public FrameLayout viewForeground;
        public RelativeLayout viewBackGround;
        private Context mContext;

        public GameViewHolder(View itemView) {
            super(itemView);
            viewForeground = itemView.findViewById(R.id.game_card_view_foreground);
            viewBackGround = itemView.findViewById(R.id.game_card_view_background);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPostion = getAdapterPosition();
            Game game = new Game(
                    mGames.get(adapterPostion).getGameId(),
                    mGames.get(adapterPostion).getGameDate(),
                    mGames.get(adapterPostion).getGameTime(),
                    mGames.get(adapterPostion).getOpponentName(),
                    mGames.get(adapterPostion).getOpponentScore(),
                    mGames.get(adapterPostion).getTeamScore(),
                    mGames.get(adapterPostion).getHomeOrAway(),
                    mGames.get(adapterPostion).getGameNote(),
                    mGames.get(adapterPostion).getFieldLocation());
            mClickHandler.onClick(game);
        }
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Context context = parent.getContext();
        mContext = parent.getContext();

        mTeamName = Utils.getTeamName(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View gameView = inflater.inflate(R.layout.game_card_item, parent, false);
        return new GameViewHolder(gameView);
    }

    @Override
    public void onBindViewHolder(GameAdapter.GameViewHolder holder, int position) {
        holder.mGameDate.setText(mGames.get(position).getGameDate());
        holder.mGameTime.setText(mGames.get(position).getGameTime());
        holder.mWinLose.setText("");
        if (mGames.get(position).getTeamScore() > mGames.get(position).getOpponentScore()) {
            holder.mWinLose.setText(mContext.getResources().getString(R.string.win_label));
            holder.mWinLose.setTypeface(null, Typeface.BOLD);
            holder.mWinLose.setTextColor(mContext.getColor(R.color.colorAccent));
        } else {
            holder.mWinLose.setVisibility(View.GONE);
        }
        if (mGames.get(position).getHomeOrAway().equals(mContext.getResources().getString(R.string.home_label))) {
            holder.mHomeTeamName.setText(mTeamName);
            holder.mHomeTeamScore.setText(String.valueOf(mGames.get(position).getTeamScore()));
            holder.mAwayTeamName.setText(mGames.get(position).getOpponentName());
            holder.mAwayTeamScore.setText(String.valueOf(mGames.get(position).getOpponentScore()));
        } else {
            holder.mHomeTeamName.setText(mGames.get(position).getOpponentName());
            holder.mHomeTeamScore.setText(String.valueOf(mGames.get(position).getOpponentScore()));
            holder.mAwayTeamName.setText(mTeamName);
            holder.mAwayTeamScore.setText(String.valueOf(mGames.get(position).getTeamScore()));
        }
    }

    @Override
    public int getItemCount() {
        if (mGames == null) {
            return 0;
        }
        return mGames.size();
    }

    public void setGames(ArrayList<Game> games) {
        mGames = games;
        notifyDataSetChanged();
    }

    public void removeGame(int position) {
        mGames.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreGame(Game game, int position) {
        mGames.add(position, game);
        notifyItemInserted(position);
    }

}
