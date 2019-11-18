package app.conectados.android.views.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import app.conectados.android.R;
import app.conectados.android.utils.MapUtil;
import butterknife.BindView;
import butterknife.ButterKnife;


public class LocationAdActivity extends AppCompatActivity implements OnMapReadyCallback, GpsStatus.Listener {

    private String TAG =  LocationAdActivity.class.getSimpleName();
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 101;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private Location lastLocation = new Location("dummyprovider");

    private SupportMapFragment mapFragment;

    @BindView(R.id.lblCoordenates) TextView lblCoordenates;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_ad);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getText(R.string.ubicacion_anuncio));

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            lastLocation.setLatitude(extras.getDouble("lat"));
            lastLocation.setLongitude(extras.getDouble("lng"));
            Log.d(TAG, "lastLocation ::: "+lastLocation);
        }

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
            activateLocationFunctions();
            centerMapWithLocation(lastLocation);
        }
        MapUtil.relocateCenterMapButton(mapFragment.getView());

        String newLocation = String.valueOf(lastLocation.getLatitude()).substring(0, 10) ;
        newLocation = newLocation  + ", " + String.valueOf(lastLocation.getLongitude()).substring(0, 10);
        lblCoordenates.setText(newLocation);

    }

    public void activateLocationFunctions() throws SecurityException{
        this.googleMap.setMyLocationEnabled(true);
        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        this.locationManager.addGpsStatusListener(this);
    }

    @Override
    public void onGpsStatusChanged(int event) {
        switch(event) {
            case GpsStatus.GPS_EVENT_STARTED:
                Log.d("onGpsStatusChanged", "GPS_EVENT_STARTED");
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                Log.d("onGpsStatusChanged", "GPS_EVENT_STOPPED");
                break;
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