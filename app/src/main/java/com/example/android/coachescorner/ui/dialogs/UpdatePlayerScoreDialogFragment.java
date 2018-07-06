package com.example.android.coachescorner.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.data.Player;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Dialog to update a users Goals, Assists, and Saves during a game
 */

public class UpdatePlayerScoreDialogFragment extends DialogFragment {

    @BindView(R.id.tv_score_edit_player_name) TextView mPlayerName;
    @BindView(R.id.btn_score_edit_goal_adder) ImageButton mBtnGoalAdder;
    @BindView(R.id.tv_score_edit_goal_total) TextView mTextViewGoalTotal;
    @BindView(R.id.btn_score_edit_goal_minus) ImageButton mBtnGoalMinus;
    @BindView(R.id.btn_score_edit_assist_adder) ImageButton mBtnAssistAdder;
    @BindView(R.id.tv_score_edit_assist_total) TextView mTextViewAssistTotal;
    @BindView(R.id.btn_score_edit_assist_minus) ImageButton mBtnAssistMinus;
    @BindView(R.id.btn_score_edit_saves_adder) ImageButton mBtnSavesAdder;
    @BindView(R.id.tv_score_edit_saves_total) TextView mTextViewSavesTotal;
    @BindView(R.id.btn_score_edit_saves_minus) ImageButton mBtnSavesMinus;
    @BindView(R.id.btn_score_edit_submit) Button mSaveButton;

    private static final String PARCABLE_PLAYER_KEY = "Player";
    private static final String GOAL_COUNT_KEY = "goalCount";

    private Player mPlayer;
    private int mCurrentGoalCount;

    private UpdatePlayerScoreDialogListener mUpdatePlayerScoreCallBack;

    private static String mDialogBoxTitle;

    public interface UpdatePlayerScoreDialogListener {
        void onFinishedUpdatePlayerScore(int playerId, int currentGoalCount, int goals, int assists, int saves);
    }

    public UpdatePlayerScoreDialogFragment() {
        //Required empty constructor
    }

    public void setDialogTitle(String title) {
        mDialogBoxTitle = title;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mUpdatePlayerScoreCallBack = (UpdatePlayerScoreDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement UpdatePlayerScoreDialogListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null && savedInstanceState.containsKey(PARCABLE_PLAYER_KEY)) {
            mPlayer = savedInstanceState.getParcelable(PARCABLE_PLAYER_KEY);
            mCurrentGoalCount = savedInstanceState.getInt(GOAL_COUNT_KEY);
        }

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_score_dialogfragment,container);
        ButterKnife.bind(this, view);

        mPlayerName.setText(mPlayer.getPlayerFirstName());
        mTextViewGoalTotal.setText(String.valueOf(mPlayer.getGoalCount()));
        mTextViewAssistTotal.setText(String.valueOf(mPlayer.getAssitCount()));
        mTextViewSavesTotal.setText(String.valueOf(mPlayer.getSaveCount()));

        mBtnGoalAdder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.addGoal();
                mTextViewGoalTotal.setText(String.valueOf(mPlayer.getGoalCount()));
            }
        });

        mBtnGoalMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.subtractGoal();
                mTextViewGoalTotal.setText(String.valueOf(mPlayer.getGoalCount()));
            }
        });


        mBtnAssistAdder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.addAssit();
                mTextViewAssistTotal.setText(String.valueOf(mPlayer.getAssitCount()));
            }
        });

        mBtnAssistMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.subtractAssit();
                mTextViewAssistTotal.setText(String.valueOf(mPlayer.getAssitCount()));
            }
        });

        mBtnSavesAdder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.addSaves();
                mTextViewSavesTotal.setText(String.valueOf(mPlayer.getSaveCount()));
            }
        });

        mBtnSavesMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.subtractSaves();
                mTextViewSavesTotal.setText(String.valueOf(mPlayer.getSaveCount()));
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUpdatePlayerScoreCallBack.onFinishedUpdatePlayerScore(mPlayer.getPlayerId(), mCurrentGoalCount, mPlayer.getGoalCount(), mPlayer.getAssitCount(), mPlayer.getSaveCount());
                dismiss();
            }
        });

        getDialog().setTitle(mDialogBoxTitle);

        return view;
    }

    public void setPlayer(Player player) {
        mPlayer = player;
        mCurrentGoalCount = mPlayer.getGoalCount();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(PARCABLE_PLAYER_KEY, mPlayer);
        outState.putInt(GOAL_COUNT_KEY, mCurrentGoalCount);
        super.onSaveInstanceState(outState);
    }
}
