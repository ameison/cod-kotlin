package app.conectados.android.views.ad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import app.conectados.android.R;
import app.conectados.android.models.Ad;
import app.conectados.android.models.Category;
import app.conectados.android.models.SubCategory;
import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.utils.AppConstants;
import app.conectados.android.utils.ImageUtils;
import app.conectados.android.utils.ViewUtils;
import app.conectados.android.views.profile.ProfileAct;
import app.conectados.android.custom.dialogs.CategoryDialogRadio;
import app.conectados.android.custom.dialogs.SubCategoryDialogRadio;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdRegisterAct extends AppCompatActivity implements AdRegisterActPresenter.View{

    private String TAG = this.getClass().getName();
    private AtomicInteger at = new AtomicInteger(0);
    private int widthDisplay;

    private Ad ad = new Ad();
    private Category categorySelected = new Category();
    private SubCategory subcategorySelected = new SubCategory();

    private AdRegisterActPresenter presenter;

    private int contadorFotos = 0;
    private boolean subcategoryVisible = false;
    private int positionCatList = 0;
    private int positionSubCatList = 0;

    private ProgressDialog progressDialog;

    @BindView(R.id.btnSelectCategory)  Button btnSelectCategory;
    @BindView(R.id.btnSelectSubCategory) Button btnSelectSubCategory;
    @BindView(R.id.txtTitleAd)  EditText txtTitleAd;
    @BindView(R.id.txtDescAd) EditText txtDescAd;
    @BindView(R.id.txtPrice) EditText txtPrice;
    @BindView(R.id.imageGallery)  LinearLayout imageGallery;
    @BindView(R.id.layoutForm)  RelativeLayout layoutForm;
    @BindView(R.id.layoutCompletarPerfil) RelativeLayout layoutCompletarPerfil;
    @BindView(R.id.btnIrPerfil) Button btnIrPerfil;
    @BindView(R.id.btnPublicarAnuncio) Button btnPublicarAnuncio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_register);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getText(R.string.op_anunciar));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        presenter = new AdRegisterActPresenter(this, new PreferenceHelper(this));
        presenter.validarFotoyLocsEnPerfil();
        presenter.requestCategorias();

        widthDisplay = ImageUtils.getWidthDisplay(this) / 4;
        imageGallery.addView(addBtnUploadImage(widthDisplay));

    }

    @Override
    public void isValidForSellerProfile(boolean result) {
        if(result){
            layoutForm.setVisibility(android.view.View.VISIBLE);
            layoutCompletarPerfil.setVisibility(android.view.View.GONE);
        }else{
            layoutForm.setVisibility(android.view.View.GONE);
            layoutCompletarPerfil.setVisibility(android.view.View.VISIBLE);
        }
    }

    @Override
    public void showCategories(final List<Category> listaCategories) {

        btnSelectCategory.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(android.view.View v) {
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
                                btnSelectSubCategory.setVisibility(android.view.View.VISIBLE);
                                subcategoryVisible = true;
                            }else{
                                btnSelectSubCategory.setVisibility(android.view.View.GONE);
                                subcategoryVisible = false;
                            }
                        }else{
                            btnSelectSubCategory.setVisibility(android.view.View.GONE);
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

        btnSelectSubCategory.setOnClickListener( new android.view.View.OnClickListener() {

            @Override
            public void onClick(android.view.View v) {
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
    public void registerAdSuccessfull() {
        finish();
    }

    @Override
    public void showToastMessage(int idMessage) {
        Toast.makeText(this, getString(idMessage), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBar() {
        //progressBar.setVisibility(View.VISIBLE);
        progressDialog = ProgressDialog.show(this, null, getString(R.string.waitPlease), true);
        progressDialog.setCancelable(false);
    }

    @Override
    public void hideProgressBar() {
        //progressBar.setVisibility(View.INVISIBLE);
        progressDialog.dismiss();
    }

    @OnClick(R.id.btnPublicarAnuncio)
    public void onPublicarAnuncio(){
        // ¿Es necesario bloquear el boton para evitar 2 clicks ??
        ad.name = txtTitleAd.getText().toString();
        ad.description = txtDescAd.getText().toString();
        ad.price = txtPrice.getText().toString();
        ad.categoryId = categorySelected.id;
        ad.subCategoryId = subcategorySelected.id;

        if(ad.isValidForm().equals("true")){
            presenter.registerAd(ad);
        }else{
            ViewUtils.message(this, ad.isValidForm());
        }
    }

    @OnClick(R.id.btnIrPerfil)
    public void onGoProfile(){
        startActivity(new Intent(this, ProfileAct.class));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== AppConstants.UPLOAD_AD_PICS && resultCode == Activity.RESULT_OK) {

            Uri selectedImage = data.getData();
            try {

                Integer idImageView = at.incrementAndGet();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(AdRegisterAct.this.getContentResolver(), selectedImage);
                Bitmap scaled = ImageUtils.scaleToFitWidth(bitmap, widthDisplay * 4);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthDisplay, widthDisplay);
                FrameLayout.LayoutParams delIconParams = new FrameLayout.LayoutParams(45, 45);
                delIconParams.gravity = Gravity.END;
                delIconParams.setMargins(0, 2, 2, 0);

                ImageView imageView = new ImageView(AdRegisterAct.this);
                imageView.setId(idImageView);
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageBitmap(scaled);
                imageView.setPadding(2,2,2,2);

                // Creando Boton eliminar
                ImageView ivDelete = new ImageView(AdRegisterAct.this);
                ivDelete.setId(at.incrementAndGet());
                ivDelete.setTag(idImageView);
                ivDelete.setImageResource(R.mipmap.delete_icon);
                ivDelete.setScaleType(ImageView.ScaleType.FIT_XY);
                ivDelete.setLayoutParams(delIconParams);
                ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FrameLayout frame = AdRegisterAct.this.findViewById(Integer.valueOf(v.getTag().toString()));
                        imageGallery.removeView(frame);
                        ad.picsBase64.remove(Integer.valueOf(v.getTag().toString()));
                        contadorFotos--;
                    }
                });


                // Creando Framelayout
                FrameLayout flayout = new FrameLayout(AdRegisterAct.this);
                flayout.setId(idImageView);
                flayout.addView(imageView);
                flayout.addView(ivDelete);

                // Agregando al Layout Principal
                imageGallery.addView(flayout);
                ad.picsBase64.put(idImageView, ImageUtils.encodeTobase64(scaled));
                contadorFotos++;
                Log.d(TAG, "Se agrego foto : "+contadorFotos );


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ImageView addBtnUploadImage(int widthDisplay){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthDisplay, widthDisplay);
        Bitmap bitmap = BitmapFactory.decodeResource(AdRegisterAct.this.getResources(), R.mipmap.icon_subir_foto);
        Bitmap scaled = ImageUtils.scaleToFitWidth(bitmap, widthDisplay);

        ImageView imageView = new ImageView(AdRegisterAct.this);
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
                    startActivityForResult(intent, AppConstants.UPLOAD_AD_PICS);
                }else{
                    ViewUtils.message(AdRegisterAct.this, getString(R.string.maximo_3_fotos));
                }
            }
        });
        return imageView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
