package app.conectados.android.views.ad;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import app.conectados.android.GlideApp;
import app.conectados.android.R;
import app.conectados.android.utils.ViewUtils;
import app.conectados.android.custom.listeners.OnSwipeTouchListener;
import butterknife.BindView;
import butterknife.ButterKnife;


public class AdImageSwipe extends AppCompatActivity {

    private String TAG = AdImageSwipe.class.getName();
    private ArrayList<String> listaImagenes;
    private Integer position;
    @BindView(R.id.imagen) ImageView imageView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_image_swipe);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setLogo(null);
        getSupportActionBar().setTitle("Imágenes del Anuncio");

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            listaImagenes = bundle.getStringArrayList("imagenes");
            position = bundle.getInt("position");
            Log.d(TAG, "posicion swipe ini : "+position);
            if(listaImagenes.size() > 0){
                imageView.setOnTouchListener(new OnSwipeTouchListener(AdImageSwipe.this) {
                    public void onSwipeTop() { }
                    public void onSwipeBottom() {  }
                    public void onSwipeRight() {
                        position --;
                        Log.d(TAG, "posicion swipe left : "+position);
                        Log.d(TAG, "posicion swipe left list : "+listaImagenes.size());

                        Context ctx = AdImageSwipe.this;
                        if(position >= 0){

                            if(ViewUtils.isValidContextForGlide(ctx)){
                                GlideApp.with(ctx)
                                        .load(listaImagenes.get(position))
                                        .placeholder(R.mipmap.giphy)
                                        .into(imageView) ;
                            }

                        }else{
                            position = listaImagenes.size() - 1;

                            if(ViewUtils.isValidContextForGlide(ctx)){
                                GlideApp.with(ctx)
                                        .load(listaImagenes.get(position))
                                        .placeholder(R.mipmap.giphy)
                                        .into(imageView) ;
                            }

                        }
                    }
                    public void onSwipeLeft() {
                        position ++;
                        Log.d(TAG, "posicion swipe right : "+position);
                        Log.d(TAG, "posicion swipe right list : "+listaImagenes.size());
                        if(position < listaImagenes.size()){
                            Context ctx = AdImageSwipe.this;
                            if(ViewUtils.isValidContextForGlide(ctx)){
                                GlideApp.with(ctx)
                                        .load(listaImagenes.get(position))
                                        .placeholder(R.mipmap.giphy)
                                        .into(imageView) ;
                            }
                        }else{
                            position = 0;

                            Context ctx = AdImageSwipe.this;
                            if(ViewUtils.isValidContextForGlide(ctx)){
                                GlideApp.with(ctx)
                                        .load(listaImagenes.get(position))
                                        .placeholder(R.mipmap.giphy)
                                        .into(imageView) ;
                            }
                        }
                    }
                });

                Context ctx = AdImageSwipe.this;
                if(ViewUtils.isValidContextForGlide(ctx)){
                    GlideApp.with(ctx)
                            .load(listaImagenes.get(position))
                            .placeholder(R.mipmap.giphy)
                            .into(imageView) ;
                }
            }

        }else{
            Log.d(TAG, "Bundle es nulo");
        }

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
