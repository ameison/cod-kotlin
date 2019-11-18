package app.conectados.android.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionUtil {

    public static final int PERMISSIONS_FINE_LOCATION= 1;
    public static final int PERMISSIONS_WRITE_STORAGE = 2;
    public static final int PERMISSIONS_CALL_PHONE = 3;
    public static final int PERMISSIONS_CALENDAR = 4;
    public static final int PERMISSIONS_ACCOUNTS = 5;
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 12345;

    public void requestPermission(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //boolean ac = activity.checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED;
            boolean ca = activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED;
            boolean al = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
            boolean ws = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
            boolean cp = activity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED;


            if(al)
            //if(al && ws && cp && ca && ac)
                activity.requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        //Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        //Manifest.permission.GET_ACCOUNTS,
                        //Manifest.permission.CALL_PHONE,
                        //Manifest.permission.READ_PHONE_STATE
                }, PERMISSIONS_MULTIPLE_REQUEST);

            if(al)
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);

            /*if(ws)
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_WRITE_STORAGE);

            if(cp)
                activity.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_CALL_PHONE);

            if(ca)
                activity.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_CALENDAR);

            if(ac)
                activity.requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, PERMISSIONS_ACCOUNTS);
            */
        }
    }
}
