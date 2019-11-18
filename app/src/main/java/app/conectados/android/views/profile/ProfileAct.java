package app.conectados.android.views.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.text.WordUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import app.conectados.android.BuildConfig;
import app.conectados.android.GlideApp;
import app.conectados.android.R;
import app.conectados.android.data.network.Restfull;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.utils.ImageUtils;
import app.conectados.android.utils.ViewUtils;
import app.conectados.android.views.password.ChangePasswordActivity;
import app.conectados.android.views.location.LocationAc;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileAct extends AppCompatActivity {

    private String TAG = this.getClass().getName();
    public static final int REQUEST_PROVIDER_LOC = 5;
    public static final int UPLOAD_AD_PIC = 3; 
    private String provider_pic;

    @BindView(R.id.tvNameUser) TextView tvNameUser;
    @BindView(R.id.tvPhoneUser) TextView tvPhoneUser;
    @BindView(R.id.tvEmailUser) TextView tvEmailUser;
    @BindView(R.id.tvDateCreated) TextView tvDateCreated;

    @BindView(R.id.btnChangePassword)  Button btnChangePassword;
    @BindView(R.id.imgPhotoClient)  CircleImageView imgPhotoClient;
    @BindView(R.id.btnCloseSesion) Button btnCloseSesion;
    @BindView(R.id.btnAddReference) Button btnAddReference;
    @BindView(R.id.btnAddPic) Button btnAddPic;

    private Restfull restfull;
    private PreferenceHelper preferenceHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getText(R.string.op_perfil));


        preferenceHelper = new PreferenceHelper(this);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        restfull = retrofit.create(Restfull.class);
        Map<String, String> map = new HashMap<>();
        map.put("op", "get-provider-with-loc");
        map.put("idx", preferenceHelper.getUserId());
        restfull.getPerfilProveedor(preferenceHelper.getToken(), map).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if(response.isSuccessful()){
                    JsonObject xdata = response.body();
                    if(xdata.has("id")){
                        String id = xdata.get("id").getAsString();
                        String firstname = xdata.get("firstname").getAsString();
                        String surname = xdata.get("surname").getAsString();
                        String phone = xdata.get("phone").getAsString();
                        String email = xdata.get("email").getAsString();
                        String date_created = xdata.get("date_created").getAsString();

                        tvNameUser.setText(WordUtils.capitalize(firstname + " "+ surname));
                        tvPhoneUser.setText(WordUtils.capitalize(phone));
                        tvDateCreated.setText(WordUtils.capitalize(date_created));
                        tvEmailUser.setText(email);
                    }

                    RequestOptions myOptions = new RequestOptions().
                            diskCacheStrategy(DiskCacheStrategy.NONE).
                            skipMemoryCache(true);

                    if(xdata.has("photo")){
                        
                        String imageUrl = xdata.get("photo").getAsString();

                        if (!ProfileAct.this.isFinishing()) {
                            GlideApp.with(ProfileAct.this)
                                    .applyDefaultRequestOptions(myOptions)
                                    .load(imageUrl)
                                    .placeholder(R.mipmap.giphy)
                                    .into(imgPhotoClient) ;
                        }

                    }else{

                        if (!ProfileAct.this.isFinishing()) {
                            GlideApp.with(ProfileAct.this)
                                    .load(R.mipmap.upload_pic)
                                    .placeholder(R.mipmap.giphy)
                                    .into(imgPhotoClient);
                            btnAddPic.setTextColor(getResources().getColor(R.color.cns_rojo));
                            imgPhotoClient.setImageResource(R.mipmap.icomenu_user);
                        }

                    }

                    if(xdata.has("locs")){
                        JsonArray xLocs = xdata.get("locs").getAsJsonArray();
                        if(xLocs.size() == 0){
                            btnAddReference.setTextColor(getResources().getColor(R.color.cns_rojo));
                        }else{
                            btnAddReference.setText(R.string.update_referencia);
                        }
                    }else{
                        Log.d(TAG, "No tiene locs");
                        btnAddReference.setTextColor(getResources().getColor(R.color.cns_rojo));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Crashlytics.logException(t);
                t.printStackTrace();
                ViewUtils.message(ProfileAct.this, getResources().getString(R.string.server_problem));
            }

        });

        btnAddReference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ProfileAct.this, LocationAc.class), REQUEST_PROVIDER_LOC);
            }
        });

        btnAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, UPLOAD_AD_PIC);
            }
        });

        imgPhotoClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crashlytics.log("Se presiono img de foto de perfil");
            }
        });

        btnChangePassword.setOnClickListener(new OnClickChangePassword());
        btnCloseSesion.setOnClickListener(new OnClickCerrarSesion());

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "requestCode : "+requestCode);
        if(requestCode== REQUEST_PROVIDER_LOC && resultCode == Activity.RESULT_OK) {

            final ProgressDialog progressDialog;
            progressDialog = ProgressDialog.show(ProfileAct.this, null,   getString(R.string.waitPlease), true);
            progressDialog.setCancelable(false);

            String lat = String.valueOf(data.getDoubleExtra("lat", 0));
            String lng = String.valueOf(data.getDoubleExtra("lng", 0));

            JsonObject odata  = new JsonObject();
            odata.addProperty("op", "register-provider-loc");
            odata.addProperty("provider_id", preferenceHelper.getUserId());
            odata.addProperty("lat", lat);
            odata.addProperty("lng", lng);

            restfull.registrarUbicacionProveedor(preferenceHelper.getToken(), odata).enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                    if (response.isSuccessful()) {

                        JsonObject e = response.body();
                        if (e.get("status").getAsString().equals("ok")){
                            btnAddReference.setTextColor(getResources().getColor(R.color.cns_celeste));
                            btnAddReference.setText(R.string.update_referencia);
                            ViewUtils.message(ProfileAct.this, getString(R.string.referencia_guardada));
                        }else{
                            switch (e.get("message").getAsString()){
                                case "email-is-empty":
                                    ViewUtils.message(ProfileAct.this, getResources().getString(R.string.server_problem));
                                    break;
                            }
                        }

                    } else {
                        Log.e(TAG, "Error "+response.code());
                        ViewUtils.message(ProfileAct.this, getResources().getString(R.string.server_problem));
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    Crashlytics.logException(t);
                    t.printStackTrace();
                    progressDialog.dismiss();
                    ViewUtils.message(ProfileAct.this, getResources().getString(R.string.server_problem));
                }
            });

        }else if(requestCode== UPLOAD_AD_PIC && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();

            try {

                final ProgressDialog progressDialog;
                progressDialog = ProgressDialog.show(ProfileAct.this, null,   getString(R.string.waitPlease), true);
                progressDialog.setCancelable(false);

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(ProfileAct.this.getContentResolver(), selectedImage);
                Bitmap picProfile = ImageUtils.scaleToFitWidth(bitmap, 400);
                imgPhotoClient.setImageBitmap(picProfile);
                provider_pic = ImageUtils.encodeTobase64(picProfile);


                JsonObject odata  = new JsonObject();
                odata.addProperty("op", "register-provider-pic");
                odata.addProperty("provider_id", preferenceHelper.getUserId());
                odata.addProperty("provider_pic", provider_pic);

                restfull.registrarFotoProveedor(preferenceHelper.getToken(), odata).enqueue(new Callback<JsonObject>() {

                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                        if (response.isSuccessful()) {

                            JsonObject e = response.body();
                            if (e.get("status").getAsString().equals("ok")){
                                btnAddPic.setText(R.string.actualizar_foto_perfil);
                                btnAddPic.setTextColor(getResources().getColor(R.color.cns_celeste));
                                ViewUtils.message(ProfileAct.this, getString(R.string.foto_proveedor_guardado));
                            }else{
                                switch (e.get("message").getAsString()){
                                    case "email-is-empty":
                                        ViewUtils.message(ProfileAct.this, getResources().getString(R.string.server_problem));
                                        break;
                                }
                            }

                        } else {
                            Log.e(TAG, "Error "+response.code());
                            ViewUtils.message(ProfileAct.this, getResources().getString(R.string.server_problem));
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                        Crashlytics.logException(t);
                        t.printStackTrace();
                        progressDialog.dismiss();
                        ViewUtils.message(ProfileAct.this, getResources().getString(R.string.server_problem));
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class OnClickChangePassword implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ProfileAct.this, ChangePasswordActivity.class);
            startActivity(intent);
        }
    }

    class OnClickCerrarSesion implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileAct.this);
            builder.setTitle(R.string.message);
            builder.setMessage(R.string.confirmCloseSession);
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.accept).toUpperCase(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    preferenceHelper.cleanAll();
                    LoginManager.getInstance().logOut();
                    ViewUtils.message(ProfileAct.this, getResources().getString(R.string.ExitCloseSession));
                    setResult(RESULT_OK, null);
                    ProfileAct.this.finish();
                }
            });
            builder.setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();

        }
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
