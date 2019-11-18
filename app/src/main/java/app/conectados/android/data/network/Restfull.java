package app.conectados.android.data.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;


public interface Restfull {

    @POST(PROVIDERS_RESOURCE)
    Call<JsonObject> registrarProveedor(@Header("Authorization") String token, @Body JsonObject json);

    @PUT(PROVIDERS_RESOURCE)
    Call<JsonObject> registrarFotoProveedor(@Header("Authorization") String token, @Body JsonObject json);

    @PUT(PROVIDERS_RESOURCE)
    Call<JsonObject> registrarUbicacionProveedor(@Header("Authorization") String token, @Body JsonObject json);

    @GET(PROVIDERS_RESOURCE)
    Call<JsonObject> getPerfilProveedor(@Header("Authorization") String token, @QueryMap Map<String, String> map);

    @GET(PROVIDERS_RESOURCE)
    Call<JsonArray> getProveedores(@Header("Authorization") String token, @QueryMap Map<String, String> map);

    @POST(AUTH_RESOURCE)
    Call<JsonObject> login(@Body JsonObject json);

    @GET(CATEGORIES_RESOURCE)
    Call<JsonArray> getCategories(@Header("Authorization") String token);

    @POST(ADS_RESOURCE)
    Call<JsonObject> registrarAnuncio(@Header("Authorization") String token, @Body JsonObject json);

    @GET(ADS_RESOURCE)
    Call<JsonObject> getAnuncioDelProveedor(@Header("Authorization") String token, @QueryMap Map<String, String> map);

    @POST(CALLS_RESOURCE)
    Call<JsonObject> guardarLLamada(@Header("Authorization") String token, @Body JsonObject json);

    @POST(MESSAGES_RESOURCE)
    Call<JsonObject> guardarMensaje(@Header("Authorization") String token, @Body JsonObject json);

    @GET(CALLS_RESOURCE)
    Call<JsonArray> getLlamadasRealizadas(@Header("Authorization") String token, @QueryMap Map<String, String> map);

    @GET(MESSAGES_RESOURCE)
    Call<JsonArray> getListaMensajes(@Header("Authorization") String token, @QueryMap Map<String, String> map);

    @GET(MESSAGES_RESOURCE)
    Call<JsonObject> getMensajesXAnuncio(@Header("Authorization") String token, @QueryMap Map<String, String> map);

    @GET(ADS_RESOURCE)
    Call<JsonArray> getAnuncios(@Header("Authorization") String token, @QueryMap Map<String, String> map);

    @GET(ADS_RESOURCE)
    Call<JsonObject> getAnunciosConDatosProveedor(@Header("Authorization") String token, @QueryMap Map<String, String> map);

    @PUT(ADS_RESOURCE)
    Call<JsonObject> editarAnuncio(@Header("Authorization") String token, @Body JsonObject json);

    @PATCH(PROVIDERS_RESOURCE + "{idx}")
    Call<JsonObject> cambiarPassword(@Header("Authorization") String token, @Path("idx") String idx, @Body JsonObject json);

    @DELETE(ADS_RESOURCE + "{idx}")
    Call<JsonObject> eliminarAnuncio(@Header("Authorization") String token, @Path("idx") String idx);

    String AUTH_RESOURCE = "/rest/auth/";
    String PROVIDERS_RESOURCE = "/rest/providers/";
    String ADS_RESOURCE = "/rest/ads/";
    String CALLS_RESOURCE = "/rest/calls/";
    String MESSAGES_RESOURCE = "/rest/messages/";
    String CATEGORIES_RESOURCE = "/rest/categories/";

}
