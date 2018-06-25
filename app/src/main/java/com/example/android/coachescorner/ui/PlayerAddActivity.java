package com.example.android.coachescorner.ui;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.ui.fragments.PlayerAddFragment;

public class PlayerAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_add);

        FragmentManager fragmentManager = getSupportFragmentManager();
        PlayerAddFragment playerAddFragment = new PlayerAddFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.player_add_container, playerAddFragment)
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
