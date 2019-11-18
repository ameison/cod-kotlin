package app.conectados.android.views.register;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import app.conectados.android.R;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.models.User;
import app.conectados.android.utils.AppConstants;
import app.conectados.android.utils.ViewUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;

import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    private String TAG = RegisterActivity.class.getSimpleName();
    @BindView(R.id.nombre) EditText nombreView ;
    @BindView(R.id.apellido) EditText apellidoView ;
    @BindView(R.id.etNumPlaca) EditText etNumPlaca ;
    @BindView(R.id.registrarUsuario) Button registrarUsuario ;
    @BindView(R.id.lyRegistro) LinearLayout lyRegistro ;

    private PreferenceHelper preferenceHelper;
    private FirebaseFirestore db;
    private IntentFilter getFlagCountry = new IntentFilter();

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        db = FirebaseFirestore.getInstance();
        preferenceHelper = new PreferenceHelper(this);

        // -- Toolbar
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setLogo(null);
        getSupportActionBar().setTitle(getString(R.string.registrar_vehiculo));
        // -- Fin toolbar


        registrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarCliente(nombreView.getText().toString().trim(),
                        apellidoView.getText().toString().trim(),
                        etNumPlaca.getText().toString().trim());
            }
        });


    }


    private void registrarCliente(final String name,
                                  final String apellido,
                                  final String numPlaca){

        if (isValidForm(name, apellido, numPlaca)){

            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.internet_cnx_error), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(task.getResult() != null){
                        String token = task.getResult().getToken();

                        final ProgressDialog progressDialog;
                        progressDialog = ProgressDialog.show(RegisterActivity.this, null,   getString(R.string.waitPlease), true);
                        progressDialog.setCancelable(false);

                        CollectionReference dbUsers = db.collection("users");

                        User us = new User();
                        us.setFirstname(name);
                        us.setSurname(apellido);
                        us.setPlateNumber(numPlaca);
                        us.setDeviceMame("android");
                        us.setFirebaseToken(token);

                        dbUsers.add(us).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                ViewUtils.message(RegisterActivity.this, "Agregado");
                                progressDialog.dismiss();
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ViewUtils.message(RegisterActivity.this, "Fallo");
                                progressDialog.dismiss();
                            }
                        });

                    }
                }
            });
        }
    }


    private boolean isValidForm(String nombre, String apellido, String numPlaca){

        if(!ViewUtils.isAlphaNumeric(nombre)){
            ViewUtils.message(this, getString(R.string.error_formato_nombre));
            return false;
        }else{
            Log.i(TAG, "nombre válido");
        }
        if(!ViewUtils.isAlphaNumeric(apellido)){
            ViewUtils.message(this, getString(R.string.error_formato_apellido));
            return false;
        }else{
            Log.i(TAG, "apellido válido");
        }
        if(!ViewUtils.isAlphaNumeric(numPlaca)){
            ViewUtils.message(this, getString(R.string.error_formato_telefono));
            return false;
        }else{
            Log.i(TAG, "formato válido");
        }
        return true;
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



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(rcvFlag);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getFlagCountry.addAction(AppConstants.BROADCAST_DEVOLVER_BANDERA);
        registerReceiver(rcvFlag, getFlagCountry);
    }

    private BroadcastReceiver rcvFlag = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "rcvFlag - close activity");
            String flag = intent.getExtras().getString("flag");
            String zipName = flag.split(",")[0];
            String zipCode = flag.split(",")[1];
        }
    };

}