package course1778.mobileapp.safeMedicare.NotificationService;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.IOException;

import course1778.mobileapp.safeMedicare.Main.FamMemActivity;
import course1778.mobileapp.safeMedicare.R;

/**
 * Created by lang on 06/03/16.
 */
public class Snooze extends BroadcastReceiver {
    private final String REMINDER_BUNDLE = "MyReminderBundle";
    public static MediaPlayer player;
    //private static final int NOTIFY_ID=1337;
    private int NOTIFY_ID = 0;

    // this constructor is called by the alarm manager.
    public Snooze(){ }

    // you can use this constructor to create the alarm.
    //  Just pass in the main activity as the context,
    //  any extras you'd like to get later when triggered
    //  and the timeout
    public Snooze(Context context, Bundle extras){
        //Alarm.mPlayer.stop();


        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Snooze.class);
        intent.putExtra(REMINDER_BUNDLE, extras);
        Bundle getBundle = intent.getBundleExtra(REMINDER_BUNDLE);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, getBundle.getInt("id"), intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        60 * 1000, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // here you can get the extras you passed in when creating the alarm
        //intent.getBundleExtra(REMINDER_BUNDLE));

        Bundle getBundle = intent.getBundleExtra(REMINDER_BUNDLE);

        player=new MediaPlayer();
        player= MediaPlayer.create(context, R.raw.aironthegstring);
        try {
            player.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();

        String title = getBundle.getString("title");
        NOTIFY_ID = getBundle.getInt("id");

        //Toast.makeText(context, Integer.toString(NOTIFY_ID), Toast.LENGTH_LONG).show();
        //Bundle bundle = new Bundle();
// add extras here..
        //bundle.putString("title", title);

        NotificationManager mgr=
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder normal=buildNormal(context, title, getBundle);
        NotificationCompat.InboxStyle notification=
                new NotificationCompat.InboxStyle(normal);

        mgr.notify(NOTIFY_ID,
                notification
                        .addLine(title)
                        .addLine(context.getString(R.string.description))
                        .build());






        //Toast.makeText(context, "Alarm went off", Toast.LENGTH_SHORT).show();
    }

    private NotificationCompat.Builder buildNormal(Context context, String title, Bundle extras) {
        NotificationCompat.Builder b=new NotificationCompat.Builder(context);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(context.getString(R.string.getmed))
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                        //.setContentText(title)
                        //.setContentIntent(buildPendingIntent(Settings.ACTION_SECURITY_SETTINGS, context))
                .setSmallIcon(R.drawable.medicine_notify)
                        //.setLargeIcon(R.drawable.medicine)
                .setTicker(context.getString(R.string.getmed) + title)
                .setPriority(Notification.PRIORITY_HIGH)
                .addAction(android.R.drawable.ic_media_play,
                        context.getString(R.string.show),
                        buildPendingIntent(FamMemActivity.class, context, extras))
                .addAction(android.R.drawable.ic_media_play,
                        context.getString(R.string.snooze),
                        buildPendingIntent(Snooze_Snooze_Act.class, context, extras))
                .addAction(android.R.drawable.ic_media_play,
                        context.getString(R.string.taken),
                        buildPendingIntent(Snooze_Taken_Act.class, context, extras));
        //buildPendingIntent(Settings.ACTION_SETTINGS, context));

        return(b);
    }

    private PendingIntent buildPendingIntent(Class intentclass, Context context, Bundle extras) {
        //mPlayer.stop();
        Intent intent=new Intent(context, intentclass);
        intent.putExtra(REMINDER_BUNDLE, extras);

        return(PendingIntent.getActivity(context, 0, intent, 0));
    }
}