package com.example.android.coachescorner.ui;

import android.app.Fragment;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.data.Game;
import com.example.android.coachescorner.ui.fragments.GameEditFragment;

public class EditGameActivity extends AppCompatActivity {

    private Game mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_game);

        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity.hasExtra("Game")) {
            mGame = (Game) intentThatStartedThisActivity.getParcelableExtra("Game");
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        GameEditFragment gameEditFragment = new GameEditFragment();
        gameEditFragment.setGame(mGame);
        fragmentManager.beginTransaction()
                .add(R.id.game_edit_container, gameEditFragment)
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
