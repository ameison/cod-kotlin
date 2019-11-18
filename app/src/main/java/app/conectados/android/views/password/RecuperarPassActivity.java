package app.conectados.android.views.password;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import app.conectados.android.BuildConfig;
import app.conectados.android.R;
import app.conectados.android.data.network.Restfull;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.utils.ViewUtils;
import app.conectados.android.views.index.IndexActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecuperarPassActivity extends AppCompatActivity {

    private IntentFilter closeActivityLogin = new IntentFilter();
    private String TAG = IndexActivity.class.getSimpleName();
    private Restfull restfull;
    private PreferenceHelper preferenceHelper;

    @BindView(R.id.etCorreo) EditText etCorreo;
    @BindView(R.id.btnIniciarSesion) Button btnIniciarSesion;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_pass);
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
        getSupportActionBar().setTitle(R.string.msg_recuperar_pwd);

        btnIniciarSesion.setOnClickListener(new OnclickLogin());
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        restfull = retrofit.create(Restfull.class);
    }

    private class OnclickLogin implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String correo = etCorreo.getText().toString();

        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
