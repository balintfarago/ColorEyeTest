package com.b2creatived.coloreyetest;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class CheckRecentRun extends Service {

    private final static String TAG = "CheckRecentPlay";
    private static Long MILLISECS_PER_DAY = 86400000L;
    private static Long MILLISECS_PER_MIN = 60000L;

    //private static long delay = MILLISECS_PER_MIN * 3;   // 3 minutes (for testing)
    private static long delay = MILLISECS_PER_DAY * 3;   // 3 days

    @Override
    public void onCreate() {
        super.onCreate();

        //Log.v(TAG, "Service started");
        SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Log.v("TAG1", sharedpref.getLong("lastRun", Long.MAX_VALUE) + "");
        //Log.v("TAG2", System.currentTimeMillis() - delay + "");

        if (sharedpref.getLong("lastRun", Long.MAX_VALUE) < System.currentTimeMillis() - delay)
            sendNotification();

        // Set an alarm for the next time this service should run:
        setAlarm();

        //Log.v(TAG, "Service stopped");
        stopSelf();
    }

    public void setAlarm() {

        Intent serviceIntent = new Intent(this, CheckRecentRun.class);
        PendingIntent pi = PendingIntent.getService(this, 10000, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pi);
        //Log.v(TAG, "Alarm set");
    }

    public void sendNotification() {

        Intent mainIntent = new Intent(this, PreSplash.class);
        mainIntent.putExtra("noti_id", "noti_missyou");

        Notification noti = new Notification.Builder(this)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 10000, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentTitle(getString(R.string.noti_missyou))
                .setContentText(getString(R.string.noti_itstime))
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.noti_icon)
                .setTicker(getString(R.string.noti_itstime))
                .setWhen(System.currentTimeMillis())
                .setStyle(new Notification.BigTextStyle().bigText(getString(R.string.noti_itstime)))
                 .build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(10000, noti);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}