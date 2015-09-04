package com.sadna.app.findmyfriends.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.sadna.app.findmyfriends.MyApplication;
import com.sadna.app.findmyfriends.R;
import com.sadna.app.findmyfriends.entities.UserLocation;
import com.sadna.app.webservice.WebService;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    final int THIRTY_SECONDS_IN_MILLISECONDS = 30000;

    private List<UserLocation> usersLocations = new ArrayList<>();
    private Gson gson = new Gson();
    private ArrayList<Marker> mMarkers = new ArrayList<>();

    private Handler mHandler;
    public static GoogleMap mMap;
    public Bitmap mUserDynamicallyGeneratedPictureResource;

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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

        showUpdatedLocationsOnMapEveryXSeconds(THIRTY_SECONDS_IN_MILLISECONDS);
    }

    @Override
    public void onBackPressed() {
        // stop Handler
        mHandler.removeCallbacksAndMessages(null);
        finish();
    }

    private void showUpdatedLocationsOnMapEveryXSeconds(int millisecondsToDelay) {
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
        for (Marker marker : mMarkers) {
            marker.remove();
        }
        mMarkers.clear();
        map.clear();

        // For each location, generate marker custom image and add marker to the map
        for (UserLocation userLocation : usersLocations) {
            LatLng location = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            String userGeneratedPictureURL = "https://chart.googleapis.com/chart?chst=d_bubble_icon_text_big_withshadow&chld=location|bb|" + userLocation.getUsername() + "|00CCFF|000000";
            downloadDynamicallyGeneratedPicture(userGeneratedPictureURL, Integer.toString(userLocation.getUsername().hashCode()));
            Marker marker = map.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromBitmap(mUserDynamicallyGeneratedPictureResource)));
            mMarkers.add(marker);
        }

        // Sets the optimal zoom of the map to include all users
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 100; // offset from edges of the mMap in pixels
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cameraUpdate);
    }

    private void downloadDynamicallyGeneratedPicture(final String fileUrl, final String name) {
        Thread downloadDynamicallyGeneratedPictureThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = (InputStream) new URL(fileUrl).getContent();
                    Drawable d = Drawable.createFromStream(is, name);
                    mUserDynamicallyGeneratedPictureResource = ((BitmapDrawable) d).getBitmap();
                } catch (Exception exception) {
                    Log.e("MapsActivity", exception.getMessage());
                }
            }
        });

        downloadDynamicallyGeneratedPictureThread.start();
        try {
            downloadDynamicallyGeneratedPictureThread.join();
        } catch (InterruptedException exception) {
            Log.e("MapsActivity", exception.getMessage());
        }
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
                    Log.e("MapsActivity", e.getMessage());
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