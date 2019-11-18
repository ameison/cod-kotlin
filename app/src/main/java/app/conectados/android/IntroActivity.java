package app.conectados.android;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

import app.conectados.android.data.prefs.PreferenceHelper;
import app.conectados.android.views.index.IndexActivity;
import app.conectados.android.views.MainActivity;
import app.conectados.android.views.onboarder.OnboarderActivity;
import app.conectados.android.views.onboarder.OnboarderPage;

public class IntroActivity extends OnboarderActivity {

    private final String TAG = getClass().getSimpleName();
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications en versiones mayores a Android 8.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        preferenceHelper = new PreferenceHelper(this);
        if(preferenceHelper.getToken() != null){
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else{
            preferenceHelper = new PreferenceHelper(this);


            List<OnboarderPage> pages = Arrays.asList(
                    new OnboarderPage.Builder()
                            .titleResourceId(R.string.productos_servicios)
                            .descriptionResourceId(R.string.productos_servicios_desc)
                            .imageResourceId(R.mipmap.feature_servicios)
                            .backgroundColor(R.color.cns_blanco)
                            .titleColor(R.color.cns_celeste)
                            .descriptionColor(R.color.cns_azul)
                            .descriptionTextSize(16f)
                            .multilineDescriptionCentered(true)
                            .build(),

                    new OnboarderPage.Builder()
                            .titleResourceId(R.string.compras)
                            .descriptionResourceId(R.string.compras_desc)
                            .imageResourceId( R.mipmap.feature_compras)
                            .backgroundColor(R.color.cns_blanco)
                            .titleColor(R.color.cns_celeste)
                            .descriptionColor(R.color.cns_azul)
                            .descriptionTextSize(16f)
                            .multilineDescriptionCentered(true)
                            .build(),

                    new OnboarderPage.Builder()
                            .titleResourceId(R.string.alquilres_y_laborales)
                            .descriptionResourceId(R.string.alquilres_y_laborales_desc)
                            .imageResourceId( R.mipmap.feature_alquileres)
                            .backgroundColor(R.color.cns_blanco)
                            .titleColor(R.color.cns_celeste)
                            .descriptionColor(R.color.cns_azul)
                            .descriptionTextSize(16f)
                            .multilineDescriptionCentered(true)
                            .build(),


                    new OnboarderPage.Builder()
                            .imageResourceId( R.mipmap.feature_obsequios)
                            .titleResourceId(R.string.obsequios)
                            .titleColor(R.color.cns_celeste)
                            .descriptionResourceId(R.string.obsequios_desc)
                            .descriptionColor(R.color.cns_azul)
                            .descriptionTextSize(16f)
                            .backgroundColor(R.color.cns_blanco)
                            .multilineDescriptionCentered(true)
                            .build()
            );

            setActiveIndicatorColor(R.color.circulo_seleccionado);
            setInactiveIndicatorColor(R.color.circulo_normal);
            setSkipButtonTitle(R.string.omitir);
            setNextButtonTitle(R.string.siguiente);
            setFinishButtonTitle(R.string.finalizar);
            setFinishButtonTextColor(R.color.cns_celeste);
            setNextButtonTextColor(R.color.cns_celeste);
            setSkipButtonTextColor(R.color.cns_celeste);
            initOnboardingPages(pages);

        }

    }

    @Override
    public void onSkipButtonPressed() {
        super.onSkipButtonPressed();
        Intent intent = new Intent(getBaseContext(), IndexActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFinishButtonPressed() {
        Intent intent = new Intent(getBaseContext(), IndexActivity.class);
        startActivity(intent);
        finish();
    }



}