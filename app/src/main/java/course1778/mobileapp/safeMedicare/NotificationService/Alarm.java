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
import android.support.v4.app.NotificationCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import course1778.mobileapp.safeMedicare.Main.FamMemActivity;
import course1778.mobileapp.safeMedicare.R;

/**
 * Created by lang on 24/02/16.
 */
public class Alarm extends BroadcastReceiver {
    private final String REMINDER_BUNDLE = "MyReminderBundle";
    public static MediaPlayer mPlayer;
    //private static final int NOTIFY_ID=1337;
    private int NOTIFY_ID = 0;

    // this constructor is called by the alarm manager.
    public Alarm(){ }

    // you can use this constructor to create the alarm.
    //  Just pass in the main activity as the context,
    //  any extras you'd like to get later when triggered
    //  and the timeout
    public Alarm(Context context, Bundle extras){
        AlarmManager alarmMgr =
                (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm.class);
        intent.putExtra(REMINDER_BUNDLE, extras);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis());



//        List<Integer> day = new ArrayList<Integer>();
//        day.add(Calendar.FRIDAY);
//        day.add(Calendar.MONDAY);
//
//        time.set(Calendar.DAY_OF_WEEK,day);



        time.set(Calendar.HOUR_OF_DAY, Integer.parseInt(extras.getString("time_h")));
        time.set(Calendar.MINUTE, Integer.parseInt(extras.getString("time_m")));
        time.set(Calendar.SECOND, 0);
        String title = extras.getString("title");
        //NOTIFY_ID = Integer.parseInt(extras.getString("title")+ extras.getString("time_h") +extras.getString("time_m"));
        int length = title.length();
        for (int i = 0; i<length; i++) {
            NOTIFY_ID = (int) title.charAt(i) + NOTIFY_ID;
        }
        NOTIFY_ID = Integer.parseInt(extras.getString("time_h") +extras.getString("time_m"));
        extras.putInt("id", NOTIFY_ID);


        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // here you can get the extras you passed in when creating the alarm
        //intent.getBundleExtra(REMINDER_BUNDLE));

        Bundle getBundle = intent.getBundleExtra(REMINDER_BUNDLE);

        //mPlayer=new MediaPlayer();
        mPlayer= MediaPlayer.create(context, R.raw.aironthegstring);
        try {
            mPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.start();

        String title = getBundle.getString("title");
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


        Alarm_Msg alarm_msg = new Alarm_Msg(context, getBundle);



        //Toast.makeText(context, "Alarm went off", Toast.LENGTH_SHORT).show();
    }

    private NotificationCompat.Builder buildNormal(Context context, String title, Bundle extras) {
        NotificationCompat.Builder b=new NotificationCompat.Builder(context);
        Snooze snooze = new Snooze();

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
                        context.getString(R.string.taken),
                        buildPendingIntent(Taken_Activity.class, context, extras));
//                .addAction(android.R.drawable.ic_media_play,
//                        context.getString(R.string.snooze),
//                        buildPendingIntent(Snooze.class, context, extras));
        //buildPendingIntent(Settings.ACTION_SETTINGS, context));

        return(b);
    }

    private PendingIntent buildPendingIntent(Class intentclass, Context context, Bundle extras) {
        //mPlayer.stop();
        Intent intent=new Intent(context, intentclass);
        intent.putExtra(REMINDER_BUNDLE, extras);
        //int ID= extras.getInt("id");
        //cancelNotification(context, ID);

        return(PendingIntent.getActivity(context, 0, intent, 0));
    }



    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
}
