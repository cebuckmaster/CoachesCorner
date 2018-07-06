package com.example.android.coachescorner.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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

public class GameAddActivity extends AppCompatActivity {

    private static final String DATEKEY = "Date";
    private static final String TIMEKEY = "Time";
    private static final String OPPONENTNAMEKEY = "OppName";
    private static final String HOMEORAWAYKEY = "HomeOrAway";
    private static final String FIELDKEY = "Field";
    private static final String NOTEKEY = "Note";


    @BindView(R.id.btn_add_game_date)
    Button mAddGameDate;
    @BindView(R.id.btn_add_game_time) Button mAddGameTime;
    @BindView(R.id.et_add_game_opponent_name)
    EditText mOpponentName;
    @BindView(R.id.spr_add_game_home_or_away)
    Spinner mHomeOrAwaySpinner;
    @BindView(R.id.et_add_game_field_location) EditText mFieldLocation;
    @BindView(R.id.et_add_game_note) EditText mGameNote;
    @BindView(R.id.btn_add_game_submit) Button mSubmitBtn;

    private Context mContext;

    private Game mGame = new Game();
    private String mGameDate;
    private String mGameTime;
    private String mOppName;
    private String mFieldInfo;
    private String mNote;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private String mHomeOrAway;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_add);

        ButterKnife.bind(this);

        mContext = this;

        String[] homeAwayArray = getResources().getStringArray(R.array.home_or_away);
        final String[] reminderTimeArray = getResources().getStringArray(R.array.reminder_time);

        if (savedInstanceState != null) {
            mGameDate = savedInstanceState.getString(DATEKEY);
            mGameTime = savedInstanceState.getString(TIMEKEY);
            mOppName = savedInstanceState.getString(OPPONENTNAMEKEY);
            mHomeOrAway = savedInstanceState.getString(HOMEORAWAYKEY);
            mFieldInfo = savedInstanceState.getString(FIELDKEY);
            mNote = savedInstanceState.getString(NOTEKEY);
            mAddGameDate.setText(mGameDate);
            mAddGameTime.setText(mGameTime);
            mOpponentName.setText(mOppName);
            switch (mHomeOrAway) {
                case "Away":
                    mHomeOrAwaySpinner.setSelection(1);
                    break;
                default:
                    mHomeOrAwaySpinner.setSelection(0);
                    break;
            }
            mFieldLocation.setText(mFieldInfo);
            mGameNote.setText(mNote);
        } else {
            mAddGameDate.setText(getString(R.string.game_date));
            mAddGameTime.setText(getString(R.string.game_time));
            mHomeOrAway = homeAwayArray[0];
        }


        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);


        mAddGameDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mGameDate = ((monthOfYear + 1) +"/" + dayOfMonth + "/" + year);
                        mAddGameDate.setText(mGameDate);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        mAddGameTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String am_pm = "";

                        Calendar dateTime = Calendar.getInstance();
                        dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateTime.set(Calendar.MINUTE, minute);

                        if (dateTime.get(Calendar.AM_PM)  == Calendar.AM) {
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
                        mAddGameTime.setText(mGameTime);
                    }
                },mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

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
                    boolean loadSuccessfully = Utils.addGameToDatabase(mContext, mGame);
                    if (!loadSuccessfully) {
                        Log.e(GameAddActivity.class.getSimpleName(), "Problem Adding Game to Database");
                    }
                    onBackPressed();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(DATEKEY, mGameDate);
        outState.putString(TIMEKEY, mGameTime);
        outState.putString(OPPONENTNAMEKEY, mOpponentName.getText().toString());
        outState.putString(HOMEORAWAYKEY, mHomeOrAway);
        outState.putString(FIELDKEY, mFieldLocation.getText().toString());
        outState.putString(NOTEKEY, mGameNote.getText().toString());
        super.onSaveInstanceState(outState);
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
