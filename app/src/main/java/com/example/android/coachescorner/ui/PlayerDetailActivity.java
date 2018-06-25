package com.example.android.coachescorner.ui;

import android.app.Fragment;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.data.Player;
import com.example.android.coachescorner.ui.fragments.PlayerEditFragment;

public class PlayerDetailActivity extends AppCompatActivity {

    private Player mPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);

        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity.hasExtra("Player")) {
            mPlayer = (Player) intentThatStartedThisActivity.getParcelableExtra("Player");
        }


        FragmentManager fragmentManager = getSupportFragmentManager();
        PlayerEditFragment playerEditFragment = new PlayerEditFragment();
        playerEditFragment.setPlayer(mPlayer);
        fragmentManager.beginTransaction()
                .add(R.id.player_edit_container, playerEditFragment)
                .commit();


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
