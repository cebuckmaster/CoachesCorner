package com.example.android.coachescorner.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.common.Utils;
import com.example.android.coachescorner.data.Game;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Edit Game Activity called withing Game Day.
 */
public class EditGameActivity extends AppCompatActivity {

    private Game mGame;
    private Context mContext;

    private static final String PARCELABLEKEY = "Game";



    @BindView(R.id.btn_edit_game_date) Button mEditGameDate;
    @BindView(R.id.btn_edit_game_time) Button mEditGameTime;
    @BindView(R.id.et_edit_game_opponent_name) EditText mOpponentName;
    @BindView(R.id.spr_edit_game_home_or_away) Spinner mHomeOrAwaySpinner;
    @BindView(R.id.et_edit_game_field_location) EditText mFieldLocation;
    @BindView(R.id.et_edit_game_note) EditText mGameNote;
    @BindView(R.id.btn_edit_game_submit) Button mSubmitBtn;

    private String mGameDate;
    private String mGameTime;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private String mHomeOrAway;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_game);

        if (savedInstanceState  != null) {
            if (savedInstanceState.containsKey(PARCELABLEKEY)) {
                mGame = savedInstanceState.getParcelable(PARCELABLEKEY);
            } else {
                mGame = new Game();
            }
        } else {
            Intent intentThatStartedThisActivity = getIntent();
            if (intentThatStartedThisActivity.hasExtra("Game")) {
                mGame = (Game) intentThatStartedThisActivity.getParcelableExtra("Game");
            }
        }

        ButterKnife.bind(this);

        mContext = this;

        mGameDate = mGame.getGameDate();
        mGameTime = mGame.getGameTime();

        mEditGameDate.setText(mGame.getGameDate());
        mEditGameTime.setText(mGame.getGameTime());
        mHomeOrAway = mGame.getHomeOrAway();
        mOpponentName.setText(mGame.getOpponentName());
        mFieldLocation.setText(mGame.getFieldLocation());
        mGameNote.setText(mGame.getGameNote());

        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);


        mEditGameDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mGameDate = ((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                        mEditGameDate.setText(mGameDate);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        mEditGameTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String am_pm = "";

                        Calendar dateTime = Calendar.getInstance();
                        dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateTime.set(Calendar.MINUTE, minute);

                        if (dateTime.get(Calendar.AM_PM) == Calendar.AM) {
                            am_pm = getString(R.string.am);
                        } else if (dateTime.get(Calendar.AM_PM) == Calendar.PM) {
                            am_pm = getString(R.string.pm);
                        }

                        if (hourOfDay == 0 && am_pm.equals(getString(R.string.am))) {
                            hourOfDay = 12;
                        }

                        if (hourOfDay > 12 && am_pm.equals(getString(R.string.pm))) {
                            hourOfDay = hourOfDay - 12;
                        }

                        String minuteString = "00";
                        if (minute == 0) {
                            mGameTime = hourOfDay + ":00" + " " + am_pm;
                        } else {
                            if (minute < 10) {
                                minuteString = "0" + minute;
                            } else {
                                minuteString = String.valueOf(minute);
                            }
                            mGameTime = hourOfDay + ":" + minuteString + " " + am_pm;
                        }
                        mEditGameTime.setText(mGameTime);
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        if (mHomeOrAway.equals("Home")) {
            mHomeOrAwaySpinner.setSelection(0);
        } else {
            mHomeOrAwaySpinner.setSelection(1);
        }
        mHomeOrAwaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mHomeOrAway = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGameDate == null || mGameDate.isEmpty()) {
                    Toast.makeText(mContext, getString(R.string.must_select_date), Toast.LENGTH_SHORT).show();
                } else if (mGameTime == null || mGameTime.isEmpty()) {
                    Toast.makeText(mContext, getString(R.string.must_select_time), Toast.LENGTH_SHORT).show();
                } else if (mOpponentName.getText().toString() == null || mOpponentName.getText().toString().isEmpty()) {
                    Toast.makeText(mContext, getString(R.string.must_select_oppt_name), Toast.LENGTH_SHORT).show();
                } else {
                    mGame.setGameDate(mGameDate);
                    mGame.setGameTime(mGameTime);
                    mGame.setOpponentName(mOpponentName.getText().toString());
                    mGame.setHomeOrAway(mHomeOrAway);
                    mGame.setFieldLocation(mFieldLocation.getText().toString());
                    mGame.setGameNote(mGameNote.getText().toString());
                    boolean loadSuccessfully = Utils.updateGameToDatabase(mContext, mGame);
                    if (!loadSuccessfully) {
                        Log.e(EditGameActivity.class.getSimpleName(), "Problem Updating Game to Database");
                    }
                    Intent intent = new Intent();
                    intent.putExtra("Game", mGame);
                    setResult(Activity.RESULT_OK, intent);
                    onBackPressed();
                }
            }
        });


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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mGame.setGameTime(mGameTime);
        mGame.setGameDate(mGameDate);
        mGame.setOpponentName(mOpponentName.getText().toString());
        mGame.setHomeOrAway(mHomeOrAway);
        mGame.setFieldLocation(mFieldLocation.getText().toString());
        mGame.setGameNote(mGameNote.getText().toString());
        outState.putParcelable(PARCELABLEKEY, mGame);
        super.onSaveInstanceState(outState);
    }
}
