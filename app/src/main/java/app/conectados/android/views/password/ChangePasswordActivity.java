package app.conectados.android.views.password;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import app.conectados.android.BuildConfig;
import app.conectados.android.R;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.data.network.Restfull;
import app.conectados.android.utils.ViewUtils;
import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePasswordActivity extends AppCompatActivity {

    private String TAG = ChangePasswordActivity.class.getName();

    //@BindView(R.id.etOldPassword)EditText etOldPassword;
    @BindView(R.id.etNewPassword)EditText etNewPassword;
    @BindView(R.id.etNewPasswordRepeat)EditText etNewPasswordRepeat;
    @BindView(R.id.btnEnviarSolicitud) Button btnEnviarSolicitud;

    private PreferenceHelper preferenceHelper;
    private Restfull restfull;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        preferenceHelper = new PreferenceHelper(this);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getText(R.string.update_pass));

        btnEnviarSolicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarSolicitud();
            }
        });
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        restfull = retrofit.create(Restfull.class);

        Log.d(TAG, "ChangePasswordActivity ...");

    }

    private void enviarSolicitud(){

        //if (!etOldPassword.getText().toString().trim().equals("") &&
        if (!etNewPassword.getText().toString().trim().equals("") &&
                !etNewPasswordRepeat.getText().toString().trim().equals("")){
            if (etNewPassword.getText().toString().equals(etNewPasswordRepeat.getText().toString())){

                final ProgressDialog progressDialog;
                progressDialog = ProgressDialog.show(this, null, getString(R.string.waitPlease), true);
                progressDialog.setCancelable(false);

                JsonObject jsonRequest = new JsonObject();
                jsonRequest.addProperty("op", "change-provider-password");
                //jsonRequest.addProperty("pass_old", etOldPassword.getText().toString());
                jsonRequest.addProperty("pass_new", etNewPassword.getText().toString());
                restfull.cambiarPassword(preferenceHelper.getToken(), preferenceHelper.getUserId(), jsonRequest).enqueue(new Callback<JsonObject>() {

                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        if (response.isSuccessful()){
                            JsonObject e = response.body();
                            Log.i(TAG, "Inicio : "+e.toString());
                            if(e.get("status").getAsString().equals("ok")){
                                ViewUtils.message(ChangePasswordActivity.this, getResources().getString(R.string.change_pass_succes));
                                finish();
                            }else if(e.get("status").getAsString().equals("warning")){
                                if(e.get("message").getAsString().equals("password incorrect")){
                                    ViewUtils.message(ChangePasswordActivity.this, getResources().getString(R.string.change_pass_incorrect));
                                }
                            }else if(e.get("status").getAsString().equals("error")){
                                if(e.get("message").getAsString().equals("client not exist")){
                                    ViewUtils.message(ChangePasswordActivity.this, getResources().getString(R.string.reg_cliente_no_registrado));
                                }
                            }

                        }else{
                            ViewUtils.message(ChangePasswordActivity.this, getResources().getString(R.string.server_problem));
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                        Crashlytics.logException(t);
                        t.printStackTrace();
                        Log.e(TAG, "Error");
                        ViewUtils.message(ChangePasswordActivity.this, getResources().getString(R.string.change_pass_failed));
                        progressDialog.dismiss();
                    }

                });

            }else{
                ViewUtils.message(this, getResources().getString(R.string.change_pass_not_c));
            }
        }else{
            ViewUtils.message(this, getResources().getString(R.string.change_pass_old_req));
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
