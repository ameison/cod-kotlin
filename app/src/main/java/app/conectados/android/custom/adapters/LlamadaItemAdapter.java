
package app.conectados.android.custom.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import app.conectados.android.BuildConfig;
import app.conectados.android.R;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.models.Llamada;
import app.conectados.android.data.network.Restfull;
import org.apache.commons.text.WordUtils;
import java.util.List;

import app.conectados.android.models.Mensaje;
import app.conectados.android.utils.PermissionUtil;
import app.conectados.android.views.messages.EnviarMensajeAct;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LlamadaItemAdapter extends ArrayAdapter<Llamada> {

    private final String TAG = AdAdapter.class.getSimpleName();
    private int layout;
    private Activity activity;
    private Restfull restfull;
    private List<Llamada> llamadas;
    private PreferenceHelper preferenceHelper;

    public LlamadaItemAdapter(Activity activity, int layout, List<Llamada> lstAnuncios) {
        super(activity, layout, lstAnuncios);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        this.activity = activity;
        this.layout = layout;
        this.llamadas = lstAnuncios;
        this.preferenceHelper = new PreferenceHelper(activity);
        this.restfull = retrofit.create(Restfull.class);
    }


    @Override
    public int getCount() {
        return llamadas.size();
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
            holder.tvProvider = item.findViewById(R.id.tvProvider);
            holder.tvDescripcion = item.findViewById(R.id.tvDescripcion);
            holder.tvPrecio = item.findViewById(R.id.tvPrecio);
            holder.btnLlamar = item.findViewById(R.id.btnLlamar);
            item.setTag(holder);

        } else {
            holder = (HolderService) item.getTag();
        }


        String providerName = llamadas.get(position).provider_name;
        holder.tvProvider.setText(WordUtils.capitalize(providerName, new char[]{'.'}));

        String name = llamadas.get(position).ad_name;
        if (name.length() > 32) {  name = name.substring(0, 32).concat("...");  }
        holder.tvDescripcion.setText(WordUtils.capitalize(name, new char[]{'.'}));

        holder.tvPrecio.setText(WordUtils.capitalize(llamadas.get(position).ad_price, new char[]{'.'}));
        holder.btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permAccessLoc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
                    if (permAccessLoc == PackageManager.PERMISSION_GRANTED) {
                        String uri = "tel:" +llamadas.get(position).provider_phone ;
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse(uri));
                        activity.startActivity(intent);
                    }else{
                        Log.d(TAG, "No tiene permiso de acceso a ubicacion");
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE},
                                PermissionUtil.MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    }
                }

            }
        });
        return item;
    }

    private class HolderService {

        public TextView tvProvider;
        public TextView tvDescripcion;
        public TextView tvPrecio;
        public ImageView btnLlamar;

    }

    public static class MensajeItemAdapter extends ArrayAdapter<Mensaje> {

        private final String TAG = AdAdapter.class.getSimpleName();
        private int layout;
        private Activity activity;
        private Restfull restfull;
        private List<Mensaje> lstMensajes;
        private PreferenceHelper preferenceHelper;

        public MensajeItemAdapter(Activity activity, int layout, List<Mensaje> lstAnuncios) {
            super(activity, layout, lstAnuncios);
            Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
            this.activity = activity;
            this.layout = layout;
            this.lstMensajes = lstAnuncios;
            this.preferenceHelper = new PreferenceHelper(activity);
            this.restfull = retrofit.create(Restfull.class);
        }


        @Override
        public int getCount() {
            return lstMensajes.size();
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
                holder.tvProvider = item.findViewById(R.id.tvProvider);
                holder.tvAdName = item.findViewById(R.id.tvAdName);
                holder.tvMensaje = item.findViewById(R.id.tvMensaje);
                holder.tvFecha = item.findViewById(R.id.tvFecha);
                item.setTag(holder);

            } else {
                holder = (HolderService) item.getTag();
            }


            String providerName = "";
            if(lstMensajes.get(position).providerId == Integer.parseInt(preferenceHelper.getUserId())){
                providerName = lstMensajes.get(position).customerName;
            }else{
                providerName = lstMensajes.get(position).providerName;
            }

            holder.tvProvider.setText(WordUtils.capitalize(providerName, new char[]{'.'}));

            String adName = lstMensajes.get(position).ad.name;
            if (adName.length() > 32) {  adName = adName.substring(0, 32).concat("...");  }
            holder.tvAdName.setText(WordUtils.capitalize(adName, new char[]{'.'}));

            holder.tvMensaje.setText(WordUtils.capitalize(lstMensajes.get(position).body, new char[]{'.'}));
            holder.tvFecha.setText(WordUtils.capitalize(lstMensajes.get(position).date, new char[]{'.'}));

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, EnviarMensajeAct.class);
                    i.putExtra("adId", lstMensajes.get(position).ad.id);
                    i.putExtra("providerId", lstMensajes.get(position).providerId);
                    i.putExtra("customerId", lstMensajes.get(position).customerId);

                    Log.d(TAG, "providerName >> "+lstMensajes.get(position).providerName);
                    Log.d(TAG, "customerName >> "+lstMensajes.get(position).customerName);

                    Log.d(TAG, "lstMensajes.get(position).providerId >> "+lstMensajes.get(position).providerId);
                    Log.d(TAG, "Integer.parseInt(preferenceHelper.getUserId() >> "+Integer.parseInt(preferenceHelper.getUserId()));
                    Log.d(TAG, "Integer.parseInt(preferenceHelper.getUserId() >> "+(lstMensajes.get(position).providerId == Integer.parseInt(preferenceHelper.getUserId())));

                    if(lstMensajes.get(position).providerId == Integer.parseInt(preferenceHelper.getUserId())){
                        i.putExtra("fullNameProvider", lstMensajes.get(position).customerName);
                    }else{
                        i.putExtra("fullNameProvider", lstMensajes.get(position).providerName);
                    }
                    activity.startActivity(i);
                }
            });
            return item;
        }

        private class HolderService {

            public TextView tvProvider;
            public TextView tvAdName;
            public TextView tvMensaje;
            public TextView tvFecha;

        }
    }
}


