package com.group13.www.today_list_insert;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class BroadcastD extends BroadcastReceiver {
    String INTENT_ACTION = Intent.ACTION_BOOT_COMPLETED;

    @Override
    public void onReceive(Context context, Intent intent) {
        PendingIntent pendingNoti = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("context"))
                .setTicker(intent.getStringExtra("clock"))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setLargeIcon(icon)
                .setAutoCancel(false)
                .setContentIntent(pendingNoti);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            noti.setPriority(2);   //이 설정은 젤리빈 이상일떄만 사용
        }

        NotificationManager manageNoti = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manageNoti.notify(1, noti.build());
    }
}
