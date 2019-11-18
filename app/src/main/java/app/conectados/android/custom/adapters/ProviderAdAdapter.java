package app.conectados.android.custom.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.apache.commons.text.WordUtils;
import java.util.List;
import app.conectados.android.R;
import app.conectados.android.models.Ad;
import app.conectados.android.views.ad.AdDetail;

public class ProviderAdAdapter extends ArrayAdapter<Ad> {
    private String TAG = ProviderAdAdapter.class.getName();
    private int layout;
    private Activity activity;
    private List<Ad> lstAnuncios;

    public ProviderAdAdapter(Activity activity, int layout, List<Ad> lstAnuncios){
        super(activity, layout, lstAnuncios);
        this.activity = activity;
        this.layout = layout;
        this.lstAnuncios = lstAnuncios;
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

        holder.tvNombre.setText(WordUtils.capitalize(lstAnuncios.get(position).name));
        holder.tvDescripcion.setText(WordUtils.capitalize(lstAnuncios.get(position).description));
        holder.tvPrecio.setText(WordUtils.capitalize(lstAnuncios.get(position).price));
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, AdDetail.class);
                i.putExtra("adId", lstAnuncios.get(position).id);
                activity.startActivity(i);
            }
        });

        return item;
    }

    private class HolderService {

        public TextView tvNombre;
        public TextView tvDescripcion;
        public TextView tvPrecio;

    }
}


