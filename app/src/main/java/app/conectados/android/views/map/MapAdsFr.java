package app.conectados.android.views.map;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.utils.AppConstants;
import app.conectados.android.BuildConfig;
import app.conectados.android.views.MainActivity;
import app.conectados.android.R;
import app.conectados.android.data.network.Restfull;
import app.conectados.android.models.Ad;
import app.conectados.android.models.Category;
import app.conectados.android.models.Provider;
import app.conectados.android.models.SubCategory;
import app.conectados.android.utils.GpsUtils;
import app.conectados.android.utils.MapUtil;
import app.conectados.android.utils.ViewUtils;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import app.conectados.android.views.ad.AdsNearbyActivity;
import app.conectados.android.custom.dialogs.AdsProviderDialog;
import app.conectados.android.custom.dialogs.CategoryDialogRadio;
import app.conectados.android.custom.dialogs.GeolitoDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapAdsFr extends Fragment implements  OnMapReadyCallback, OnMarkerClickListener{

    private final String TAG = this.getClass().getSimpleName();
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 101;
    private final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 102;
    private final int WRITE_EXTERNAL_STORAGE = 103;

    private GoogleMap googleMap;
    private MainActivity mActivity;
    private Restfull restfull;
    private PreferenceHelper preferenceHelper;

    private LocationManager locationManager;
    private Location lastLocation;
    private Category categorySelected;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;

    @BindView(R.id.map) MapView mapView;
    @BindView(R.id.btnSelectCategory) ImageView btnSelectCategory;
    @BindView(R.id.txtSearch) TextView txtSearch;
    @BindView(R.id.imgBuscar) ImageView imgBuscar;
    @BindView(R.id.layoutGeolito) LinearLayout layoutGeolito;
    @BindView(R.id.cerrar_geolito) ImageView cerrar_geolito;
    @BindView(R.id.geolito) ImageView geolito;
    @BindView(R.id.lblNomCategoria) TextView lblNomCategoria;
    @BindView(R.id.pnlGpsMessage) LinearLayout pnlGpsMessage;
    @BindView(R.id.pinUbicacion) ImageView pinUbicacion;
    @BindView(R.id.activateGps) Button activateGps;
    @BindView(R.id.mostrarLista) Button mostrarLista;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity){
            mActivity = (MainActivity) context;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        this.googleMap.setOnMarkerClickListener(this);
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.googleMap.getUiSettings().setZoomControlsEnabled(false);
        this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mActivity, R.raw.style_json));

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permAccessLoc = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permAccessLoc == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permiso de acceso a ubicacion concedido");
                activateGoogleMapLocationFunctions();
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
             }else{
                Log.d(TAG, "No tiene permiso de acceso a ubicacion");
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
            Log.d(TAG, "No es necesario pedir permiso");
            activateGoogleMapLocationFunctions();
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }

        int topSize = ViewUtils.dpToPx(mActivity, 90);
        MapUtil.relocateCenterMapButton(mapView, topSize);
    }

    public void activateGoogleMapLocationFunctions() throws SecurityException{
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setOnCameraIdleListener(new OnCameraIdleListener());
        this.locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);
        ButterKnife.bind(this, view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.w(TAG, "loop location ......");
                    if (location != null) {
                        Log.d(TAG, "loop location winnnn");
                        lastLocation = location;
                        pnlGpsMessage.setVisibility(View.GONE);
                        centerMapWithLocation(location);
                       // getProveedoresPorUbicacionCategoriaYPalabra(location, categorySelected, txtSearch.getText().toString());
                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        restfull = retrofit.create(Restfull.class);
        lblNomCategoria.setText("\u0020\u0020".concat(getString(R.string.categoria)));

        preferenceHelper = new PreferenceHelper(mActivity);
        cargarCategorias();

        imgBuscar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtSearch.getText().toString().trim().equals("")){
                    ViewUtils.message(mActivity, "Ingresa una palabra sobre lo que estás buscando...");
                }else{
                    if(categorySelected != null){
                        getProveedoresPorUbicacionCategoriaYPalabra(lastLocation, categorySelected, txtSearch.getText().toString());
                    }else{
                        getProveedoresPorUbicacionCategoriaYPalabra(lastLocation, null, txtSearch.getText().toString());
                    }
                }
            }
        });

        cerrar_geolito.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutGeolito.setVisibility(View.GONE);
            }
        });

        geolito.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                GeolitoDialog alert = new GeolitoDialog();
                alert.setTitle(getString(R.string.seleccione_categoria));
                alert.setAlertPositiveListener(new GeolitoDialog.AlertPositiveListener() {
                    @Override
                    public void onPositiveClick(Category category) {

                    }
                });
                Bundle b  = new Bundle();
                alert.setArguments(b);
                alert.show(manager, "alert_dialog_geolito");
            }
        });

        mostrarLista.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastLocation != null){
                    Intent intent = new Intent(mActivity, AdsNearbyActivity.class);
                    intent.putExtra("lastLocation", lastLocation);
                    startActivity(intent);
                }else{
                    ViewUtils.message(mActivity, mActivity.getString(R.string.location_null));
                }
            }
        });

        activateGps.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new GpsUtils(mActivity).turnGPSOn(new GpsUtils.onGpsListener() {
                    @Override
                    public void gpsStatus(boolean isGPSEnable) {
                        // turn on GPS
                        isGPS = isGPSEnable;
                        if(!isGPSEnable){
                            pnlGpsMessage.setVisibility(View.VISIBLE);
                        }else{
                            pnlGpsMessage.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mapView.onResume();
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.e(TAG, "Error: "+e);
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        final ProgressDialog progressDialog = ProgressDialog.show( mActivity, null, null, false, true );
        progressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        progressDialog.setContentView( R.layout.progress_bar );

        final ArrayList<Ad> lstPedidos = new ArrayList<>();
        String providerId =  marker.getTag().toString();

        Map<String, String> map = new HashMap<>();
        map.put("op", "get-ads-by-provider-id");
        map.put("provider_id", providerId);
        map.put("lat", ""+lastLocation.getLatitude());
        map.put("lon", ""+lastLocation.getLongitude());

        if(categorySelected != null){
            map.put("cat_id", String.valueOf(categorySelected.id));

        }else{
            map.put("cat_id", "missing");
        }

        map.put("word_search", txtSearch.getText().toString());

        restfull.getAnunciosConDatosProveedor(preferenceHelper.getToken(), map).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(@NonNull Call<JsonObject> call,
                                   @NonNull Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        JsonObject tdata = response.body();

                        JsonObject jprovider = tdata.get("provider").getAsJsonObject();
                        Provider p = new Provider();
                        p.id = jprovider.get("id").getAsInt();
                        p.firstname = jprovider.get("firstname").getAsString();
                        p.surname = jprovider.get("surname").getAsString();
                        p.rating = jprovider.get("rating").getAsString();

                        JsonArray adata = tdata.get("items-list").getAsJsonArray();
                        for(JsonElement e: adata){
                            JsonObject odata = e.getAsJsonObject();

                            Ad se = new Ad();
                            se.id = odata.get("id").getAsInt();
                            se.name = odata.get("name").getAsString();
                            se.description = odata.get("description").getAsString();
                            se.price = odata.get("price").getAsString();

                            JsonObject jdata = odata.get("category").getAsJsonObject();
                            se.categoryId = jdata.get("id").getAsInt();
                            se.mipmapid = jdata.get("mipmapid").getAsString();
                            lstPedidos.add(se);
                        }

                        Bundle b = new Bundle();
                        b.putParcelable("provider", p);
                        b.putParcelableArrayList("listaAds", lstPedidos);

                        FragmentManager manager = getFragmentManager();
                        AdsProviderDialog dialog = new AdsProviderDialog();
                        dialog.setArguments(b);
                        dialog.show(manager, "alert_dialog_list");

                    }else {
                        Log.e(TAG, "body es null");
                    }

                } else {
                    Log.e(TAG, "Error : "+response.code());
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                Crashlytics.logException(t);
            }

        });

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult : acepto permisos de localizacion");
                    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if(lastLocation != null){
                            activateGoogleMapLocationFunctions();
                            centerMapWithLocation(lastLocation);
                        }
                    }
                } else {
                    Log.w(TAG, "onRequestPermissionsResult : No acepto permisos de localizacion");
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permiso de telefono aceptado ...");
                } else {
                    Log.w(TAG, "No acepto permisos de telefono");
                }
            }

            case WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permiso de telefono aceptado ...");
                } else {
                    Log.w(TAG, "No acepto permisos de telefono");
                }
            }

        }
    }

    private Location getLastKnownLocation() {
        try {
            locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
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

    private class OnCameraIdleListener implements GoogleMap.OnCameraIdleListener {

        @Override
        public void onCameraIdle() {
            try {
                LatLng latLngOrigen = googleMap.getCameraPosition().target;
                Log.d(TAG, "nueva posicion :: ["+ latLngOrigen.latitude + ", "+ latLngOrigen.longitude+"]");
                if(lastLocation != null){
                    lastLocation.setLatitude(latLngOrigen.latitude);
                    lastLocation.setLongitude(latLngOrigen.longitude);
                    getProveedoresPorUbicacionCategoriaYPalabra(lastLocation, categorySelected, txtSearch.getText().toString());
                }

            } catch (Exception e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
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
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mapView.onDestroy();
            if (mActivity != null) {
                Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                if (fragment != null) {
                    ft.remove(fragment);
                    ft.commit();
                }
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
                pnlGpsMessage.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void cargarCategorias(){

        btnSelectCategory.setOnClickListener( new OnClickListener() {

            @Override
            public void onClick(View v) {

            restfull.getCategories(preferenceHelper.getToken()).enqueue(new Callback<JsonArray>() {

                @Override
                public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {

                    JsonArray categories = response.body();
                    List<Category> listaCategories = parsearListaCategorias(categories);

                    FragmentManager manager = getFragmentManager();
                    CategoryDialogRadio alert = new CategoryDialogRadio();
                    alert.setTitle(getString(R.string.seleccione_categoria));
                    alert.setListaCategories(listaCategories);
                    alert.setAlertPositiveListener(new CategoryDialogRadio.AlertPositiveListener() {

                        @Override
                        public void onPositiveClick(Category category) {
                            categorySelected = category;
                            lblNomCategoria.setText("\u0020\u0020".concat(category.name));

                            int mipmapid = mActivity.getResources().getIdentifier("top_"+category.mipmapName, "mipmap", mActivity.getPackageName());
                            Drawable dd = ContextCompat.getDrawable(mActivity, mipmapid);

                            lblNomCategoria.setCompoundDrawablesWithIntrinsicBounds(dd, null, null, null);
                            getProveedoresPorUbicacionCategoriaYPalabra(lastLocation, categorySelected, txtSearch.getText().toString());
                        }
                    });

                    Bundle b  = new Bundle();
                    Log.d(TAG, "categorySelected >> "+categorySelected);
                    b.putParcelable("categorySelected", categorySelected);
                    alert.setArguments(b);
                    alert.show(manager, "alert_dialog_radio");
                }

                @Override
                public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) { t.printStackTrace();}
            });

            }}
        );

    }

    private List<Category> parsearListaCategorias(JsonArray categories){
        List<Category> listaCategories = new ArrayList<>();
        for (JsonElement e : categories){
            JsonObject jObject = e.getAsJsonObject();
            Category c = new Category();
            c.id = jObject.get("id").getAsInt();
            c.name = jObject.get("name").getAsString();
            c.mipmapName = jObject.get("mipmapid").getAsString();

            if(jObject.has("sub_categories")){
                JsonArray jArray = jObject.get("sub_categories").getAsJsonArray();
                if(jArray.size() > 0){
                    List<SubCategory> subcategorias = new ArrayList<>();
                    for (JsonElement sc : jArray){
                        JsonObject o = sc.getAsJsonObject();
                        SubCategory scat = new SubCategory();
                        scat.id = Integer.parseInt(o.get("id").getAsString());
                        scat.name = o.get("name").getAsString();
                        subcategorias.add(scat);
                    }
                    c.subCategorias = subcategorias;
                }
            }

            listaCategories.add(c);
        }

        return listaCategories;
    }

    public void getProveedoresPorUbicacionCategoriaYPalabra(Location loc, Category category, String word_search){
        googleMap.clear();
        final int mipmapid;
        final ProgressDialog progressDialog = ProgressDialog.show( mActivity, null, null, false, true );
        progressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        progressDialog.setContentView( R.layout.progress_bar );

        HashMap<String, String> map = new HashMap<>();
        map.put("op", "get-providers-by-cat-and-word");
        map.put("lat", ""+loc.getLatitude());
        map.put("lon", ""+loc.getLongitude());

        if(category != null){
            map.put("cat_id", String.valueOf(category.id));
            mipmapid = mActivity.getResources().getIdentifier("marker_"+category.mipmapName, "mipmap", mActivity.getPackageName());
            Log.d(TAG, "mipmapid >>> "+"marker_"+category.mipmapName);
        }else{
            map.put("cat_id", "missing");
            mipmapid = R.mipmap.marker_default;
        }

        Log.d(TAG, "mipmapid >>> "+mipmapid);

        map.put("word_search", word_search);
        restfull.getProveedores(preferenceHelper.getToken(), map).enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                JsonArray proveedores = response.body();
                Log.d(TAG, "Cantidad de proveedores : "+proveedores.size());

                if(proveedores.size() == 0 ){
                    ViewUtils.message(mActivity, "No se encontró proveedores para tu búsqueda");
                }else if (proveedores.size() == 1){
                    ViewUtils.message(mActivity, "Se encontró "+proveedores.size()+" proveedor de anuncios");
                }else if (proveedores.size() > 1){
                    ViewUtils.message(mActivity, "Se encontraron "+proveedores.size()+" proveedores de anuncios");
                }

                for(JsonElement item: proveedores){
                    JsonObject pro = item.getAsJsonObject();
                    String providerId = pro.get("id").getAsString();
                    JsonArray adsLocs = pro.getAsJsonArray("locs");
                    LatLng loc = new LatLng(adsLocs.get(0).getAsDouble(),adsLocs.get(1).getAsDouble());

                    googleMap.addMarker(new MarkerOptions()
                            .position(loc)
                            .icon(BitmapDescriptorFactory.fromResource(mipmapid))
                            .title("")).setTag(providerId);
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Crashlytics.logException(t);
                t.printStackTrace();
                ViewUtils.message(mActivity, getResources().getString(R.string.server_problem));
            }

        });
    }

}