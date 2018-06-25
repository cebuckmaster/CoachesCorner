package com.example.android.coachescorner.data;

/**
 * Created by cebuc on 5/6/2018.
 */

public class Score {

    private int mScoreId;
    private int mGameId;
    private int mPlayerId;
    private int mGoals;
    private int mAssits;
    private int mSaves;

    public Score(int id, int gameId, int playerId, int goals, int assits, int saves) {
        mScoreId = id;
        mGameId = gameId;
        mPlayerId = playerId;
        mGoals = goals;
        mAssits = assits;
        mSaves = saves;

    }


    public int getScoreId() {
        return mScoreId;
    }

    public int getGameId() {
        return mGameId;
    }

    public int getPlayerId() {
        return mPlayerId;
    }

    public int getGoals() {
        return mGoals;
    }

    public int getAssits() {
        return mAssits;
    }

    public int getSaves() {
        return mSaves;
    }

}
