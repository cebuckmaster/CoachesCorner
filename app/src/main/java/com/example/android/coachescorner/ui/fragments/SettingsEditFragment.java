package com.example.android.coachescorner.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by cebuc on 5/9/2018.
 */

public class SettingsEditFragment extends Fragment {

    private Context mContext;
    @BindView(R.id.et_team_input_name) EditText mTeamName;
    @BindView(R.id.et_head_coach_name) EditText mHeadCoachName;
    @BindView(R.id.et_assist_coach1_name) EditText mAssistCoachName1;
    @BindView(R.id.et_assist_coach2_name) EditText mAssistCoachName2;
    @BindView(R.id.spr_game_age_group) Spinner mSprAgeGroup;
    @BindView(R.id.spr_game_format) Spinner mSprGameFormat;
    @BindView(R.id.et_game_time) EditText mGameTimeEditText;
    @BindView(R.id.spr_game_type) Spinner mSprGameType;
    @BindView(R.id.et_substitution_time) EditText mSubTime;
    @BindView(R.id.btn_game_setting_submit) Button mSubmitButton;

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

    public SettingsEditFragment() {
        //Required empty public Constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        mContext = view.getContext();
        mAgeGroupArray = mContext.getResources().getStringArray(R.array.age_group);
        mGameFormatArray = mContext.getResources().getStringArray(R.array.gameformat);
        mHalfQuarterArray = mContext.getResources().getStringArray(R.array.half_quarter);

        mSharedPreferences = getActivity().getSharedPreferences(Utils.CC_PREFERENCES, Context.MODE_PRIVATE);
        if (mSharedPreferences.contains(Utils.PREF_TEAM_NAME)) {
            mTeamName.setText(mSharedPreferences.getString(Utils.PREF_TEAM_NAME, Utils.DEFAULT_TEAM_NAME));
            mHeadCoachName.setText(mSharedPreferences.getString(Utils.PREF_COACH_NAME, ""));
            mAssistCoachName1.setText(mSharedPreferences.getString(Utils.PREF_ASSIST1_NAME, ""));
            mAssistCoachName2.setText(mSharedPreferences.getString(Utils.PREF_ASSIST2_NAME, ""));
            mAgeGroup = mSharedPreferences.getString(Utils.PREF_AGE_GROUP, mAgeGroupArray[0]);
            mGameFormat = mSharedPreferences.getString(Utils.PREF_GAME_FORMAT, mGameFormatArray[0]);
            mGameTime = mSharedPreferences.getInt(Utils.PREF_GAME_TIME, 25);
            mGameType = mSharedPreferences.getString(Utils.PREF_GAME_TYPE, mHalfQuarterArray[0]);
            mTimeBetweenSubstitutions = mSharedPreferences.getInt(Utils.PREF_SUB_TIME, 6);
            setGameFormationSpinner();
            setGameTypeSpinner();
            setAgeGroupSpinner();
            mGameTimeEditText.setText(String.valueOf(mGameTime));
            mSubTime.setText(String.valueOf(mTimeBetweenSubstitutions));
            mSavedPreferencesFound = true;
        } else {
            mSavedPreferencesFound = false;
        }


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
                editor.putString(Utils.PREF_TEAM_NAME, mTeamName.getText().toString());
                editor.putString(Utils.PREF_COACH_NAME, mHeadCoachName.getText().toString());
                editor.putString(Utils.PREF_ASSIST1_NAME, mAssistCoachName1.getText().toString());
                editor.putString(Utils.PREF_ASSIST2_NAME, mAssistCoachName2.getText().toString());
                editor.putString(Utils.PREF_AGE_GROUP, mAgeGroup);
                editor.putString(Utils.PREF_GAME_FORMAT, mGameFormat);
                editor.putInt(Utils.PREF_GAME_TIME, mGameTime);
                editor.putString(Utils.PREF_GAME_TYPE, mGameType);
                editor.putInt(Utils.PREF_SUB_TIME, mTimeBetweenSubstitutions);
                editor.commit();


                getActivity().onBackPressed();
            }
        });


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return view;
    }


    private void  setGameFormationSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.gameformat, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSprGameFormat.setAdapter(adapter);
        if (mGameFormat != null) {
            int spinnerPosition = adapter.getPosition(mGameFormat);
            mSprGameFormat.setSelection(spinnerPosition);
        }
    }

    private void  setGameTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.half_quarter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSprGameType.setAdapter(adapter);
        if (mGameType != null) {
            int spinnerPosition = adapter.getPosition(mGameType);
            mSprGameType.setSelection(spinnerPosition);
        }
    }

    private void  setAgeGroupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.age_group, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSprAgeGroup.setAdapter(adapter);
        if (mAgeGroup != null) {
            int spinnerPosition = adapter.getPosition(mAgeGroup);
            mSprAgeGroup.setSelection(spinnerPosition);
        }
    }

    private void setGameDefaultsByAgeGroup() {

        Context context = getActivity();

        switch (mAgeGroup) {
            case "U3":
                mGameFormat = context.getResources().getString(R.string.u6_under_game_formation);
                mGameTime = 10;
                mGameType = context.getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = mGameTime / 2;
                break;
            case "U4":
                mGameFormat = context.getResources().getString(R.string.u6_under_game_formation);
                mGameTime = 10;
                mGameType = context.getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = mGameTime / 2;
                break;
            case "U5":
                mGameFormat = context.getResources().getString(R.string.u6_under_game_formation);
                mGameTime = 10;
                mGameType = context.getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = mGameTime / 2;
                break;
            case "U6":
                mGameFormat = context.getResources().getString(R.string.u6_under_game_formation);
                mGameTime = 10;
                mGameType = context.getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = mGameTime / 2;
                break;
            case "U7":
                mGameFormat = context.getResources().getString(R.string.u8_under_game_formation);
                mGameTime = 20;
                mGameType = context.getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = mGameTime / 4;
                break;
            case "U8":
                mGameFormat = context.getResources().getString(R.string.u8_under_game_formation);
                mGameTime = 20;
                mGameType = context.getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = mGameTime / 4;
                break;
            case "U9":
                mGameFormat = context.getResources().getString(R.string.u11_under_game_formation);
                mGameTime = 25;
                mGameType = context.getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 6;
                break;
            case "U10":
                mGameFormat = context.getResources().getString(R.string.u11_under_game_formation);
                mGameTime = 25;
                mGameType = context.getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 6;
                break;
            case "U11":
                mGameFormat = context.getResources().getString(R.string.u11_under_game_formation);
                mGameTime = 25;
                mGameType = context.getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 6;
                break;
            case "U12":
                mGameFormat = context.getResources().getString(R.string.u13_under_game_formation);
                mGameTime = 30;
                mGameType = context.getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 7;
                break;
            case "U13":
                mGameFormat = context.getResources().getString(R.string.u13_under_game_formation);
                mGameTime = 30;
                mGameType = context.getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 7;
                break;
            case "U14":
                mGameFormat = context.getResources().getString(R.string.u16_under_game_formation);
                mGameTime = 35;
                mGameType = context.getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 8;
                break;
            case "U15":
                mGameFormat = context.getResources().getString(R.string.u16_under_game_formation);
                mGameTime = 35;
                mGameType = context.getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 8;
                break;
            case "U16":
                mGameFormat = context.getResources().getString(R.string.u16_under_game_formation);
                mGameTime = 40;
                mGameType = context.getResources().getString(R.string.game_half);
                mTimeBetweenSubstitutions = 10;
                break;
        }

    }


}
