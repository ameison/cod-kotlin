package app.conectados.android.views.ad;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import app.conectados.android.BuildConfig;
import app.conectados.android.R;
import app.conectados.android.data.network.Restfull;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.models.Ad;
import app.conectados.android.utils.ViewUtils;
import app.conectados.android.custom.adapters.AdAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdsNearbyActivity extends AppCompatActivity {

private String TAG = AdsNearbyActivity.class.getSimpleName();

    private Location location;
    private List<Ad> lstPedidos ;
    @BindView(R.id.lvMyAds) ListView lvMyAds;
    @BindView(R.id.srlMyAds) SwipeRefreshLayout srlMyAds;
    @BindView(R.id.textoBusqueda) EditText textoBusqueda;
    @BindView(R.id.imgBuscar) ImageView imgBuscar;
    private Restfull restfull;
    private PreferenceHelper preferenceHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        ButterKnife.bind(this);

        preferenceHelper = new PreferenceHelper(this);

        // -- Toolbar
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setLogo(null);
        getSupportActionBar().setTitle(getString(R.string.ads_list));
        // -- Fin toolbar

        if(getIntent().getExtras()!=null){
            Bundle extras = getIntent().getExtras();
            if(extras != null){
                location = extras.getParcelable("lastLocation");
             }

        }

        imgBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAnunciosCercanos(location);
            }
        });

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        restfull = retrofit.create(Restfull.class);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getAnunciosCercanos(location);
    }

    private void getAnunciosCercanos(Location location){

        final ProgressDialog progressDialog = ProgressDialog.show( this, null, null, false, true );
        progressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        progressDialog.setContentView( R.layout.progress_bar );

        lvMyAds.setAdapter(null);
        lstPedidos = new ArrayList<>();

        Map<String, String> map = new HashMap<>();
        map.put("op", "get-ads-nearby-provider");
        map.put("lat", String.valueOf(location.getLatitude()));
        map.put("lng", String.valueOf(location.getLongitude()));
        map.put("textoBusqueda", textoBusqueda.getText().toString().trim());
        restfull.getAnuncios(preferenceHelper.getToken(), map).enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        for(JsonElement e: response.body()){
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

                        if(lstPedidos.size() > 0){
                            AdAdapter adapter = new AdAdapter(AdsNearbyActivity.this, R.layout.item_ad, lstPedidos);
                            adapter.notifyDataSetChanged();
                            lvMyAds.setAdapter(adapter);
                        } else {
                            lvMyAds.setAdapter(null);
                        }
                        srlMyAds.setRefreshing(false);
                    }else {
                        Log.e(TAG, "body es null");
                    }

                } else {
                    Log.e(TAG, "Error : "+response.code());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, Throwable t) {
                t.printStackTrace();
                Crashlytics.logException(t);
                progressDialog.dismiss();
                srlMyAds.setRefreshing(false);
                lvMyAds.setAdapter(null);
            }

        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ViewUtils.hideKeyboard(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}