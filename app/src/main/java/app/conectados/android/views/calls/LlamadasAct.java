package app.conectados.android.views.calls;

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
import app.conectados.android.models.Llamada;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.utils.ViewUtils;
import app.conectados.android.custom.adapters.LlamadaItemAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LlamadasAct extends AppCompatActivity {

    private String TAG = this.getClass().getName();
    private List<Llamada> lstLlamadas;
    @BindView(R.id.lvLlamadas) ListView lvLlamadas;
    @BindView(R.id.messageDefault) TextView messageDefault;
    private PreferenceHelper preferenceHelper;
    private Restfull restfull;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llamadas);
        ButterKnife.bind(this);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        restfull = retrofit.create(Restfull.class);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getText(R.string.op_misllamadas));
    }

    @Override
    protected void onStart() {

        super.onStart();
        preferenceHelper = new PreferenceHelper(this);
        lstLlamadas = new ArrayList<>();

        final ProgressDialog progressDialog = ProgressDialog.show( this, null, null, false, true );
        progressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        progressDialog.setContentView( R.layout.progress_bar );

        Map<String, String> map = new HashMap<>();
        map.put("op", "get-my-calls");
        map.put("provider_id", preferenceHelper.getUserId());


        restfull.getLlamadasRealizadas(preferenceHelper.getToken(), map).enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(@NonNull Call<JsonArray> call,
                                   @NonNull Response<JsonArray> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        JsonArray tdata = response.body().getAsJsonArray();
                        for(JsonElement item: tdata){
                            JsonObject odata = item.getAsJsonObject();
                            Log.d(TAG, ""+item.toString());
                            Llamada lla = new Llamada();
                            lla.ad_id = odata.get("ad_id").getAsInt();
                            lla.ad_name = odata.get("ad_name").getAsString();
                            lla.ad_price = odata.get("ad_price").getAsString();

                            lla.provider_id = odata.get("provider_id").getAsInt();
                            lla.provider_name = odata.get("provider_name").getAsString();
                            lla.provider_phone = odata.get("provider_phone").getAsString();
                            lla.call_date = odata.get("call_date").getAsString();
                            lstLlamadas.add(lla);
                        }

                        if(lstLlamadas.size() > 0){
                            messageDefault.setVisibility(View.GONE);
                            LlamadaItemAdapter adapter = new LlamadaItemAdapter(LlamadasAct.this, R.layout.item_llamadas, lstLlamadas);
                            adapter.notifyDataSetChanged();
                            lvLlamadas.setAdapter(adapter);
                        } else {
                            lvLlamadas.setAdapter(null);
                        }

                    }else {
                        Log.e(TAG, "body es null");
                    }

                } else {
                    Log.e(TAG, "Error : "+response.code());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                Crashlytics.logException(t);
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
