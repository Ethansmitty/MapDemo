package com.example.ethan.mapdemo;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import static com.google.android.gms.maps.GoogleMap.*;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    private static final int MY_PERMISSION_FINE_LOCATION = 101;
    private static final int LOCATION_SETTINGS_REQUEST = 102;

    private Spinner mSpinner;
    private CheckBox ulsterMain, hBuilding, bBuilding, aBuilding, vBuilding, dBuilding, sBuilding;

    private Marker ulsterMarker, hMarker, bMarker, aMarker, vMarker, dMarker, sMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ulsterMain = findViewById(R.id.ulsterMain);
        hBuilding = findViewById(R.id.hBuilding);
        bBuilding = findViewById(R.id.bBuilding);
        aBuilding = findViewById(R.id.aBuilding);
        vBuilding = findViewById(R.id.vBuilding);
        dBuilding = findViewById(R.id.dBuilding);
        sBuilding = findViewById(R.id.sBuilding);

        mSpinner = (Spinner) findViewById(R.id.layers_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.layers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker at SUNY Ulster
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerDragListener(this);

        LatLngBounds ULSTER = new LatLngBounds(new LatLng(41.849423, -74.133734), new LatLng(41.853426, -74.126084));
        LatLng ULSTERCENTER = new LatLng(41.851467, -74.129027);

        // Add markers for buildings
        setUpMarkers();

        mMap.setLatLngBoundsForCameraTarget(ULSTER);

        //MyLocation layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            displayLocationSettingsRequest(this);
            Toast.makeText(this, "User location enabled", Toast.LENGTH_LONG).show();
        } else {
            //Prompt user for permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);

        }
    }

    private void setUpMarkers()
    {
        ulsterMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.851467, -74.129027))
                .title("SUNY Ulster")
                .snippet("491 Cottekill Rd, Stone Ridge, NY 12484")
                .draggable(true)
                .visible(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        hMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.8521, -74.1278))
                .title("Hardenbergh Building")
                .visible(false)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        bMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.85126, -74.12745))
                .title("Burroughs Hall")
                .visible(false)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        aMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.85074, -74.128028))
                .title("Algonquin")
                .visible(false)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        vMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.850867, -74.12979))
                .title("Vanderlyn Hall")
                .visible(false)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        dMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.85132, -74.128813))
                .title("Dewitt Library")
                .visible(false)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        sMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.85078, -74.1308))
                .title("Senate Gymnasium")
                .visible(false)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Redundant permission check
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        displayLocationSettingsRequest(this);
                        Toast.makeText(this, "User location enabled", Toast.LENGTH_LONG).show();
                        //TODO Make this work on first run
                    }
                } else {
                    //If no permission, just don't use the marker
                    Toast.makeText(this, "User location disabled", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void displayLocationSettingsRequest(Context context)
    {
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

        final LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        settingsBuilder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(settingsBuilder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response =
                            task.getResult(ApiException.class);
                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                resolvableApiException
                                        .startResolutionForResult(MapActivity.this, LOCATION_SETTINGS_REQUEST);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Toast.makeText(this, marker.getPosition().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Toast.makeText(this, marker.getPosition().toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // No toast because this can also be called by the Android framework in onResume() at which
        // point mMap may not be ready yet.
        if (mMap == null) {
            return;
        }

        String layerName = ((String) mSpinner.getSelectedItem());
        if (layerName.equals(getString(R.string.normal))) {
            mMap.setMapType(MAP_TYPE_NORMAL);
        } else if (layerName.equals(getString(R.string.hybrid))) {
            mMap.setMapType(MAP_TYPE_HYBRID);


        } else if (layerName.equals(getString(R.string.satellite))) {
            mMap.setMapType(MAP_TYPE_SATELLITE);
        } else if (layerName.equals(getString(R.string.terrain))) {
            mMap.setMapType(MAP_TYPE_TERRAIN);
        } else {
            Log.i("LDA", "Error setting layer with name " + layerName);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void ulsterMainToggled(View view) {
        if (checkReady()) {
            ulsterMarker.setVisible(ulsterMain.isChecked());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ulsterMarker.getPosition()));
        }
    }


    public void hBuildingToggled(View view) {
        if (checkReady()) {
            hMarker.setVisible(hBuilding.isChecked());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(hMarker.getPosition()));
        }
    }

    public void bBuildingToggled(View view) {
        if (checkReady()) {
            bMarker.setVisible(bBuilding.isChecked());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(bMarker.getPosition()));
        }
    }

    public void aBuildingToggled(View view) {
        if (checkReady()) {
            aMarker.setVisible(aBuilding.isChecked());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(aMarker.getPosition()));
        }
    }

    public void vBuildingToggled(View view) {
        if (checkReady()) {
            vMarker.setVisible(vBuilding.isChecked());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(vMarker.getPosition()));
        }
    }

    public void dBuildingToggled(View view) {
        if (checkReady()) {
            dMarker.setVisible(dBuilding.isChecked());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(dMarker.getPosition()));
        }
    }

    public void sBuildingToggled(View view) {
        if (checkReady()) {
            sMarker.setVisible(sBuilding.isChecked());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sMarker.getPosition()));
        }
    }
}