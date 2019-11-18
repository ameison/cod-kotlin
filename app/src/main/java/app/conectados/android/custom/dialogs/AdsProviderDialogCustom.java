package app.conectados.android.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import app.conectados.android.R;
import app.conectados.android.models.Ad;
import app.conectados.android.models.Provider;
import app.conectados.android.custom.adapters.AdAdapter;

public class AdsProviderDialogCustom extends Dialog {

    private String TAG = getClass().getSimpleName();
    private Provider provider;
    private List<Ad> listaAds;
    public Activity mActivity;
    public TextView title;
    public TextView numStars;
    public ImageView closeBtn;
    public ListView lvAds;

    public AdsProviderDialogCustom(Activity mActivity, Provider provider, List<Ad> listaAds) {
        super(mActivity);
        this.mActivity = mActivity;
        this.provider = provider;
        this.listaAds = listaAds;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ads_provider);

        AdAdapter myAdapter = new AdAdapter(mActivity, R.layout.item_ad_map, listaAds );

        lvAds = findViewById(R.id.lvAds);
        lvAds.setAdapter(myAdapter);

        title = findViewById(R.id.title);
        title.setText(provider.firstname.split(" ")[0].concat(" ").concat(provider.surname.split(" ")[0]));

        numStars = findViewById(R.id.numStars);
        numStars.setText(provider.rating);

        closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdsProviderDialogCustom.this.cancel();
            }
        });

    }

}