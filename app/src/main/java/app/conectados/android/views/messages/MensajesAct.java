package app.conectados.android.views.messages;

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
import app.conectados.android.custom.adapters.LlamadaItemAdapter;
import app.conectados.android.data.network.Restfull;
import app.conectados.android.models.Ad;
import app.conectados.android.models.Mensaje;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MensajesAct extends AppCompatActivity {

    private String TAG = this.getClass().getName();
    private List<Mensaje> listaMensajues;
    @BindView(R.id.lvMensajes) ListView lvMensajes;
    @BindView(R.id.messageDefault) TextView messageDefault;
    private PreferenceHelper preferenceHelper;
    private Restfull restfull;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ButterKnife.bind(this);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        restfull = retrofit.create(Restfull.class);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getText(R.string.op_mensajes));



    }

    @Override
    protected void onStart() {
        super.onStart();
        preferenceHelper = new PreferenceHelper(this);
        listaMensajues = new ArrayList<>();

        final ProgressDialog progressDialog = ProgressDialog.show( this, null, null, false, true );
        progressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        progressDialog.setContentView( R.layout.progress_bar );

        Map<String, String> map = new HashMap<>();
        map.put("op", "get-all-messages");
        map.put("provider_id", preferenceHelper.getUserId());


        restfull.getListaMensajes(preferenceHelper.getToken(), map).enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(@NonNull Call<JsonArray> call,
                                   @NonNull Response<JsonArray> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        JsonArray tdata = response.body().getAsJsonArray();
                        for(JsonElement item: tdata){
                            JsonObject odata = item.getAsJsonObject();
                            Log.d(TAG, ""+item.toString());
                            Mensaje m = new Mensaje();
                            m.customerId = odata.get("customer_id").getAsInt();
                            m.providerId = odata.get("provider_id").getAsInt();
                            m.providerName = odata.get("provider_name").getAsString();
                            m.customerName = odata.get("customer_name").getAsString();
                            m.body = odata.get("body").getAsString();
                            m.date = odata.get("date").getAsString();
                            JsonObject adOb = odata.get("ads").getAsJsonObject();
                            m.ad = new Ad();
                            m.ad.id = adOb.get("id").getAsInt();
                            m.ad.name = adOb.get("name").getAsString();
                            listaMensajues.add(m);
                        }

                        if(listaMensajues.size() > 0){
                            messageDefault.setVisibility(View.GONE);
                            LlamadaItemAdapter.MensajeItemAdapter adapter = new LlamadaItemAdapter.MensajeItemAdapter(MensajesAct.this,  R.layout.item_mensaje, listaMensajues);
                            adapter.notifyDataSetChanged();
                            lvMensajes.setAdapter(adapter);
                        } else {
                            lvMensajes.setAdapter(null);
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
