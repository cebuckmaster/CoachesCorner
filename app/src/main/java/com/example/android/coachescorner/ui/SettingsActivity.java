package com.example.android.coachescorner.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.common.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    private Context mContext;
    @BindView(R.id.et_team_input_name) EditText mTeamNameEditText;
    @BindView(R.id.et_head_coach_name) EditText mHeadCoachNameEditText;
    @BindView(R.id.et_assist_coach1_name) EditText mAssistCoachName1EditText;
    @BindView(R.id.et_assist_coach2_name) EditText mAssistCoachName2EditText;
    @BindView(R.id.spr_game_age_group) Spinner mSprAgeGroup;
    @BindView(R.id.spr_game_format) Spinner mSprGameFormat;
    @BindView(R.id.et_game_time) EditText mGameTimeEditText;
    @BindView(R.id.spr_game_type) Spinner mSprGameType;
    @BindView(R.id.et_substitution_time) EditText mSubTime;
    @BindView(R.id.btn_game_setting_submit) Button mSubmitButton;

    private static final String TEAMNAMEKEY = "TeamName";
    private static final String HEADCOACHKEY = "HeadCoach";
    private static final String ASSIST1COACHKEY = "Assist1";
    private static final String ASSIST2COACHKEY = "Assist2";
    private static final String AGEGROUPKEY = "AgeGroup";
    private static final String GAMEFORMATKEY = "Format";
    private static final String TIMEKEY = "Time";
    private static final String TYPEKEY = "Type";
    private static final String SUBTIMEKEY = "SubTIME";

    private String mTeamName;
    private String mHeadCoachName;
    private String mAssist1Name;
    private String mAssist2Name;
    private String mGameFormat;
    private int mGameTime;
    private String mGameType;
    private int mTimeBetweenSubstitutions;
    private String mAgeGroup;
    private SharedPreferences mSharedPreferences;
    private String[] mAgeGroupArray;
    private String[] mGameFormatArray;
    private String[] mHalfQuarterArray;
    private Boolean mSavedPreferencesFound;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TEAMNAMEKEY, mTeamName);
        outState.putString(HEADCOACHKEY, mHeadCoachName);
        outState.putString(ASSIST1COACHKEY, mAssist1Name);
        outState.putString(ASSIST2COACHKEY, mAssist2Name);
        outState.putString(AGEGROUPKEY, mAgeGroup);
        outState.putString(GAMEFORMATKEY, mGameFormat);
        outState.putInt(TIMEKEY, mGameTime);
        outState.putString(TYPEKEY, mGameType);
        outState.putInt(SUBTIMEKEY, mTimeBetweenSubstitutions);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        mContext = this;

        mAgeGroupArray = mContext.getResources().getStringArray(R.array.age_group);
        mGameFormatArray = mContext.getResources().getStringArray(R.array.gameformat);
        mHalfQuarterArray = mContext.getResources().getStringArray(R.array.half_quarter);
        mSharedPreferences = getSharedPreferences(Utils.CC_PREFERENCES, Context.MODE_PRIVATE);


        if (savedInstanceState != null) {
            mTeamName = savedInstanceState.getString(TEAMNAMEKEY);
            mHeadCoachName = savedInstanceState.getString(HEADCOACHKEY);
            mAssist1Name = savedInstanceState.getString(ASSIST1COACHKEY);
            mAssist2Name = savedInstanceState.getString(ASSIST2COACHKEY);
            mAgeGroup = savedInstanceState.getString(AGEGROUPKEY);
            mGameFormat = savedInstanceState.getString(GAMEFORMATKEY);
            mGameTime = savedInstanceState.getInt(TIMEKEY);
            mGameType = savedInstanceState.getString(TYPEKEY);
            mTimeBetweenSubstitutions = savedInstanceState.getInt(SUBTIMEKEY);
            mSavedPreferencesFound = false;
        } else {
            if (mSharedPreferences.contains(Utils.PREF_TEAM_NAME)) {
                mTeamName = mSharedPreferences.getString(Utils.PREF_TEAM_NAME, Utils.DEFAULT_TEAM_NAME);
                mHeadCoachName = mSharedPreferences.getString(Utils.PREF_COACH_NAME, "");
                mAssist1Name = mSharedPreferences.getString(Utils.PREF_ASSIST1_NAME, "");
                mAssist2Name = mSharedPreferences.getString(Utils.PREF_ASSIST2_NAME, "");
                mAgeGroup = mSharedPreferences.getString(Utils.PREF_AGE_GROUP, mAgeGroupArray[0]);
                mGameFormat = mSharedPreferences.getString(Utils.PREF_GAME_FORMAT, mGameFormatArray[0]);
                mGameTime = mSharedPreferences.getInt(Utils.PREF_GAME_TIME, 25);
                mGameType = mSharedPreferences.getString(Utils.PREF_GAME_TYPE, mHalfQuarterArray[0]);
                mTimeBetweenSubstitutions = mSharedPreferences.getInt(Utils.PREF_SUB_TIME, 6);
                mSavedPreferencesFound = true;
            } else {
                mSavedPreferencesFound = false;
            }
        }


        mTeamNameEditText.setText(mTeamName);
        mHeadCoachNameEditText.setText(mHeadCoachName);
        mAssistCoachName1EditText.setText(mAssist1Name);
        mAssistCoachName2EditText.setText(mAssist2Name);

        setGameFormationSpinner();
        setGameTypeSpinner();
        setAgeGroupSpinner();
        mGameTimeEditText.setText(String.valueOf(mGameTime));
        mSubTime.setText(String.valueOf(mTimeBetweenSubstitutions));

        mSprAgeGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mAgeGroup = adapterView.getItemAtPosition(position).toString();
                if (!mSavedPreferencesFound) {
                    setGameDefaultsByAgeGroup();
                    setGameFormationSpinner();
                    mGameTimeEditText.setText(String.valueOf(mGameTime));
                    setGameTypeSpinner();
                    mSubTime.setText(String.valueOf(mTimeBetweenSubstitutions));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSprGameFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mGameFormat = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSprGameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mGameType = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGameTime = Integer.parseInt(mGameTimeEditText.getText().toString());
                mTimeBetweenSubstitutions = Integer.parseInt(mSubTime.getText().toString());
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(Utils.PREF_TEAM_NAME, mTeamNameEditText.getText().toString());
                editor.putString(Utils.PREF_COACH_NAME, mHeadCoachNameEditText.getText().toString());
                editor.putString(Utils.PREF_ASSIST1_NAME, mAssistCoachName1EditText.getText().toString());
                editor.putString(Utils.PREF_ASSIST2_NAME, mAssistCoachName2EditText.getText().toString());
                editor.putString(Utils.PREF_AGE_GROUP, mAgeGroup);
                editor.putString(Utils.PREF_GAME_FORMAT, mGameFormat);
                editor.putInt(Utils.PREF_GAME_TIME, mGameTime);
                editor.putString(Utils.PREF_GAME_TYPE, mGameType);
                editor.putInt(Utils.PREF_SUB_TIME, mTimeBetweenSubstitutions);
                editor.apply();
                onBackPressed();
            }
        });


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void  setGameFormationSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.gameformat, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSprGameFormat.setAdapter(adapter);
        if (mGameFormat != null) {
            int spinnerPosition = adapter.getPosition(mGameFormat);
            mSprGameFormat.setSelection(spinnerPosition);
        }
    }

    private void  setGameTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.half_quarter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSprGameType.setAdapter(adapter);
        if (mGameType != null) {
            int spinnerPosition = adapter.getPosition(mGameType);
            mSprGameType.setSelection(spinnerPosition);
        }
    }

    private void  setAgeGroupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.age_group, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSprAgeGroup.setAdapter(adapter);
        if (mAgeGroup != null) {
            int spinnerPosition = adapter.getPosition(mAgeGroup);
            mSprAgeGroup.setSelection(spinnerPosition);
        }
    }

    private void setGameDefaultsByAgeGroup() {

         switch (mAgeGroup) {
            case "U3":
                mGameFormat = getResources().getString(R.string.u6_under_game_formation);
                mGameTime = 10;
                mGameType = getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = mGameTime / 2;
                break;
            case "U4":
                mGameFormat = getResources().getString(R.string.u6_under_game_formation);
                mGameTime = 10;
                mGameType = getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = mGameTime / 2;
                break;
            case "U5":
                mGameFormat = getResources().getString(R.string.u6_under_game_formation);
                mGameTime = 10;
                mGameType = getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = mGameTime / 2;
                break;
            case "U6":
                mGameFormat = getResources().getString(R.string.u6_under_game_formation);
                mGameTime = 10;
                mGameType = getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = mGameTime / 2;
                break;
            case "U7":
                mGameFormat = getResources().getString(R.string.u8_under_game_formation);
                mGameTime = 20;
                mGameType = getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = mGameTime / 4;
                break;
            case "U8":
                mGameFormat = getResources().getString(R.string.u8_under_game_formation);
                mGameTime = 20;
                mGameType = getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = mGameTime / 4;
                break;
            case "U9":
                mGameFormat = getResources().getString(R.string.u11_under_game_formation);
                mGameTime = 25;
                mGameType = getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 6;
                break;
            case "U10":
                mGameFormat = getResources().getString(R.string.u11_under_game_formation);
                mGameTime = 25;
                mGameType = getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 6;
                break;
            case "U11":
                mGameFormat = getResources().getString(R.string.u11_under_game_formation);
                mGameTime = 25;
                mGameType = getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 6;
                break;
            case "U12":
                mGameFormat = getResources().getString(R.string.u13_under_game_formation);
                mGameTime = 30;
                mGameType = getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 7;
                break;
            case "U13":
                mGameFormat = getResources().getString(R.string.u13_under_game_formation);
                mGameTime = 30;
                mGameType = getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 7;
                break;
            case "U14":
                mGameFormat = getResources().getString(R.string.u16_under_game_formation);
                mGameTime = 35;
                mGameType = getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 8;
                break;
            case "U15":
                mGameFormat = getResources().getString(R.string.u16_under_game_formation);
                mGameTime = 35;
                mGameType = getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 8;
                break;
            case "U16":
                mGameFormat = getResources().getString(R.string.u16_under_game_formation);
                mGameTime = 40;
                mGameType = getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 10;
                break;
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
