package app.conectados.android.views.ad;

import android.util.Log;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import app.conectados.android.BuildConfig;
import app.conectados.android.R;
import app.conectados.android.data.network.Restfull;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.models.Ad;
import app.conectados.android.models.Category;
import app.conectados.android.models.SubCategory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdRegisterActPresenter {

    private String TAG = AdRegisterActPresenter.class.getSimpleName();
    private View view;
    private PreferenceHelper preferenceHelper;
    private Restfull restfull;

    public AdRegisterActPresenter(View view, PreferenceHelper preferenceHelper) {
        this.view = view;
        this.preferenceHelper = preferenceHelper;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        restfull = retrofit.create(Restfull.class);
    }

    public void registerAd(Ad ad){

        Gson gson = new Gson();
        JsonObject odata  = new JsonObject();
        odata.addProperty("provider_id", preferenceHelper.getUserId());
        odata.addProperty("titleAd", ad.name);
        odata.addProperty("descAd", ad.description);
        odata.addProperty("price", ad.price);
        odata.addProperty("categoryId", ad.categoryId);
        odata.addProperty("sub_category_id", ad.subCategoryId);
        odata.add("listaFotos", gson.toJsonTree(ad.picsBase64).getAsJsonObject());

        view.showProgressBar();
        restfull.registrarAnuncio(preferenceHelper.getToken(), odata).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    JsonObject e = response.body();
                    if(e != null){
                        try{
                            if (e.get("status").getAsString().equals("ok")){
                                view.showToastMessage(R.string.anucio_publicado);
                                view.registerAdSuccessfull();
                            }else{
                                view.showToastMessage(e.get("message").getAsString());
                            }
                        }catch (Exception ex){
                            Crashlytics.logException(ex);
                        }
                    }

                } else {
                    Log.e(TAG, "Error de red : "+response.code());
                    Crashlytics.log("Error de red : "+response.code());
                    Crashlytics.logException( new RuntimeException("Error de red : "+response.code()));
                    view.showToastMessage(R.string.network_problem);
                }
                view.hideProgressBar();
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Crashlytics.logException(t);
                t.printStackTrace();
                view.hideProgressBar();
                view.showToastMessage(R.string.server_problem);
            }
        });
    }

    public void validarFotoyLocsEnPerfil(){

        Map<String, String> map = new HashMap<>();
        map.put("op", "get-provider-with-loc");
        map.put("idx", preferenceHelper.getUserId());
        restfull.getPerfilProveedor(preferenceHelper.getToken(), map).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                boolean tieneFoto  = false;
                boolean tieneLocs  = false;
                if(response.isSuccessful()){
                    JsonObject xdata = response.body();

                    if(xdata.has("photo")){  tieneFoto = true;   }
                    if(xdata.has("locs")){
                        JsonArray xLocs = xdata.get("locs").getAsJsonArray();
                        if(xLocs.size() != 0){
                            tieneLocs = true;
                        }
                    }

                    view.isValidForSellerProfile(tieneFoto && tieneLocs);

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Crashlytics.logException(t);
                t.printStackTrace();
                view.showToastMessage(R.string.server_problem);
            }

        });
    }

    public void requestCategorias(){

        restfull.getCategories(preferenceHelper.getToken()).enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                JsonArray categories = response.body();
                if(categories != null){
                    List<Category> listaCategories = parsearListaCategorias(categories);
                    view.showCategories(listaCategories);
                }else{
                    view.showToastMessage(R.string.server_problem);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {  }
        });

    }

    private List<Category> parsearListaCategorias(JsonArray categories){
        List<Category> listaCategories = new ArrayList<>();
        for (JsonElement e : categories){
            JsonObject jObject = e.getAsJsonObject();
            Category c = new Category();
            c.id = jObject.get("id").getAsInt();
            c.name = jObject.get("name").getAsString();
            c.mipmapName = jObject.get("mipmapid").getAsString();

            if(jObject.has("sub_categories")){
                JsonArray jArray = jObject.get("sub_categories").getAsJsonArray();
                if(jArray.size() > 0){
                    List<SubCategory> subcategorias = new ArrayList<>();
                    for (JsonElement sc : jArray){
                        JsonObject o = sc.getAsJsonObject();
                        SubCategory scat = new SubCategory();
                        scat.id = Integer.parseInt(o.get("id").getAsString());
                        scat.name = o.get("name").getAsString();
                        subcategorias.add(scat);
                    }
                    c.subCategorias = subcategorias;
                }
            }

            listaCategories.add(c);
        }

        return listaCategories;
    }


    public interface View{
        // output
        void showToastMessage(int idMessage);
        void showToastMessage(String message);
        void isValidForSellerProfile(boolean result);
        void registerAdSuccessfull();
        void showCategories(List<Category> listaCategories);
        void showProgressBar();
        void hideProgressBar();
    }


}
