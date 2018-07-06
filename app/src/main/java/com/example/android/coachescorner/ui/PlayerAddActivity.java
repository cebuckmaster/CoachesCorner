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

public class PlayerAddActivity extends AppCompatActivity {

    @BindView(R.id.civ_player_pic) CircleImageView mImgPlayerPic;
    @BindView(R.id.btn_add_player_pic) Button mBtnAddPlayerPic;
    @BindView(R.id.et_player_add_first_name) EditText mPlayerFirstNameEditText;
    @BindView(R.id.et_player_add_last_name) EditText mPlayerLastNameEditText;
    @BindView(R.id.et_player_add_number) EditText mPlayerNumberEditText;
    @BindView(R.id.btn_submit_player) Button mBtnSaveButton;

    private static final String PICTUREKEY = "Pic";
    private static final String FIRSTNAMEKEY = "FirstName";
    private static final String LASTNAMEKEY = "LastName";
    private static final String NUMBERKEY = "Number";

    private Context mContext;
    private Player mPlayer = new Player();

    private Bitmap mPlayerPicBitMap;
    private Uri mPicUri;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 107;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_add);
        ButterKnife.bind(this);

        mContext = this;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(PICTUREKEY)) {
                try {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(savedInstanceState.getByteArray(PICTUREKEY));
                    Bitmap storedBitMap = BitmapFactory.decodeStream(inputStream);
                    mImgPlayerPic.setImageBitmap(storedBitMap);
                } catch (Exception e) {
                    Log.e(PlayerAdapter.class.getSimpleName(), "Error trying to load Pic - " + e);
                }
            }
            mPlayerFirstNameEditText.setText(savedInstanceState.getString(FIRSTNAMEKEY));
            mPlayerLastNameEditText.setText(savedInstanceState.getString(LASTNAMEKEY));
            mPlayerNumberEditText.setText(savedInstanceState.getString(NUMBERKEY));
        }

        mBtnAddPlayerPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(Utils.getPickImageChooserIntent(mContext), 200);
            }
        });

        mBtnSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayerFirstNameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(mContext, getString(R.string.must_player_first_name), Toast.LENGTH_SHORT).show();
                } else if (mPlayerLastNameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(mContext, getString(R.string.must_player_last_name), Toast.LENGTH_SHORT).show();
                } else {
                    mPlayer.setPlayerFirstName(mPlayerFirstNameEditText.getText().toString());
                    mPlayer.setPlayerLastName(mPlayerLastNameEditText.getText().toString());
                    mPlayer.setPlayerNumber(mPlayerNumberEditText.getText().toString());
                    try {
                        Bitmap image = ((BitmapDrawable) mImgPlayerPic.getDrawable()).getBitmap();
                        mPlayer.setPlayerPic(Utils.getBitmapAsByteArray(image));
                    } catch (Exception e) {
                        Log.e(PlayerAddActivity.class.getSimpleName(), "Error getting bitmap - " + e);
                    }
                    boolean loadedSuccessfully = Utils.addPlayerToDatabase(mContext, mPlayer);
                    if (!loadedSuccessfully) {
                        Log.e(PlayerAddActivity.class.getSimpleName(), "Problem Adding Player to Database");
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
    protected void onSaveInstanceState(Bundle outState) {
        try {
            Bitmap image = ((BitmapDrawable) mImgPlayerPic.getDrawable()).getBitmap();
            outState.putByteArray(PICTUREKEY, Utils.getBitmapAsByteArray(image));
        } catch (Exception e) {
            Log.e(PlayerAddActivity.class.getSimpleName(), "Error getting bitmap for onSaveInstanceState - " + e);
        }
        outState.putString(FIRSTNAMEKEY, mPlayerFirstNameEditText.getText().toString());
        outState.putString(LASTNAMEKEY, mPlayerLastNameEditText.getText().toString());
        outState.putString(NUMBERKEY, mPlayerNumberEditText.getText().toString());
        super.onSaveInstanceState(outState);
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

                    mImgPlayerPic.setImageBitmap(mPlayerPicBitMap);
                } catch (IOException e) {
                    Log.e(PlayerAddActivity.class.getSimpleName(), "Error getting Pic - " + e);
                }
            } else {
                if (data.hasExtra("data")) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    mPlayerPicBitMap = bitmap;
                    mImgPlayerPic.setImageBitmap(mPlayerPicBitMap);
                }
            }
        }
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
