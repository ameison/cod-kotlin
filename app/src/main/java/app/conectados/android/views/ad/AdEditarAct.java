package app.conectados.android.views.ad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.text.WordUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import app.conectados.android.BuildConfig;
import app.conectados.android.R;
import app.conectados.android.data.network.Restfull;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.models.Category;
import app.conectados.android.models.SubCategory;
import app.conectados.android.utils.ImageUtils;
import app.conectados.android.utils.ViewUtils;
import app.conectados.android.views.profile.ProfileAct;
import app.conectados.android.custom.dialogs.CategoryDialogRadio;
import app.conectados.android.custom.dialogs.SubCategoryDialogRadio;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdEditarAct extends AppCompatActivity {

    private String TAG = this.getClass().getName();
    private AtomicInteger at = new AtomicInteger(0);
    private int widthDisplay;
    private int adId;
    public static final int UPLOAD_AD_PICS = 4;

    private PreferenceHelper preferenceHelper;
    private Restfull restfull;
    private int contadorFotos = 0;
    private boolean subcategoryVisible = false;
    private int positionCatList = 0;
    private int positionSubCatList = 0;
    private Category categorySelected;
    private SubCategory subcategorySelected;

    @BindView(R.id.btnSelectCategory)  Button btnSelectCategory;
    @BindView(R.id.btnSelectSubCategory) Button btnSelectSubCategory;
    @BindView(R.id.txtTitleAd)  EditText txtTitleAd;
    @BindView(R.id.titleCat) TextView titleCat;
    @BindView(R.id.txtDescAd) EditText txtDescAd;
    @BindView(R.id.txtPrice) EditText txtPrice;
    @BindView(R.id.imageGallery)  LinearLayout imageGallery;
    @BindView(R.id.layoutForm)  RelativeLayout layoutForm;
    @BindView(R.id.layoutCompletarPerfil) RelativeLayout layoutCompletarPerfil;
    @BindView(R.id.btnIrPerfil) Button btnIrPerfil;
    @BindView(R.id.btnPublicarAnuncio) Button btnEnviar;

    private Map<Integer, String> mapaFotosEnBase64 = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_register);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getText(R.string.op_anunciar_edit));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            adId = bundle.getInt("adId");
        }

        preferenceHelper = new PreferenceHelper(AdEditarAct.this);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.URL_BACKEND).client(okHttpClient).
                addConverterFactory(GsonConverterFactory.create()).build();
        restfull = retrofit.create(Restfull.class);

        titleCat.setVisibility(View.GONE);

        widthDisplay = ImageUtils.getWidthDisplay(AdEditarAct.this) / 4;
        btnIrPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdEditarAct.this, ProfileAct.class));
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final ProgressDialog progressDialog;
                String message = esValidoElFormulario();
                Log.d(TAG, "message de validacion : "+ message);
                if(message.equals("")){
                    v.setEnabled(false);
                    progressDialog = ProgressDialog.show(AdEditarAct.this, null,   getString(R.string.waitPlease), true);
                    progressDialog.setCancelable(false);

                    Gson gson = new Gson();
                    JsonObject odata  = new JsonObject();

                    try{

                        String titleAd = txtTitleAd.getText().toString();
                        String descAd = txtDescAd.getText().toString();
                        String price = txtPrice.getText().toString();

                        Log.d(TAG, ">>> mapaFotosEnBase64: "+mapaFotosEnBase64.size());
                        JsonObject listaFotos = gson.toJsonTree(mapaFotosEnBase64).getAsJsonObject();

                        odata.addProperty("adId", adId);
                        odata.addProperty("titleAd", titleAd);
                        odata.addProperty("descAd", descAd);
                        odata.addProperty("price", price);
                        odata.addProperty("categoryId", categorySelected.id);

                        if(subcategoryVisible){
                            if(subcategorySelected != null){
                                odata.addProperty("sub_category_id", subcategorySelected.id);
                            }
                        }

                        odata.addProperty("provider_id", preferenceHelper.getUserId());
                        odata.add("listaFotos", listaFotos);

                        Log.d(TAG, ">>> odata : "+odata.toString());

                        restfull.editarAnuncio(preferenceHelper.getToken(), odata).enqueue(new Callback<JsonObject>() {

                            @Override
                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                                if (response.isSuccessful()) {

                                    JsonObject e = response.body();
                                    if (e.get("status").getAsString().equals("ok")){
                                        ViewUtils.message(AdEditarAct.this, "Tu anuncio se publicó exitosamente");
                                        finish();
                                    }else{
                                        ViewUtils.message(AdEditarAct.this, e.get("message").getAsString());
                                    }

                                } else {
                                    Log.e(TAG, "Error "+response.code());
                                    v.setEnabled(true);
                                    Crashlytics.log("Problema en Servidor guardando Ad  : code : "+response.code());
                                    ViewUtils.message(AdEditarAct.this, getResources().getString(R.string.problem_guardando_anuncio));
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                Crashlytics.logException(t);
                                t.printStackTrace();
                                v.setEnabled(true);
                                progressDialog.dismiss();
                                ViewUtils.message(AdEditarAct.this, getResources().getString(R.string.server_problem));
                            }
                        });

                    }catch(Exception e){
                        Crashlytics.log("Problema preparando Ad para guardado : "+e.getMessage());
                        v.setEnabled(true);
                    }

                }else{
                    ViewUtils.message(AdEditarAct.this, message);
                }

            }
        });
        layoutForm.setVisibility(View.VISIBLE);
        imageGallery.addView(addBtnUploadImage(widthDisplay));
        restfull.getCategories(preferenceHelper.getToken()).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {

                final JsonArray categories = response.body();
                final List<Category> listaCategories = parsearListaCategorias(categories);

                btnSelectCategory.setOnClickListener( new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentManager manager = getSupportFragmentManager();
                        CategoryDialogRadio alert = new CategoryDialogRadio();
                        alert.setListaCategories(listaCategories);
                        alert.setTitle(getString(R.string.seleccione_categoria));
                        alert.setAlertPositiveListener(new CategoryDialogRadio.AlertPositiveListener() {
                            @Override
                            public void onPositiveClick(Category category) {
                                categorySelected  = category;
                                btnSelectCategory.setText("\u0020\u0020".concat(category.name));

                                if(category.subCategorias != null){
                                    Log.d(TAG, "category.subCategorias.size()"+category.subCategorias.size());
                                    if(category.subCategorias.size() > 0){
                                        btnSelectSubCategory.setVisibility(View.VISIBLE);
                                        subcategoryVisible = true;
                                    }else{
                                        btnSelectSubCategory.setVisibility(View.GONE);
                                        subcategoryVisible = false;
                                    }
                                }else{
                                    btnSelectSubCategory.setVisibility(View.GONE);
                                    subcategoryVisible = false;
                                }
                            }
                        });

                        Bundle b  = new Bundle();
                        b.putInt("position", positionCatList);
                        alert.setArguments(b);
                        alert.show(manager, "alert_dialog_radio");
                    }}
                );

                btnSelectSubCategory.setOnClickListener( new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentManager manager = getSupportFragmentManager();
                        SubCategoryDialogRadio alert = new SubCategoryDialogRadio();
                        alert.setTitle(getString(R.string.que_servicio_desea_ofrecer));
                        alert.setListaSubCategories(categorySelected.subCategorias);
                        alert.setAlertPositiveListener(new SubCategoryDialogRadio.AlertPositiveListener() {
                            @Override
                            public void onPositiveClick(SubCategory subcategory) {
                                subcategorySelected = subcategory;
                                btnSelectSubCategory.setText(subcategory.name);
                            }
                        });

                        Bundle b  = new Bundle();
                        b.putInt("position", positionSubCatList);
                        alert.setArguments(b);
                        alert.show(manager, "alert_dialog_radio");
                    }}
                );

            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {  }
        });

        cargarAnuncio();
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== UPLOAD_AD_PICS && resultCode == Activity.RESULT_OK) {

            Uri selectedImage = data.getData();
            try {

                Integer idImageView = at.incrementAndGet();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(AdEditarAct.this.getContentResolver(), selectedImage);
                Bitmap scaled = ImageUtils.scaleToFitWidth(bitmap, widthDisplay * 4);
                FrameLayout flayoutImg = agregarImgFrmLay(scaled, idImageView);
                // Agregando al Layout Principal
                imageGallery.addView(flayoutImg);
                mapaFotosEnBase64.put(idImageView, ImageUtils.encodeTobase64(scaled));
                contadorFotos++;
                Log.d(TAG, "Se agrego foto : "+contadorFotos );

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public FrameLayout agregarImgFrmLay(Bitmap scaled, Integer idImageView){

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthDisplay, widthDisplay);
        FrameLayout.LayoutParams delIconParams = new FrameLayout.LayoutParams(45, 45);
        delIconParams.gravity = Gravity.END;
        delIconParams.setMargins(0, 2, 2, 0);

        ImageView imageView = new ImageView(AdEditarAct.this);
        imageView.setId(idImageView);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(scaled);
        imageView.setPadding(2,2,2,2);

        // Creando Boton eliminar
        ImageView ivDelete = new ImageView(AdEditarAct.this);
        ivDelete.setId(at.incrementAndGet());
        ivDelete.setTag(idImageView);
        ivDelete.setImageResource(R.mipmap.delete_icon);
        ivDelete.setScaleType(ImageView.ScaleType.FIT_XY);
        ivDelete.setLayoutParams(delIconParams);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout frame = AdEditarAct.this.findViewById(Integer.valueOf(v.getTag().toString()));
                imageGallery.removeView(frame);
                mapaFotosEnBase64.remove(Integer.valueOf(v.getTag().toString()));
                contadorFotos--;
            }
        });


        // Creando Framelayout
        FrameLayout flayout = new FrameLayout(AdEditarAct.this);
        flayout.setId(idImageView);
        flayout.addView(imageView);
        flayout.addView(ivDelete);
        return flayout;
    }

    public ImageView addBtnUploadImage(int widthDisplay){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthDisplay, widthDisplay);
        Bitmap bitmap = BitmapFactory.decodeResource(AdEditarAct.this.getResources(), R.mipmap.icon_subir_foto);
        Bitmap scaled = ImageUtils.scaleToFitWidth(bitmap, widthDisplay);

        ImageView imageView = new ImageView(AdEditarAct.this);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(scaled);
        imageView.setPadding(2,2,2,2);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Fotos  total : "+contadorFotos );
                if(contadorFotos < 3){
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, UPLOAD_AD_PICS);
                }else{
                    ViewUtils.message(AdEditarAct.this, getString(R.string.maximo_3_fotos));
                }
            }
        });
        return imageView;
    }

    public Map<String, String> generateMapBase64(SparseArray<Bitmap> listaFotos){
        Map<String, String> mapaFotos = new HashMap<>();
        for (int i = 0; i< listaFotos.size(); i++){
            int key = listaFotos.keyAt(i);
            Log.w(TAG, "String.valueOf(key) "+ String.valueOf(key));
            mapaFotos.put(String.valueOf(key), ImageUtils.encodeTobase64(listaFotos.get(key)));
        }
        return mapaFotos;
    }

    public String esValidoElFormulario() {
        String message = "";

        if(categorySelected == null){
            return "Debes seleccionar una categoría";
        }

        if(subcategoryVisible){
            if(subcategorySelected== null){
                return "Debes seleccionar una subcategoría";
            }
        }

        if(txtTitleAd.getText().toString().trim().length() < 5 ){
            return "Debes ingresar un titulo de anuncio mayor a 5 caracteres";
        }
        if(txtDescAd.getText().toString().trim().length() < 10 ){
            return "Debes ingresar una descripción de anuncio mayor a 10 caracteres";
        }

        if(txtPrice.getText().toString().trim().length() < 4 ){
            return "Debes especificar el precio (4 caracteres como mínimo)";
        }

        if(contadorFotos == 0){
            return "Debes ingresar al menos una foto";
        }

        return message;
    }


    private List<Category> parsearListaCategorias(JsonArray categories){
        List<Category> listaCategories = new ArrayList<>();
        for (JsonElement e : categories){
            JsonObject jObject = e.getAsJsonObject();
            Category c = new Category();
            c.id = jObject.get("id").getAsInt();
            c.name = jObject.get("name").getAsString();
            c.mipmapName = jObject.get("mipmapid").getAsString();
            //c.mipmapName = mActivity.getResources().getIdentifier(mipmapName, "mipmap", mActivity.getPackageName());

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


    public void cargarAnuncio(){
        Map<String, String> map = new HashMap<>();
        map.put("op", "get-ad");
        map.put("ad_id", String.valueOf(adId));
        restfull.getAnuncioDelProveedor(preferenceHelper.getToken(), map).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject xdata = response.body();
                    Log.d(TAG, "xdata >>> ads_detail >>> "+xdata);
                    if (xdata.has("id")) {
                        String name = xdata.get("name").getAsString();
                        String description = xdata.get("description").getAsString();
                        String price = xdata.get("price").getAsString();

                        //Agregando categoria y subcategoria
                        categorySelected = new Category();
                        categorySelected.id = xdata.get("categoryId").getAsInt();
                        categorySelected.name = xdata.get("category_name").getAsString();
                        btnSelectCategory.setText("\u0020\u0020".concat(categorySelected.name));

                        int idValidate = xdata.get("sub_category_id").getAsInt();
                        Log.d(TAG, "idValidate : "+idValidate);
                        if(idValidate != 0){
                            subcategorySelected = new SubCategory();
                            subcategorySelected.id = xdata.get("sub_category_id").getAsInt();
                            subcategorySelected.name = xdata.get("sub_category_name").getAsString();
                            btnSelectSubCategory.setVisibility(View.VISIBLE);
                            btnSelectSubCategory.setText("\u0020\u0020".concat(subcategorySelected.name));
                        }

                        txtTitleAd.setText(WordUtils.capitalize(name, new char[]{'.'}));
                        txtDescAd.setText(WordUtils.capitalize(description, new char[]{'.'}));
                        txtPrice.setText(price);

                        JsonArray pics = xdata.get("pics").getAsJsonArray();
                        for (JsonElement pathImg : pics) {
                            Log.d(TAG, "******* "+pathImg.getAsString());
                            AsyncTaskRunner runner = new AsyncTaskRunner();
                            runner.execute(pathImg.getAsString());
                        }
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Crashlytics.logException(t);
                t.printStackTrace();
                ViewUtils.message(AdEditarAct.this, getResources().getString(R.string.server_problem));
            }

        });
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, Bitmap> {

        private ProgressDialog progressDialog = new ProgressDialog(AdEditarAct.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.waitPleaseLoadImage));
            progressDialog.show();
            progressDialog.setCancelable(false);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String pathImg = params[0];
            Bitmap bitmap = ImageUtils.getBitmapFromUrl(pathImg);
            Log.d(TAG, "Se transformo la imagen a mapa de Bits");
            bitmap = ImageUtils.scaleToFitWidth(bitmap, widthDisplay * 4);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap scaled) {
            Integer idImageView = at.incrementAndGet();
            FrameLayout flayoutImg = agregarImgFrmLay(scaled, idImageView);
            imageGallery.addView(flayoutImg);
            mapaFotosEnBase64.put(idImageView, ImageUtils.encodeTobase64(scaled));
            contadorFotos++;
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "Se agrego foto : "+contadorFotos );
        }



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

}
