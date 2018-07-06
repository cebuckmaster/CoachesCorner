package com.example.android.coachescorner.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.data.Player;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * ScoreCardAdapter used to display Player information during Game Day RecyclerView
 */

public class ScoreCardAdapter extends RecyclerView.Adapter<ScoreCardAdapter.ScoreCardViewHolder> {

    private static final String TAG = ScoreCardAdapter.class.getSimpleName();

    private ArrayList<Player> mPlayers;
    private Context mContext;

    private final ScoreCardAdapterOnClickHandler mClickHandler;

    public interface ScoreCardAdapterOnClickHandler {
        void onClick(Player player);
    }

    public ScoreCardAdapter(Context context, ScoreCardAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }


    public class ScoreCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.civ_player_picture) CircleImageView mPlayerPicture;
        @BindView(R.id.tv_player_name) TextView mPlayerName;
        @BindView(R.id.tv_player_number) TextView mPlayerNum;
        @BindView(R.id.tv_goals_scored) TextView mGoalsScored;
        @BindView(R.id.tv_assits_made) TextView mAssistsMade;
        @BindView(R.id.tv_saves_made) TextView mSavesMade;

        public ScoreCardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Player player = new Player(
                    mPlayers.get(adapterPosition).getPlayerId(),
                    mPlayers.get(adapterPosition).getPlayerFirstName(),
                    mPlayers.get(adapterPosition).getPlayerLastName(),
                    mPlayers.get(adapterPosition).getPlayerNum(),
                    mPlayers.get(adapterPosition).getPlayerPic(),
                    mPlayers.get(adapterPosition).getGoalCount(),
                    mPlayers.get(adapterPosition).getAssitCount(),
                    mPlayers.get(adapterPosition).getSaveCount(),
                    mPlayers.get(adapterPosition).getPlayerNote());
            mClickHandler.onClick(player);
        }
    }
    public ScoreCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View scoreView = inflater.inflate(R.layout.player_card_item, parent, false);
        return new ScoreCardViewHolder(scoreView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreCardViewHolder holder, int position) {
        if (mPlayers.get(position).getPlayerPic() != null) {
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(mPlayers.get(position).getPlayerPic());
                Bitmap storedBitMap = BitmapFactory.decodeStream(inputStream);
                holder.mPlayerPicture.setImageBitmap(storedBitMap);
            } catch (Exception e) {
                Log.e(ScoreCardAdapter.class.getSimpleName(), "Error trying to load Pic - " + e);
            }
        }
        String playerFullName = mPlayers.get(position).getPlayerFirstName() + " " + mPlayers.get(position).getPlayerLastName();
        holder.mPlayerName.setText(playerFullName);
        holder.mPlayerNum.setText(mPlayers.get(position).getPlayerNum());
        holder.mGoalsScored.setText(String.valueOf(mPlayers.get(position).getGoalCount()));
        holder.mAssistsMade.setText(String.valueOf(mPlayers.get(position).getAssitCount()));
        holder.mSavesMade.setText(String.valueOf(mPlayers.get(position).getSaveCount()));

    }

    @Override
    public int getItemCount() {
        if (mPlayers == null) {
            return 0;
        }
        return mPlayers.size();
    }

    public void setPlayers(ArrayList<Player> players) {
        mPlayers = players;
        notifyDataSetChanged();
    }
}
