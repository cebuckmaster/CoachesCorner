package com.example.android.coachescorner.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.data.Player;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * Created by cebuc on 4/25/2018.
 */

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private static final String TAG = PlayerAdapter.class.getSimpleName();
    private ArrayList<Player> mPlayers;
    private Context mContext;

    private final PlayerAdapterOnClickHandler mClickHandler;

    public interface PlayerAdapterOnClickHandler {
        void onClick(Player player);
    }


//    public PlayerAdapter(Context context, ArrayList<Player> players) {
    public PlayerAdapter(Context context, PlayerAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }


    public class PlayerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.civ_player_picture) CircleImageView mPlayerPicture;
        @BindView(R.id.tv_player_name) TextView mPlayerName;
        @BindView(R.id.tv_player_number) TextView mPlayerNum;
        @BindView(R.id.tv_goals_scored) TextView mGoalsScored;
        @BindView(R.id.tv_assits_made) TextView mAssistsMade;
        @BindView(R.id.tv_saves_made) TextView mSavesMade;
        @BindView(R.id.player_card_view_background) RelativeLayout mViewBackground;
        public FrameLayout viewForeground;
        public RelativeLayout viewBackground;

        public PlayerViewHolder(View itemView) {
            super(itemView);
            viewForeground = itemView.findViewById(R.id.player_card_view_foreground);
            viewBackground = itemView.findViewById(R.id.player_card_view_background);
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

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View playerView = inflater.inflate(R.layout.player_card_item, parent, false);
        return new PlayerViewHolder(playerView);
    }

    @Override
    public void onBindViewHolder(PlayerAdapter.PlayerViewHolder holder, int position) {

        if (mPlayers.get(position).getPlayerPic() != null) {
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(mPlayers.get(position).getPlayerPic());
                Bitmap storedBitMap = BitmapFactory.decodeStream(inputStream);
                holder.mPlayerPicture.setImageBitmap(storedBitMap);
            } catch (Exception e) {
                Log.e(PlayerAdapter.class.getSimpleName(), "Error trying to load Pic - " + e);
            }
        }
        holder.mPlayerName.setText(mPlayers.get(position).getPlayerFirstName() + " " + mPlayers.get(position).getPlayerLastName());
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

    public void removePlayer(int position) {
        mPlayers.remove(position);
        notifyItemRemoved(position);
    }

    public void restorePlayer(Player player, int position) {
        mPlayers.add(position, player);
        notifyItemInserted(position);
    }

}
