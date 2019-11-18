package app.conectados.android.utils;

import android.view.View;
import android.widget.RelativeLayout;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

public class MapUtil {

    public static void relocateCenterMapButton(MapView mapView, int top) {
        try {
            View locationButton = ((View) mapView.findViewById(Integer.valueOf("1")).getParent()).findViewById(Integer.valueOf("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.setMargins(0, top, 0, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void relocateCenterMapButton(View mapView) {
        try {
            View locationButton = ((View) mapView.findViewById(Integer.valueOf("1")).getParent()).findViewById(Integer.valueOf("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0, 0, 5, 5);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static double getDistance(LatLng A, LatLng B) {
        //METODO Q OBTIENE DISTANCIA ENTRE DOS PUNTOS EN METROS
        final int R = 6371;
        Double latDistance = Math.toRadians(B.latitude - A.latitude);
        Double lonDistance = Math.toRadians(B.longitude - A.longitude);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(A.latitude)) * Math.cos(Math.toRadians(B.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }

}