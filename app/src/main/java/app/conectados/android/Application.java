package app.conectados.android;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import io.fabric.sdk.android.Fabric;


public class Application extends MultiDexApplication {

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {

        super.onCreate();

        // Crashlytics
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().build())
                //.core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        Fabric.with(this, crashlyticsKit);

        Crashlytics.log("Fabric Start !");
        Crashlytics.setUserName("Meison");
        Crashlytics.setUserEmail("masonxp@gmail.com");

        /*final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(BuildConfig.DEBUG)           // Enables Crashlytics debugger
                .build();
        Fabric.with(fabric);*/

    }

}
