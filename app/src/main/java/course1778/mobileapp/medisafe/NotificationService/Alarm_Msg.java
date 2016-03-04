package course1778.mobileapp.medisafe.NotificationService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import course1778.mobileapp.medisafe.R;

/**
 * Created by lang on 28/02/16.
 */
public class Alarm_Msg extends BroadcastReceiver {
    private final String REMINDER_BUNDLE = "MyReminderBundle";
    private static final int NOTIFY_ID=1338;

    // this constructor is called by the alarm manager.
    public Alarm_Msg(){ }

    // you can use this constructor to create the alarm.
    //  Just pass in the main activity as the context,
    //  any extras you'd like to get later when triggered
    //  and the timeout
    public Alarm_Msg(Context context, Bundle extras){
        /*AlarmManager alarmMgr =
                (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm_Msg.class);
        intent.putExtra(REMINDER_BUNDLE, extras);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis());
        time.set(Calendar.HOUR_OF_DAY, Integer.parseInt(extras.getString("time_h")));
        time.set(Calendar.MINUTE, Integer.parseInt(extras.getString("time_m")));

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);*/


        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm_Msg.class);
        intent.putExtra(REMINDER_BUNDLE, extras);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent,
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


        String title = getBundle.getString("title");

        Log.i("Send SMS", "");
        Toast.makeText(context, (title+context.getString(R.string.msgcontent)), Toast.LENGTH_SHORT).show();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("12896892386", null, (title+context.getString(R.string.msgcontent)), null, null);
            Toast.makeText(context, "SMS sent.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(context, "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}


