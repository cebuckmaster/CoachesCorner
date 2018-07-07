package com.example.android.coachescorner.ui;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.android.coachescorner.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class FieldLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String mLocation;
    private LatLng mFieldLocation;
    private CameraPosition mCameraPosition;
    private static final int DEFAULT_ZOOM = 10;

    private static final String KEY_CAMERA_POSITION = "camera_psition";
    private static final String KEY_LOCATION = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (savedInstanceState != null) {
            mFieldLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        } else {
            Intent intentThatStartedThisActivity = getIntent();
            if (intentThatStartedThisActivity.hasExtra("Location")) {
                mLocation = intentThatStartedThisActivity.getStringExtra("Location");
            }
            if (mLocation != null && !mLocation.isEmpty()) {
                if (!getLocationFromAddress(mLocation)) {
                    mFieldLocation = new LatLng(90, 0);
                    Toast.makeText(this, getString(R.string.no_field_found), Toast.LENGTH_SHORT).show();
                }
            }
        }

        mapFragment.getMapAsync(this);


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_LOCATION, mFieldLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);


        if (mFieldLocation != null) {
            mMap.addMarker(new MarkerOptions().position(mFieldLocation).title("Marker for field location"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(fieldLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mFieldLocation, DEFAULT_ZOOM));
        }

    }

    private boolean getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return false;
            }
            Address location = address.get(0);
            mFieldLocation = new LatLng(location.getLatitude(), location.getLongitude());
            return true;
        } catch (Exception e) {
            Log.e(FieldLocationActivity.class.getSimpleName(), "Error getting field location - " + e);
            return false;
        }

    }


}
