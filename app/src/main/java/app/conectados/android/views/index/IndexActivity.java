package app.conectados.android.views.index;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import app.conectados.android.views.MainActivity;
import app.conectados.android.R;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.utils.AppConstants;
import app.conectados.android.utils.PermissionUtil;
import app.conectados.android.utils.ViewUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import app.conectados.android.views.register.RegisterActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class IndexActivity extends AppCompatActivity implements IndexActivityPresenter.View {

    private String TAG = IndexActivity.class.getSimpleName();
    private IntentFilter closeActivityLogin = new IntentFilter();
    private PermissionUtil permissions;
    private IndexActivityPresenter presenter;

    @BindView(R.id.linkTYC) TextView linkTYC;
    private CallbackManager callbackManager; // Facebook


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);

        //android O fix bug orientation
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        permissions = new PermissionUtil();
        callbackManager = CallbackManager.Factory.create();
        presenter = new IndexActivityPresenter(this, new PreferenceHelper(this), callbackManager);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null){
            startActivity(new Intent(IndexActivity.this, MainActivity.class));
            finish();
        }

        presenter.loginWithFb();
        permissions.requestPermission(this);
    }

    @OnClick(R.id.btnRegistrarse)
    public void onRegister(){
        Intent intent = new Intent(IndexActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.linkTYC)
    public void onReadTyc(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://conectados.app/tyc/"));
        startActivity(browserIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        closeActivityLogin.addAction(AppConstants.BROADCAST_CERRAR_PANTALLA_LOGIN);
        registerReceiver(rcvSplashActivity, closeActivityLogin);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private BroadcastReceiver rcvSplashActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "rcvSplashActivity - close activity");
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            unregisterReceiver(rcvSplashActivity);
        }catch (Exception e){
            e.getMessage();
        }

    }


    // Views  from Presenter **********************

    @Override
    public void showToastMessage(int idMessage) {
        Toast.makeText(this, getString(idMessage), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void endFbLogin(String nomUser) {
        startActivity(new Intent(this, MainActivity.class));
        ViewUtils.message(IndexActivity.this, getString(R.string.bienvenido) +nomUser);
        finish();
    }
}
