package com.example.android.coachescorner.ui;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.ui.fragments.GameAddFragment;

public class GameAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_add);

        FragmentManager fragmentManager = getSupportFragmentManager();
        GameAddFragment gameAddFragment = new GameAddFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.game_add_container, gameAddFragment)
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
