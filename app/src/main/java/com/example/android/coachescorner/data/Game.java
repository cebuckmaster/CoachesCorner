package com.example.android.coachescorner.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by cebuc on 4/22/2018.
 */

public class Game implements Parcelable{

    private int mGameId;
    private String mGameDate;
    private String mGameTime;
    private String mOpponentName;
    private int mOpponentScore;
    private int mTeamScore;
    private String mHomeOrAway;
    private String mGameNote;
    private String mFieldLocation;

    public Game(int gameId, String gameDate, String gameTime, String opponentName, int opponentScore, int teamScore, String homeOrAway, String gameNote, String fieldLocation) {
        mGameId = gameId;
        mGameDate = gameDate;
        mGameTime = gameTime;
        mOpponentName = opponentName;
        mOpponentScore = opponentScore;
        mTeamScore = teamScore;
        mHomeOrAway = homeOrAway;
        mGameNote = gameNote;
        mFieldLocation = fieldLocation;
    }

    public Game() {

    }

    private Game(Parcel in) {
        mGameId = in.readInt();
        mGameDate = in.readString();
        mGameTime = in.readString();
        mOpponentName = in.readString();
        mOpponentScore = in.readInt();
        mTeamScore = in.readInt();
        mHomeOrAway = in.readString();
        mGameNote = in.readString();
        mFieldLocation = in.readString();
    }

    public void setGameDate(String gameDate) {
        mGameDate = gameDate;
    }

    public void setGameTime(String gameTime) {
        mGameTime = gameTime;
    }

    public void setOpponentName(String name) {
        mOpponentName = name;
    }

    public void addOpponentScore(){
        mOpponentScore++;
    }

    public void subtractOpponentScore() {
        if (mOpponentScore > 0) {
            mOpponentScore--;
        }
    }

    public void setGameNote(String note) {
        mGameNote = note;
    }

    public void setFieldLocation(String location) {
        mFieldLocation = location;
    }

    public void setHomeOrAway(String homeOrAway) {
        mHomeOrAway = homeOrAway;
    }


    public int getGameId() {
        return mGameId;
    }

    public String getGameDate() {
        return mGameDate;
    }

    public String getGameTime() {
        return mGameTime;
    }

    public String getOpponentName() {
        return mOpponentName;
    }

    public int getOpponentScore() {
        return mOpponentScore;
    }

    public int getTeamScore() {

        return mTeamScore;
    }

    public String getHomeOrAway() {
        return mHomeOrAway;
    }

    public String getGameNote() {
        return mGameNote;
    }

    public String getFieldLocation() {
        return mFieldLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mGameId);
        parcel.writeString(mGameDate);
        parcel.writeString(mGameTime);
        parcel.writeString(mOpponentName);
        parcel.writeInt(mOpponentScore);
        parcel.writeInt(mTeamScore);
        parcel.writeString(mHomeOrAway);
        parcel.writeString(mGameNote);
        parcel.writeString(mFieldLocation);

    }

    public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }
        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

}
