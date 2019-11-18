package app.conectados.android.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;

public class FacebookUtil {

    public static void getHashForFbDevelopment(Activity a){
        try {
            String namePackage = "app.conectados.android";
            PackageInfo info = a.getPackageManager().getPackageInfo(namePackage, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.e("Error:", e.getMessage());
        }
    }

}
