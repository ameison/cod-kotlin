package app.conectados.android.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

public class TelephonyUtil {

    public static String getSimNumber(Activity activity) {
        String simNumber = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permAccessLoc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
            if (permAccessLoc == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
                simNumber = telephonyManager.getLine1Number();
            }
        }
        return simNumber;
    }
}
