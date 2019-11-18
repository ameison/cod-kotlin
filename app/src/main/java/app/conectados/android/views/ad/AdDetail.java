package app.conectados.android.views.ad;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.conectados.android.BuildConfig;
import app.conectados.android.GlideApp;
import app.conectados.android.R;
import app.conectados.android.data.network.Restfull;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.utils.PermissionUtil;
import app.conectados.android.utils.ViewUtils;
import app.conectados.android.views.messages.EnviarMensajeAct;
import app.conectados.android.views.location.LocationAdActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AdDetail extends AppCompatActivity {

    private String TAG = AdDetail.class.getName();

    private Integer adId;
    private Integer providerId;
    private Restfull restfull;
    private PreferenceHelper preferenceHelper;
    private ArrayList<String> listaImagenes = new ArrayList<>();
    private String fullNameProvider;
    private Double locLat;
    private Double locLng;

    @BindView(R.id.imageGallery) LinearLayout imageGallery;
    @BindView(R.id.rating) RatingBar ratingBar;
    @BindView(R.id.titulo) TextView titulo;
    @BindView(R.id.descripcion) EditText descripcion;
    @BindView(R.id.precio) EditText precio;
    @BindView(R.id.btnLlamar) Button btnLlamar;
    @BindView(R.id.btnEnviarMensaje) Button btnEnviarMensaje;
    @BindView(R.id.btnVerUbicacion) Button btnVerUbicacion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setLogo(null);


        preferenceHelper = new PreferenceHelper(this);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        restfull = retrofit.create(Restfull.class);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            adId = bundle.getInt("adId");
            Map<String, String> map = new HashMap<>();
            map.put("op", "get-ad");
            Log.d(TAG, "adId : " + adId);
            map.put("ad_id", String.valueOf(adId));

            restfull.getAnuncioDelProveedor(preferenceHelper.getToken(), map).enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject xdata = response.body();
                        Log.d(TAG, "xdata >>> ads_detail >>> "+xdata);
                        if (xdata.has("id")) {

                            String id = xdata.get("id").getAsString();
                            String name = xdata.get("name").getAsString();
                            String description = xdata.get("description").getAsString();
                            String price = xdata.get("price").getAsString();
                            String phone = xdata.get("phone").getAsString();
                            JsonArray pics = xdata.get("pics").getAsJsonArray();
                            JsonObject provider = xdata.get("provider").getAsJsonObject();

                            titulo.setText(WordUtils.capitalize(name, new char[]{'.'}));
                            descripcion.setText(WordUtils.capitalize(description, new char[]{'.'}));
                            precio.setText(price);
                            btnLlamar.setTag(phone);

                            providerId = provider.get("id").getAsInt();
                            String nombreProv = WordUtils.capitalize(provider.get("firstname").getAsString());
                            String apellidoProv = WordUtils.capitalize(provider.get("surname").getAsString());

                            locLat = provider.get("loc_lat").getAsDouble();
                            locLng = provider.get("loc_lng").getAsDouble();


                            btnVerUbicacion.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(AdDetail.this, LocationAdActivity.class);
                                    i.putExtra("lat", locLat);
                                    i.putExtra("lng", locLng);
                                    startActivity(i);
                                }
                            });


                            apellidoProv = apellidoProv.split(" ")[0];
                            int rating = provider.get("rating").getAsInt();
                            fullNameProvider = nombreProv + " " + apellidoProv;
                            getSupportActionBar().setTitle(fullNameProvider);

                            Log.d(TAG, "locLat es " + locLat);
                            Log.d(TAG, "locLng es " + locLng);

                            ratingBar.setRating(rating);

                            for (JsonElement pathImg : pics) {

                                int position = listaImagenes.size();
                                listaImagenes.add(pathImg.getAsString());
                                imageGallery.addView(addImageCtrl(300, pathImg.getAsString(), position));

                            }
                        }

                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    Crashlytics.logException(t);
                    t.printStackTrace();
                    ViewUtils.message(AdDetail.this, getResources().getString(R.string.server_problem));
                }

            });


        } else {
            Log.d(TAG, "Bundle es nulo");
        }

        btnEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdDetail.this, EnviarMensajeAct.class);
                i.putExtra("fullNameProvider", fullNameProvider);
                i.putExtra("adId", adId);
                i.putExtra("providerId", providerId);
                i.putExtra("customerId", Integer.parseInt(preferenceHelper.getUserId()));
                startActivity(i);
            }
        });

        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permAccessLoc = ActivityCompat.checkSelfPermission(AdDetail.this, Manifest.permission.READ_PHONE_STATE);
                    if (permAccessLoc == PackageManager.PERMISSION_GRANTED) {
                        JsonObject jsonRequest = new JsonObject();
                        jsonRequest.addProperty("provider_id", preferenceHelper.getUserId());
                        jsonRequest.addProperty("ads_id", adId);
                        restfull.guardarLLamada(preferenceHelper.getToken(), jsonRequest).enqueue(new Callback<JsonObject>() {

                            @Override
                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                if (response.isSuccessful()) {
                                    JsonObject e = response.body();
                                    if (e.get("status").getAsString().equals("ok")) {
                                        Log.d(TAG, "Llamda registrada");
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                Crashlytics.logException(t);
                                t.printStackTrace();
                            }

                        });

                        String phone = v.getTag().toString();
                        String uri = "tel:" + phone.trim();
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    }else{
                        Log.d(TAG, "No tiene permiso de acceso a ubicacion");
                        ActivityCompat.requestPermissions(AdDetail.this, new String[]{Manifest.permission.READ_PHONE_STATE},
                                PermissionUtil.MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    }
                }

            }
        });

    }

    public ImageView addImageCtrl(int widthDisplay, String url, final int position) {

        Log.d(TAG, "posicion adsdt: " + position);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthDisplay, widthDisplay);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(2, 2, 2, 2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdDetail.this, AdImageSwipe.class);
                i.putExtra("imagenes", listaImagenes);
                i.putExtra("position", position);
                startActivity(i);
            }
        });


        Context ctx = AdDetail.this;
        if (ViewUtils.isValidContextForGlide(ctx)) {
            GlideApp.with(ctx)
                    .load(url)
                    .placeholder(R.mipmap.giphy)
                    .into(imageView);
        }

        return imageView;
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
