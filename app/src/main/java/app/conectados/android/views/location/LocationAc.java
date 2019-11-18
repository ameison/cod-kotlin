package app.conectados.android.views.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import app.conectados.android.utils.MapUtil;
import java.util.List;
import app.conectados.android.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationAc extends AppCompatActivity implements LocationListener, OnMapReadyCallback  {

    private String TAG =  LocationAc.class.getSimpleName();
    private String locationProvider = "";
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 101;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private Location lastLocation;

    private SupportMapFragment mapFragment;

    @BindView(R.id.btnAddReference) Button btnAddReference;
    @BindView(R.id.lblCoordenates) TextView lblCoordenates;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapper);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getText(R.string.update_my_loc));


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnAddReference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("lat", lastLocation.getLatitude());
                data.putExtra("lng", lastLocation.getLongitude());
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Ingreso a MapReady");
        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.googleMap.getUiSettings().setZoomControlsEnabled(false);
        this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permAccessLoc = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permAccessLoc == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "API "+Build.VERSION.SDK_INT + " : Permiso ubicacion concedido.");
                lastLocation = getLastKnownLocation();
                Log.d(TAG, "API "+Build.VERSION.SDK_INT + " : LastLocation "+lastLocation);
                if(lastLocation!=null){
                    activateLocationFunctions();
                    centerMapWithLocation(lastLocation);
                }
            }else{
                Log.d(TAG, "No tiene permiso de acceso a ubicacion");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
            Log.d(TAG, "No es necesario pedir permiso");
            lastLocation = getLastKnownLocation();
            activateLocationFunctions();
            centerMapWithLocation(lastLocation);
        }
        MapUtil.relocateCenterMapButton(mapFragment.getView());

    }

    public void activateLocationFunctions() throws SecurityException{
        this.googleMap.setMyLocationEnabled(true);
        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        this.locationManager.requestLocationUpdates(this.getBestProviderName(), 0, 0, this, Looper.getMainLooper());
        this.googleMap.setOnCameraIdleListener(new OnCameraChange());
    }

    private Location getLastKnownLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
            return bestLocation;
        } catch (SecurityException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
            return null;
        } catch (Exception ex){
            Crashlytics.logException(ex);
            ex.printStackTrace();
            return null;
        }
    }

    private void centerMapWithLocation(Location loc) {
        if(loc!=null){
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            CameraPosition camPos = new CameraPosition.Builder().target(latLng).zoom(16).build();
            CameraUpdate newPosition = CameraUpdateFactory.newCameraPosition(camPos);
            googleMap.moveCamera(newPosition);
        }else{
            Log.w(TAG,"centerMapWithLocation > Location is null");
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        Log.i(TAG, "onLocationChanged");
        centerMapWithLocation(loc);

        lastLocation = loc;
        lastLocation.setLatitude(loc.getLatitude());
        lastLocation.setLongitude(loc.getLongitude());
        centerMapWithLocation(loc);
        locationManager.removeUpdates(this);


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "Proveedor "+provider+" cambio status > "+status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "El proveedor fue habilitado : "+provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "El proveedor fue des-habilitado : "+provider);
    }

    private class OnCameraChange implements GoogleMap.OnCameraIdleListener {

        @Override
        public void onCameraIdle() {
            try {
                LatLng latLngOrigen = googleMap.getCameraPosition().target;
                String newLocation = String.valueOf(latLngOrigen.latitude).substring(0, 10) ;
                newLocation = newLocation  + ", " + String.valueOf(latLngOrigen.longitude).substring(0, 10);
                lblCoordenates.setText(newLocation);
                lastLocation.setLatitude(latLngOrigen.latitude);
                lastLocation.setLongitude(latLngOrigen.longitude);

            } catch (Exception e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
    }

    private String getBestProviderName() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        locationProvider = locationManager.getBestProvider(criteria, true);
        Log.d(TAG, "LocationProvider : "+locationProvider);
        return locationProvider;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}