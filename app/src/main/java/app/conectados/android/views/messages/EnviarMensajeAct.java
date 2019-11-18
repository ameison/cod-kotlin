package app.conectados.android.views.messages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import app.conectados.android.BuildConfig;
import app.conectados.android.R;
import app.conectados.android.data.network.Restfull;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnviarMensajeAct extends AppCompatActivity {

    private String TAG = this.getClass().getName();
    private int providerId;
    private int customerId;
    private int adId;
    private Restfull restfull;
    private PreferenceHelper preferenceHelper;

    @BindView(R.id.scrollView) ScrollView scrollView;
    @BindView(R.id.btnSendMessage) ImageButton btnSendMessage;
    @BindView(R.id.bodyMsj) EditText bodyMsj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        ButterKnife.bind(this);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        restfull = retrofit.create(Restfull.class);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        preferenceHelper = new PreferenceHelper(this);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String fullNameProvider = bundle.getString("fullNameProvider");
            providerId = bundle.getInt("providerId");
            customerId = bundle.getInt("customerId");
            adId = bundle.getInt("adId");
            Log.d(TAG, ">> msj a >> "+fullNameProvider);
            Log.d(TAG, ">> msj providerId >> "+providerId);
            Log.d(TAG, ">> msj customerId >> "+customerId);
            Log.d(TAG, ">> msj adId >> "+adId);

            getSupportActionBar().setTitle(fullNameProvider);

            Map<String, String> map = new HashMap<>();
            map.put("op", "get-messages-by-ad");
            map.put("ads_id", ""+adId);
            map.put("provider_id", ""+providerId);
            map.put("customer_id", ""+customerId);

            Log.d(TAG, "map >> "+map.toString());
            restfull.getMensajesXAnuncio(preferenceHelper.getToken(), map).enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                    if (response.isSuccessful()) {
                        if (response.body() != null) {

                            JsonObject tdata = response.body().getAsJsonObject();
                            JsonArray xdata = tdata.get("messages").getAsJsonArray();
                            Log.d(TAG, "tdata >> "+xdata );
                            for(JsonElement item: xdata){
                                JsonObject odata = item.getAsJsonObject();
                                String body = odata.get("body").getAsString();
                                String date = odata.get("date").getAsString();
                                String senderId = odata.get("sender_id").getAsString();
                                String senderName = odata.get("sender_name").getAsString();
                                Log.d(TAG, senderName  +" - "+senderName + " - " + body + " - "+date);

                                if(senderId.equals(preferenceHelper.getUserId())){
                                    enviarMsj(body);
                                }else{
                                    recibirMsj(body);
                                }
                            }

                        }else {
                            Log.e(TAG, "body es null");
                        }

                    } else {
                        Log.e(TAG, "Error : "+response.code());
                    }

                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    Crashlytics.logException(t);
                }

            });


            btnSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!bodyMsj.getText().toString().trim().equals("")){
                        JsonObject jsonRequest = new JsonObject();
                        jsonRequest.addProperty("customer_id", customerId);
                        jsonRequest.addProperty("provider_id", providerId);
                        jsonRequest.addProperty("sender_id", preferenceHelper.getUserId());
                        jsonRequest.addProperty("ads_id", adId);
                        jsonRequest.addProperty("body", bodyMsj.getText().toString().trim());
                        restfull.guardarMensaje(preferenceHelper.getToken(), jsonRequest).enqueue(new Callback<JsonObject>() {

                            @Override
                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                if (response.isSuccessful()) {
                                    JsonObject e = response.body();
                                    Log.d(TAG, "Mensaje >> "+e.toString());
                                    if (e.get("status").getAsString().equals("ok")) {
                                        enviarMsj(bodyMsj.getText().toString().trim());
                                        bodyMsj.setText("");
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                Crashlytics.logException(t);
                                t.printStackTrace();
                            }

                        });
                    }

                }
            });
        }else{
            ViewUtils.message(this, "No se pudo cargar los mensajes");
        }

        scrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void enviarMsj(String msj){
        LinearLayout linearLayout = findViewById(R.id.contenedorMsj);
        TextView valueTV = new TextView(this);
        valueTV.setText(msj);
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/quicksand_regular.otf");
        valueTV.setTypeface(face);

        valueTV.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_chat_borde_celeste));

        int paddInPx = ViewUtils.convertDpToPx(this, 10);
        valueTV.setPadding(paddInPx, paddInPx, paddInPx, paddInPx);

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int marEndInPx = ViewUtils.convertDpToPx(this, 50);
        int marBottomInPx = ViewUtils.convertDpToPx(this, 5);
        llp.setMargins(0, 0, marEndInPx, marBottomInPx);
        valueTV.setLayoutParams(llp);
        linearLayout.addView(valueTV);
    }

    private void recibirMsj(String msj){
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.contenedorMsj);
        TextView valueTV = new TextView(this);
        valueTV.setText(msj);
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/quicksand_regular.otf");
        valueTV.setTypeface(face);

        valueTV.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_chat_fondo_celeste));

        int paddInPx = ViewUtils.convertDpToPx(this, 10);
        valueTV.setPadding(paddInPx, paddInPx, paddInPx, paddInPx);

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.gravity = Gravity.RIGHT;
        int marStartInPx = ViewUtils.convertDpToPx(this, 100);
        int marBottomInPx = ViewUtils.convertDpToPx(this, 5);
        llp.setMargins(marStartInPx, 0, 0, marBottomInPx);
        valueTV.setLayoutParams(llp);
        linearLayout.addView(valueTV);
    }

    @Override
    public void onResume() {
        super.onResume();

        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("send-provider-message"));
    }

    // Handling the received Intents for the "my-integer" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String myMessage = intent.getStringExtra("message");
            recibirMsj(myMessage);
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mMessageReceiver);
        super.onPause();
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
