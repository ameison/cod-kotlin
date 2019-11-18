package app.conectados.android;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import app.conectados.android.views.messages.EnviarMensajeAct;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private static final String LOGTAG = "android-fcm";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("GCM - ", "onMessageReceived ");
        Log.d("GCM - ", "getData().getData() "+ remoteMessage.getData());
        Map<String, String> mainObject = remoteMessage.getData();

        Log.d("GCM - ", "mainObject() ");
        String senderName = mainObject.get("sender_name");
        String message = mainObject.get("body");
        String ads_id = mainObject.get("ads_id");
        String provider_id = mainObject.get("provider_id");
        String customer_id = mainObject.get("customer_id");

        Log.d("GCM - ", "ads_id : "+ads_id);
        Log.d("GCM - ", "provider_id : "+provider_id);
        Log.d("GCM - ", "customer_id : "+customer_id);

        Intent ix = new Intent(this, EnviarMensajeAct.class);
        ix.putExtra("fullNameProvider", senderName);
        ix.putExtra("adId", Integer.parseInt(ads_id));
        ix.putExtra("providerId", Integer.parseInt(provider_id));
        ix.putExtra("customerId", Integer.parseInt(customer_id));

        Activity a = getForegroundActivity();
        if(a != null){
            if(a.getLocalClassName().equalsIgnoreCase("views.activities.EnviarMensajeAct")){
                Intent intent = new Intent("send-provider-message");
                intent.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            } else{
                showNotification(senderName, message, ix, 0);
            }
        }else {
            showNotification(senderName, message, ix, 0);
        }

    }


    public void showNotification(String title, String message, Intent intent, int notificationId) {



        PendingIntent pendingIntent = null;
        if(intent != null){
            pendingIntent = PendingIntent.getActivity( this,  notificationId,  intent,  PendingIntent.FLAG_UPDATE_CURRENT   );
        }

        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder = builder
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        builder.setSmallIcon(R.drawable.ic_stat_name);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(getResources().getColor(R.color.cns_celeste));
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        } else {
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        }

        Notification mNotification  = builder.build();
        notificationManager.notify(notificationId, mNotification);

    }

    private static Activity getForegroundActivity(){
        Activity activity = null;
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);

            if(activities == null)
                return null;

            for(Object activityRecord:activities.values()){
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if(!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return activity;
    }
}