package app.conectados.android.utils;

public final class AppConstants {

    public static final int GPS_REQUEST = 1200;
    public static final int LOCATION_REQUEST = 1201;

    public static final String STATUS_CODE_SUCCESS = "success";
    public static final String STATUS_CODE_FAILED = "failed";
    public static final int API_STATUS_CODE_LOCAL_ERROR = 0;
    public static final String DB_NAME = "mindorks_mvp.db";
    public static final String PREF_NAME = "mindorks_pref";
    public static final long NULL_INDEX = -1L;
    public static final String SEED_DATABASE_OPTIONS = "seed/options.json";
    public static final String SEED_DATABASE_QUESTIONS = "seed/questions.json";
    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
    public static final int UPLOAD_AD_PICS = 4;

    // Bradcasts
    public static String BROADCAST_CERRAR_PANTALLA_LOGIN = "com.abcdroid.broadcast.cerrar_ventana_login";
    public static String BROADCAST_DEVOLVER_BANDERA = "com.abcdroid.broadcast.getbandera";

    private AppConstants() {
        // This utility class is not publicly instantiable
    }

}
