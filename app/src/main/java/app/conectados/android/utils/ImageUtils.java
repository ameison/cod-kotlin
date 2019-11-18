package app.conectados.android.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageUtils {
    // Scale and maintain aspect ratio given a desired width
    // ImageUtils.scaleToFitWidth(bitmap, 100);
    public static Bitmap scaleToFitWidth(Bitmap b, int width) {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }

    // Scale and maintain aspect ratio given a desired height
    // ImageUtils.scaleToFitHeight(bitmap, 100);
    public static Bitmap scaleToFitHeight(Bitmap b, int height) {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
    }

    public static Bitmap getBitmapFromUrl(String src) {
        String urldisplay = src;
        Bitmap bmp = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            bmp = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bmp;
    }

    public static int getWidthDisplay(Activity a){
        Display display = a.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.e("Width", "" + width);
        Log.e("height", "" + height);
        return width;
    }

    public static String encodeTobase64(Bitmap image)  {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b,Base64.DEFAULT);
    }

    public static int getMipMapId(Activity a, String mipmapName){
        return a.getResources().getIdentifier(mipmapName, "mipmap", a.getPackageName());
    }

}