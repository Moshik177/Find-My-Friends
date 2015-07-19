package com.sadna.app.findmyfriends.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.ui.IconGenerator;
import com.sadna.app.findmyfriends.MyApplication;
import com.sadna.app.findmyfriends.R;
import com.sadna.app.findmyfriends.entities.UserLocation;
import com.sadna.app.webservice.WebService;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    final int THIRTY_SECONDS_IN_MILLISECONDS = 30000;

    private List<UserLocation> usersLocations = new ArrayList<>();
    private Gson gson = new Gson();
    private ArrayList<Marker> mMarkers = new ArrayList<>();
    private IconGenerator mIconGenerator;

    private Handler mHandler;
    public static GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mIconGenerator = new IconGenerator(getApplicationContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.mMap = map;

        // Map settings
        map.setBuildingsEnabled(true);
        map.setIndoorEnabled(true);
        map.setTrafficEnabled(true);

        // UI Settings
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setIndoorLevelPickerEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        // TODO: Make each marker to have the text shown
        updateLocationsEveryXSeconds(THIRTY_SECONDS_IN_MILLISECONDS);
    }

    @Override
    public void onBackPressed() {
        // stop Handler
        mHandler.removeCallbacksAndMessages(null);
        finish();
    }

    private void updateLocationsEveryXSeconds(int millisecondsToDelay) {
        mHandler = new Handler();
        final int delay = millisecondsToDelay; // milliseconds

        mHandler.postDelayed(new Runnable() {
            public void run() {
                showLocationsOnMap(MapsActivity.mMap);
                mHandler.postDelayed(this, delay);
            }
        }, 0);
    }

    private void showLocationsOnMap(GoogleMap map) {
        getGroupMembersLocations();

        // Clears any past markers
        for (Marker marker: mMarkers) {
            marker.remove();
        }
        mMarkers.clear();
        map.clear();

        for (UserLocation userLocation: usersLocations) {
            mIconGenerator.setStyle(IconGenerator.STYLE_BLUE);
            Bitmap iconBitmap = mIconGenerator.makeIcon(userLocation.getUsername());
            // TODO: Add pin drawable to make it more beautiful
            LatLng location = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            Marker marker = map.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)).anchor(mIconGenerator.getAnchorU(), mIconGenerator.getAnchorV()));
            marker.showInfoWindow();
            mMarkers.add(marker);
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 100; // offset from edges of the mMap in pixels
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cameraUpdate);
    }

    private void getGroupMembersLocations() {
        Thread getGroupMembersLocationsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WebService wsHttpRequest = new WebService("getGroupMembersLocations");
                    String result = null;

                    try {
                        result = wsHttpRequest.execute(((MyApplication) getApplication()).getSelectedGroupId());
                    } catch (Throwable exception) {
                        Log.e("MapsActivity", exception.getMessage());
                    }

                    usersLocations = gson.fromJson(result, new TypeToken<ArrayList<UserLocation>>() {
                    }.getType());
                } catch (Exception e) {
                    Log.e("GroupsMainActivity", e.getMessage());
                }
            }
        });

        getGroupMembersLocationsThread.start();
        try {
            getGroupMembersLocationsThread.join();
        } catch (InterruptedException exception) {
            Log.e("MapsActivity", exception.getMessage());
        }
    }
}