package app.conectados.android.views.index;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;
import app.conectados.android.BuildConfig;
import app.conectados.android.R;
import app.conectados.android.data.network.Restfull;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.models.Provider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IndexActivityPresenter {

    private String TAG = IndexActivityPresenter.class.getSimpleName();

    private Provider provider;
    private View view;
    private PreferenceHelper preferenceHelper;
    private CallbackManager callbackManager;
    private Restfull restfull;

    public IndexActivityPresenter(View view, PreferenceHelper preferenceHelper, CallbackManager callbackManager) {
        this.provider = new Provider();
        this.view = view;
        this.preferenceHelper = preferenceHelper;
        this.callbackManager = callbackManager;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        restfull = retrofit.create(Restfull.class);
    }

    public void setPhoneUser(String phone){
        provider.phone = phone;
    }

    public void loginWithFb(){
        Log.d(TAG, " Presenter >> loginWithFb");
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>()  {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(final JSONObject object, GraphResponse response) {
                        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    view.showToastMessage(R.string.internet_cnx_error);
                                    return;
                                }

                                if(task.getResult() != null){
                                    Log.d(TAG, " Presenter >> task.getResult >> ok");
                                    String token = task.getResult().getToken();
                                    try {
                                        provider.login_type =  "login-facebook";
                                        provider.firstname = object.getString("first_name");
                                        provider.surname= object.getString("last_name");
                                        provider.email = object.getString("email");
                                        provider.password = "textosoloparapasarlogin";
                                        provider.device_id= token;
                                        provider.device_type = "android";
                                        loginConectados(provider);

                                    } catch (JSONException e) {
                                        Log.e(TAG, "Error: " + e);
                                        Crashlytics.log("Presenter >> "+e.getMessage());
                                        Crashlytics.logException(e);
                                    }
                                }
                            }
                        });


                    }
                });

                LoginManager.getInstance().logOut();
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,last_name,email,picture.width(150)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.w(TAG, "loginWithFb >> onCancel ");
                Crashlytics.log("loginWithFb - onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                view.showToastMessage(R.string.error_interno);
                Log.e(TAG, "loginWithFb >> "+exception.getMessage());
                Crashlytics.log("loginWithFb - onError");
                Crashlytics.log("loginWithFb - "+exception.getMessage());
            }
        });
    }


    private void loginConectados(final Provider provider){
        Crashlytics.log("********** loginConectados ***********");
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("login_type", provider.login_type);
        jsonRequest.addProperty("device_id", provider.device_id);
        jsonRequest.addProperty("firstname", provider.firstname);
        jsonRequest.addProperty("surname", provider.surname);
        jsonRequest.addProperty("email", provider.email);
        jsonRequest.addProperty("phone", provider.phone);
        jsonRequest.addProperty("device_type", "android");
        jsonRequest.addProperty("password", provider.password);

        Log.d(TAG, "loginWithFb >> loginConectados >> "+jsonRequest.toString());
        Crashlytics.log("loginFacebook - request json - "+jsonRequest.toString());
        restfull.login(jsonRequest).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                if(response.isSuccessful()){
                    Log.d(TAG, "loginConectados >> isSuccessful ");
                    try{
                        JsonObject e = response.body();
                        Log.d(TAG, "loginConectados >> body >>  "+e.toString());

                        if(e.get("status").getAsString().equals("ok")){
                            preferenceHelper.setUserId(e.get("id").getAsString());
                            preferenceHelper.setToken(e.get("token").getAsString());
                            view.endFbLogin(e.get("nom_apellido").getAsString());
                        }else if(e.get("status").getAsString().equals("error")){
                            Log.d(TAG, "loginConectados >> error >>  ");
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }

                } else {
                    Log.e(TAG, "Error Code"+ String.valueOf(response.code()));
                    Log.e(TAG,"Error Body"+ response.errorBody().toString());
                    view.showToastMessage(R.string.server_problem);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Crashlytics.logException(t);
                t.printStackTrace();
            }

        });


    }


    public interface View{
        // output
        void showToastMessage(int idMessage);
        void endFbLogin(String nomUser);

    }

}
