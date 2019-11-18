package app.conectados.android.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewUtils {

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources() .getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static int convertDpToPx(Context c, int dp){
        final float scale = c.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static void message(Context context, String mesage) {
        Toast toast = Toast.makeText(context, mesage, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static boolean existInternetConnection(Context ctx) {
        boolean result = false;
        ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        for (int i = 0; i < 2; i++) {
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                result = true;
            }
        }
        return result;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static boolean isEmail(final String hex) {
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);;
        Matcher matcher;
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    public static boolean isAlphaNumeric(final String hex) {
        final String EMAIL_PATTERN = "^[a-zA-Z0-9-ñÑ áéíóú]*$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);;
        Matcher matcher;
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    public static boolean isNumeric(final String hex) {
        final String EMAIL_PATTERN = "^[0-9]*$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);;
        Matcher matcher;
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

}