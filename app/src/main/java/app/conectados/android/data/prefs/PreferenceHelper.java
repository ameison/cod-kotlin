package app.conectados.android.data.prefs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

    private static final String SHP_CLIENTE = "Cliente";
    private static final String KEY_ID = "id";
    private static final String KEY_TOKEN = "token_seguridad";

    private SharedPreferences prefCliente;

    public PreferenceHelper(Activity activity) {
        prefCliente = activity.getSharedPreferences(SHP_CLIENTE, Context.MODE_PRIVATE);
    }

    public void setUserId(String id) {
        prefCliente.edit().putString(KEY_ID, id).apply();
    }

    public String getUserId() {
        return prefCliente.getString(KEY_ID, "");
    }

    public void setToken(String tokenSeguridad){
        prefCliente.edit().putString(KEY_TOKEN, tokenSeguridad).apply();
    }

    public String getBearerToken(){ return "Bearer "+prefCliente.getString(KEY_TOKEN, null); }

    public String getToken(){ return prefCliente.getString(KEY_TOKEN, null); }

    public void cleanAll() {
        prefCliente.edit().clear().apply();
    }


}