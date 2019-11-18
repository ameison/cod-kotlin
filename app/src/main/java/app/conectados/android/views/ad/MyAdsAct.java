package app.conectados.android.views.ad;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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
import app.conectados.android.models.Ad;
import app.conectados.android.models.Provider;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.utils.ViewUtils;
import app.conectados.android.custom.adapters.AdAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyAdsAct extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private String TAG = this.getClass().getName();
    private List<Ad> lstPedidos ;
    @BindView(R.id.lvMyAds) ListView lvMyAds;
    @BindView(R.id.messageNoneAd) TextView messageNoneAd;
    @BindView(R.id.srlMyAds) SwipeRefreshLayout srlMyAds;


    private PreferenceHelper preferenceHelper;
    private Restfull restfull;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myadds);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getText(R.string.op_misanuncios));
    }

    @Override
    protected void onStart() {
        super.onStart();
        srlMyAds.setOnRefreshListener(this);
        srlMyAds.setColorSchemeResources(R.color.cns_celeste, R.color.cns_rojo);
        preferenceHelper = new PreferenceHelper(this);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        restfull = retrofit.create(Restfull.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMisAnuncios();
    }

    private void getMisAnuncios(){

        final ProgressDialog progressDialog = ProgressDialog.show( MyAdsAct.this, null, null, false, true );
        progressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        progressDialog.setContentView( R.layout.progress_bar );

        lvMyAds.setAdapter(null);
        lstPedidos = new ArrayList<>();

        Map<String, String> map = new HashMap<>();
        map.put("op", "get-ads-by-provider-id");
        map.put("cat_id", "missing");
        map.put("provider_id", preferenceHelper.getUserId());
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

                        if(lstPedidos.size() > 0){
                            AdAdapter adapter = new AdAdapter(MyAdsAct.this, R.layout.item_ad, lstPedidos);
                            adapter.notifyDataSetChanged();
                            lvMyAds.setAdapter(adapter);
                        } else {
                            messageNoneAd.setVisibility(View.VISIBLE);
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
            public void onFailure(@NonNull Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                Crashlytics.logException(t);
            }

        });

    }

    @Override
    public void onRefresh() {
        srlMyAds.post(new Runnable() {
            @Override
            public void run() {
                getMisAnuncios();
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
