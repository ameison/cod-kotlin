package app.conectados.android.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;

import app.conectados.android.BuildConfig;
import app.conectados.android.GlideApp;
import app.conectados.android.R;
import app.conectados.android.data.network.Restfull;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.utils.AppConstants;
import app.conectados.android.utils.ViewUtils;
import app.conectados.android.views.about.AboutAct;
import app.conectados.android.views.ad.AdRegisterAct;
import app.conectados.android.views.ad.MyAdsAct;
import app.conectados.android.views.calls.LlamadasAct;
import app.conectados.android.views.map.MapAdsFr;

import org.apache.commons.text.WordUtils;

import java.util.HashMap;
import java.util.Map;

import app.conectados.android.views.messages.MensajesAct;
import app.conectados.android.views.profile.ProfileAct;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    private Restfull restfull;

    private ObjectDrawerItem[] drawerItem;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.iconoMenu) ImageView iconoMenu;
    @BindView(R.id.left_drawer) ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;
    public static final int FRAGMENT_INICIO = 0;
    public static final int FRAGMENT_ANUNCIAR = 1;
    public static final int FRAGMENT_MISANUNCIOS = 2;
    public static final int FRAGMENT_HISTORY = 3;
    public static final int FRAGMENT_MENSAJES = 4;
    public static final int FRAGMENT_PERFIL = 5;
    public static final int FRAGMENT_ABOUT = 6;

    public static final int REQUEST_EXIT = 100;

    private Fragment mapFragment = null;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        preferenceHelper = new PreferenceHelper(this);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setLogo(null);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).addConverterFactory(GsonConverterFactory.create()).build();
        restfull = retrofit.create(Restfull.class);

        ajustarMenu();
        cargarMenu();

        if (savedInstanceState == null) {
            selectItem(FRAGMENT_INICIO, "");
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    private ViewGroup cargarCabeceraMenu() {
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_menu, mDrawerList, false);

        Map<String, String> map = new HashMap<>();
        map.put("op", "get-provider");
        map.put("idx", preferenceHelper.getUserId());


        restfull.getPerfilProveedor( preferenceHelper.getToken(), map).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if(response.isSuccessful()){
                    JsonObject xdata = response.body();
                    if(xdata.has("id")){
                        String firstname = xdata.get("firstname").getAsString();
                        String surname = xdata.get("surname").getAsString();
                        TextView tvNameUser = header.findViewById(R.id.tvNameUser);
                        TextView tvFleetAdmin = header.findViewById(R.id.tvFleetAdmin);
                        tvNameUser.setText(WordUtils.capitalizeFully( firstname));
                        tvFleetAdmin.setText(surname);
                        Crashlytics.setUserIdentifier(preferenceHelper.getUserId());

                    }else{
                        Log.w(TAG, "No se recibio datos de proveedor" );
                    }

                    CircleImageView imgPhotoClient = header.findViewById(R.id.imgPhotoClient);

                    if(xdata.has("photo")){
                        String photo = xdata.get("photo").getAsString();

                        RequestOptions myOptions = new RequestOptions()
                                //.transforms(new RoundedCorners(50))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true);

                        GlideApp.with(MainActivity.this)
                                .applyDefaultRequestOptions(myOptions)
                                .load(photo)
                                .placeholder(R.mipmap.giphy)
                                .into(imgPhotoClient) ;

                    }else{
                        imgPhotoClient.setImageResource(R.mipmap.icomenu_user);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

            }

        });

        return header;
    }

    private void cargarMenu(){
        LayoutInflater inflater = getLayoutInflater();

        // Headet
        mDrawerList.addHeaderView(cargarCabeceraMenu(), null, false);

        // Center
        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.item_main_menu, customMenu());
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Footer
        final ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer_menu, mDrawerList, false);
        Button btnDestacaTuAnuncio = footer.findViewById(R.id.btnDestacaTuAnuncio);
        Button btnCompraMasanuncios = footer.findViewById(R.id.btnCompraMasanuncios);
        btnDestacaTuAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.message(MainActivity.this, getString(R.string.msg_pronto_destaca_anuncios));
            }
        });

        btnCompraMasanuncios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.message(MainActivity.this, getString(R.string.msg_pronto_compra_anuncios));
            }
        });
        mDrawerList.addFooterView(footer);
    }

    private void ajustarMenu() {
        int width = (getResources().getDisplayMetrics().widthPixels / 8) * 6;
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mDrawerList.getLayoutParams();
        params.width = width;
        mDrawerList.setLayoutParams(params);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(drawerItem[position - 1].id, drawerItem[position - 1].name);
        }
    }

    private void setTitleActionBar(String title){
        if(getSupportActionBar() != null){
            Typeface typeface =  ResourcesCompat.getFont(this, R.font.font_quicksand);
            SpannableString s = new SpannableString(title);
            s.setSpan(typeface, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(s);
            iconoMenu.setVisibility(View.GONE);
        }
    }

    public void selectItem(int position, String title) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position) {
            case FRAGMENT_INICIO:
                mapFragment = new MapAdsFr();
                fragmentManager.beginTransaction().replace(R.id.container, mapFragment, MapAdsFr.class.getSimpleName()).commit();
                fragmentManager.beginTransaction().addToBackStack(MapAdsFr.class.getSimpleName()).commit();
                setTitleActionBar("");
                iconoMenu.setVisibility(View.VISIBLE);
                break;
            case FRAGMENT_ANUNCIAR:
                startActivity(new Intent(this, AdRegisterAct.class));
                break;
            case FRAGMENT_MISANUNCIOS:
                startActivity(new Intent(this, MyAdsAct.class));
                break;
            case FRAGMENT_HISTORY:
                startActivity(new Intent(this, LlamadasAct.class));
                break;
            case FRAGMENT_MENSAJES:
                startActivity(new Intent(this, MensajesAct.class));
                break;
            case FRAGMENT_PERFIL:
                startActivityForResult(new Intent(this, ProfileAct.class), REQUEST_EXIT);
                break;
            case FRAGMENT_ABOUT:
                startActivity(new Intent(this, AboutAct.class));
                break;
            default:
                break;
        }

        if (mapFragment != null) {
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "MainActivity - requestCode :: "+requestCode);
        Log.d(TAG, "MainActivity - resultCode :: "+resultCode);

        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_EXIT )  {
                this.finish();
            }else if (requestCode == AppConstants.GPS_REQUEST) {
                mapFragment.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!ViewUtils.existInternetConnection(this)){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.message);
            builder.setMessage(R.string.internet_cnx_error);
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.agreed).toUpperCase(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }});

            builder.show();
        }

    }

    public static class ObjectDrawerItem {
        public int id;
        public String name;
        public int icon;

        private ObjectDrawerItem(int id, String name, int icon) {
            this.id = id;
            this.icon = icon;
            this.name = name;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public ObjectDrawerItem[] customMenu() {
        String activeMenuItems = getResources().getString(R.string.active_menuitems);
        String[] totalMenuItems = getResources().getStringArray(R.array.navigation_items_array);
        drawerItem = new ObjectDrawerItem[activeMenuItems.length()];
        int idx = 0;
        for (String item : totalMenuItems){
            String[] detItem = item.split(",");
            String id = detItem[0];
            String name = detItem[1];
            String icon = detItem[2];
            if(activeMenuItems.contains(id)){
                int resId = getResources().getIdentifier(icon, "mipmap", getPackageName());
                drawerItem[idx] = new ObjectDrawerItem(Integer.parseInt(id), name, resId);
                idx++;
            }
        }
        return drawerItem;
    }


    private class DrawerItemCustomAdapter extends ArrayAdapter<ObjectDrawerItem> {

        private Context mContext;
        private int layoutResourceId;
        private ObjectDrawerItem data[] = null;

        private DrawerItemCustomAdapter(Context mContext, int layoutResourceId, ObjectDrawerItem[] data) {

            super(mContext, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.data = data;
        }

        @Override
        public View getView(@NonNull int position, @NonNull View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            ImageView imageViewIcon = convertView.findViewById(R.id.imageViewIcon);
            TextView textViewName =  convertView.findViewById(R.id.textViewName);
            ObjectDrawerItem folder = data[position];
            try {
                imageViewIcon.setImageResource(folder.icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
            textViewName.setText(folder.name);

            return convertView;
        }

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Finalizando activity ");
        finish();
    }

}