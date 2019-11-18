package app.conectados.android.custom.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;

import app.conectados.android.BuildConfig;
import app.conectados.android.R;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.models.Ad;
import app.conectados.android.data.network.Restfull;
import org.apache.commons.text.WordUtils;
import java.util.List;

import app.conectados.android.utils.ViewUtils;
import app.conectados.android.views.ad.AdDetail;
import app.conectados.android.views.ad.AdEditarAct;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdAdapter extends ArrayAdapter<Ad> {

    private final String TAG = AdAdapter.class.getSimpleName();
    private int layout;
    private Activity activity;
    private Restfull restfull;
    private List<Ad> lstAnuncios;
    private CheckListener checkListener;
    private PreferenceHelper preferenceHelper;

    public AdAdapter(Activity activity, int layout, List<Ad> lstAnuncios){
        super(activity, layout, lstAnuncios);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        this.activity = activity;
        this.layout = layout;
        this.lstAnuncios = lstAnuncios;
        this.preferenceHelper = new PreferenceHelper(activity);
        this.restfull = retrofit.create(Restfull.class);
    }


    @Override
    public int getCount() {
        return lstAnuncios.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {
        View item = convertView;

        final HolderService holder;

        if (item == null) {
            item = activity.getLayoutInflater().inflate(layout, null);
            holder = new HolderService();
            holder.tvNombre = item.findViewById(R.id.tvNombre);
            holder.tvDescripcion = item.findViewById(R.id.tvDescripcion);
            holder.tvPrecio = item.findViewById(R.id.tvPrecio);
            item.setTag(holder);

        }else{
            holder = (HolderService) item.getTag();
        }

        int mipmapid = activity.getResources().getIdentifier("red_"+lstAnuncios.get(position).mipmapid, "mipmap", activity.getPackageName());
        Drawable draw = ContextCompat.getDrawable(activity, mipmapid);
        holder.tvNombre.setCompoundDrawablesWithIntrinsicBounds(draw, null, null, null);

        String name = lstAnuncios.get(position).name;
        if(name.length() > 32 ){
            name = name.substring(0, 32).concat("...");
        }
        holder.tvNombre.setText(" "+WordUtils.capitalize(name, new char[] { '.' }));

        String description = lstAnuncios.get(position).description;
        /*if(description.length() > 32 ){
            description = description.substring(0, 32).concat("...");
        }*/
        holder.tvDescripcion.setText(WordUtils.capitalize(description, new char[] { '.' }));
        holder.tvPrecio.setText(WordUtils.capitalize(lstAnuncios.get(position).price, new char[] { '.' }));

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, AdDetail.class);
                i.putExtra("adId", lstAnuncios.get(position).id);
                activity.startActivity(i);
            }
        });

        item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String[] acciones = {"Editar", "Eliminar"};

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setItems(acciones, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ("Editar".equals(acciones[which])){
                            Intent i = new Intent(activity, AdEditarAct.class);
                            i.putExtra("adId", lstAnuncios.get(position).id);
                            activity.startActivity(i);

                        } else if ("Eliminar".equals(acciones[which])){

                            AlertDialog.Builder adb = new AlertDialog.Builder(activity);
                            adb.setTitle("MENSAJE");
                            adb.setMessage("Está a punto de eliminar este anuncio, seleccione Aceptar si está de acuerdo.");
                            adb.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "Ad a eliminar >> "+lstAnuncios.get(position).id);
                                    restfull.eliminarAnuncio(preferenceHelper.getToken(), ""+lstAnuncios.get(position).id).enqueue(new Callback<JsonObject>() {

                                        @Override
                                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                            if (response.isSuccessful()){
                                                JsonObject e = response.body();
                                                Log.d(TAG, "rpt de elminar >> "+e.toString());
                                                if(e.get("status").getAsString().equals("ok")){
                                                    Log.d(TAG, "eliminmado");
                                                    lstAnuncios.remove(position);
                                                    notifyDataSetChanged();
                                                    ViewUtils.message(activity, "El anuncio se eliminó correctamente");
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                            Crashlytics.logException(t);
                                            t.printStackTrace();
                                        }

                                    });
                                }
                            });
                            adb.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            adb.show();
                        }

                    }
                });
                builder.show();
                return true;
            }
        });

        return item;
    }

    public void onCheckListener(CheckListener checkListener) {
        this.checkListener = checkListener;
    }

    public interface CheckListener {
        void onChecked(Ad adItem);
    }

    private class HolderService {

        public TextView tvNombre;
        public TextView tvDescripcion;
        public TextView tvPrecio;

    }
}


