package com.example.android.coachescorner.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.coachescorner.R;
import com.example.android.coachescorner.adapter.PlayerAdapter;
import com.example.android.coachescorner.common.Utils;
import com.example.android.coachescorner.data.Player;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class PlayerDetailActivity extends AppCompatActivity {

    private static final String PARCELABLEKEY = "Player";
    private String mFirstName;
    private String mLastName;
    private String mNumber;
    private String mNote;

    private Player mPlayer;
    private Context mContext;
    @BindView(R.id.civ_player_edit_pic)CircleImageView mPlayerPic;
    @BindView(R.id.btn_add_player_edit_pic) Button mBtnAddPlayerPic;
    @BindView(R.id.et_player_edit_first_name) EditText mPlayerFirstNameEditText;
    @BindView(R.id.et_player_edit_last_name) EditText mPlayerLastNameEditText;
    @BindView(R.id.et_player_edit_number) EditText mPlayerNumber;
    @BindView(R.id.et_player_edit_notes) EditText mPlayerNotes;
    @BindView(R.id.btn_player_edit_submit) Button mSubmitBtn;

    private static final String PICTUREKEY = "Pic";
    private static final String FIRSTNAMEKEY = "FirstName";
    private static final String LASTNAMEKEY = "LastName";
    private static final String NUMBERKEY = "Number";
    private static final String NOTEKEY = "Note";

    private Bitmap mPlayerPicBitMap;
    private Uri mPicUri;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 107;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);
        ButterKnife.bind(this);

        mContext = this;


        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(PICTUREKEY)) {
                try {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(savedInstanceState.getByteArray(PICTUREKEY));
                    Bitmap storedBitMap = BitmapFactory.decodeStream(inputStream);
                    mPlayerPic.setImageBitmap(storedBitMap);
                } catch (Exception e) {
                    Log.e(PlayerAdapter.class.getSimpleName(), "Error trying to load Pic - " + e);
                }
            }
            mFirstName = savedInstanceState.getString(FIRSTNAMEKEY);
            mPlayerFirstNameEditText.setText(mFirstName);
            mLastName = savedInstanceState.getString(LASTNAMEKEY);
            mPlayerLastNameEditText.setText(mLastName);
            mNumber = savedInstanceState.getString(NUMBERKEY);
            mPlayerNumber.setText(mNumber);
            mNote = savedInstanceState.getString(NOTEKEY);
            mPlayerNotes.setText(mNote);
            mPlayer = new Player();
        } else {
            Intent intentThatStartedThisActivity = getIntent();
            if (intentThatStartedThisActivity.hasExtra("Player")) {
                mPlayer = intentThatStartedThisActivity.getParcelableExtra("Player");
            }
            if (mPlayer.getPlayerPic() != null) {
                try {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(mPlayer.getPlayerPic());
                    Bitmap storedBitMap = BitmapFactory.decodeStream(inputStream);
                    mPlayerPic.setImageBitmap(storedBitMap);
                } catch (Exception e) {
                    Log.e(PlayerDetailActivity.class.getSimpleName(), "Error trying to load Pic - " + e);
                }
            }
            mPlayerFirstNameEditText.setText(mPlayer.getPlayerFirstName());
            mPlayerLastNameEditText.setText(mPlayer.getPlayerLastName());
            mPlayerNumber.setText(String.valueOf(mPlayer.getPlayerNum()));
            mPlayerNotes.setText(mPlayer.getPlayerNote());
        }

        mBtnAddPlayerPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(Utils.getPickImageChooserIntent(mContext), 200);
            }
        });


        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirstName = mPlayerFirstNameEditText.getText().toString();
                mLastName = mPlayerNumber.getText().toString();
                mNumber = mPlayerNumber.getText().toString();
                mNote = mPlayerNotes.getText().toString();
                if (mFirstName.isEmpty()) {
                    Toast.makeText(mContext, getString(R.string.must_player_first_name), Toast.LENGTH_SHORT).show();
                } else if (mLastName.isEmpty()) {
                    Toast.makeText(mContext, getString(R.string.must_player_last_name), Toast.LENGTH_SHORT).show();
                } else {
                    mPlayer.setPlayerFirstName(mFirstName);
                    mPlayer.setPlayerLastName(mLastName);
                    mPlayer.setPlayerNumber(mNumber);
                    mPlayer.setPlayerNote(mNote);
                    try {
                        Bitmap image = ((BitmapDrawable) mPlayerPic.getDrawable()).getBitmap();
                        mPlayer.setPlayerPic(Utils.getBitmapAsByteArray(image));
                    } catch (Exception e) {
                        Log.e(PlayerDetailActivity.class.getSimpleName(), "Error getting bitmap - " + e);
                    }
                    Boolean updatePlayerSuccess = Utils.updatePlayerToDatabase(mContext, mPlayer);
                    if (!updatePlayerSuccess) {
                        Log.e("PlayerEditFragment", "Failed to update player");
                    }
                    onBackPressed();
                }
            }
        });


        permissions.add(CAMERA);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap bitmap;

        if (resultCode == Activity.RESULT_OK) {
            if (Utils.getPickImageResultUri(mContext, data) != null) {
                mPicUri = Utils.getPickImageResultUri(mContext, data);
                try {
                    mPlayerPicBitMap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mPicUri);
                    mPlayerPicBitMap = Utils.rotateImageIfRequired(mPlayerPicBitMap, mPicUri);
                    mPlayerPicBitMap = Utils.getResizedBitmap(mPlayerPicBitMap, 500);
                    mPlayerPic.setImageBitmap(mPlayerPicBitMap);
                } catch (IOException e) {
                    Log.e(PlayerDetailActivity.class.getSimpleName(), "Error getting Pic - " + e);
                }
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                mPlayerPicBitMap = bitmap;
                mPlayerPic.setImageBitmap(mPlayerPicBitMap);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        try {
            Bitmap image = ((BitmapDrawable) mPlayerPic.getDrawable()).getBitmap();
            outState.putByteArray(PICTUREKEY, Utils.getBitmapAsByteArray(image));
        } catch (Exception e) {
            Log.e(PlayerDetailActivity.class.getSimpleName(), "Error getting bitmap for onSaveInstanceState - " + e);
        }
        outState.putString(FIRSTNAMEKEY, mPlayerFirstNameEditText.getText().toString());
        outState.putString(LASTNAMEKEY, mPlayerLastNameEditText.getText().toString());
        outState.putString(NUMBERKEY, mPlayerNumber.getText().toString());
        outState.putString(NOTEKEY, mPlayerNotes.getText().toString());
        super.onSaveInstanceState(outState);
    }


    public void setPlayer(Player player) {
        mPlayer = player;
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok_button), okListener)
                .setNegativeButton(getString(R.string.cancel_button), null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {

                    } else {

                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                //Log.d("API123", "permisionrejected " + permissionsRejected.size());
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
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
