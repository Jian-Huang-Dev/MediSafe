package course1778.mobileapp.safeMedicare.Main;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import course1778.mobileapp.safeMedicare.Helpers.DatabaseHelper;
import course1778.mobileapp.safeMedicare.Helpers.Helpers;
import course1778.mobileapp.safeMedicare.Helpers.MySimpleCursorAdapter;
import course1778.mobileapp.safeMedicare.NotificationService.Alarm;
import course1778.mobileapp.safeMedicare.R;

/**
 * Created by jianhuang on 16-03-03.
 */
public class PatientFrag extends android.support.v4.app.ListFragment {

   // public TextView todos;
    //private ListView listView;
    private ArrayList<String> strArrList= new ArrayList<String>();
    private DatabaseHelper db = null;
    private Cursor current = null;
    private AsyncTask task = null;
    Handler mHandler;
    public static MediaPlayer mPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retrieveDataFromLocalDatabase();

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.patient_frag,
//                container, false);
//
//        todos = (TextView) view.findViewById(R.id.todos);
//        listView = (ListView) view.findViewById(R.id.list_view);

        View view = inflater.inflate(R.layout.patient_main_frag,
                container, false);

        TextView date = (TextView) view.findViewById(R.id.date);
        //todos = (TextView) view.findViewById(R.id.todos);
        //listView = (ListView) view.findViewById(R.id.listView);
        //ListView listView = (ListView) view.findViewById(R.id.list_view);
        Calendar c = Calendar.getInstance();
        String day, month;
        if (c.get(Calendar.DAY_OF_WEEK) == 1){
            day = "Sunday";
        } else if (c.get(Calendar.DAY_OF_WEEK) == 2){
            day = "Monday";
        } else if (c.get(Calendar.DAY_OF_WEEK) == 3){
            day = "Tuesday";
        } else if (c.get(Calendar.DAY_OF_WEEK) == 4){
            day = "Wednesday";
        } else if (c.get(Calendar.DAY_OF_WEEK) == 5){
            day = "Thursday";
        } else if (c.get(Calendar.DAY_OF_WEEK) == 6){
            day = "Friday";
        } else {
            day = "Saturday";
        }

        if (c.get(Calendar.MONTH) == 0){
            month = "January";
        } else if (c.get(Calendar.MONTH) == 1){
            month = "February";
        } else if (c.get(Calendar.MONTH) == 2){
            month = "March";
        } else if (c.get(Calendar.MONTH) == 3){
            month = "April";
        } else if (c.get(Calendar.MONTH) == 4){
            month = "May";
        } else if (c.get(Calendar.MONTH) == 5) {
            month = "June";
        } else if (c.get(Calendar.MONTH) == 6){
            month = "July";
        } else if (c.get(Calendar.MONTH) == 7) {
            month = "August";
        } else if (c.get(Calendar.MONTH) == 8) {
            month = "September";
        } else if (c.get(Calendar.MONTH) == 9) {
            month = "October";
        } else if (c.get(Calendar.MONTH) == 10) {
            month = "November";
        } else {
            month = "December";
        }

        String sDate = day + ",  "
                + month
                + "  " + c.get(Calendar.DAY_OF_MONTH);

        date.setText(sDate);



//        SimpleCursorAdapter adapter =
//                new SimpleCursorAdapter(getActivity(), R.layout.patient_item,
//                        current, new String[]{
//                        DatabaseHelper.TITLE,
//                        DatabaseHelper.TIME_H,
//                        DatabaseHelper.TIME_M,
//                        DatabaseHelper.DOSAGE,
//                        DatabaseHelper.INSTRUCTION},
//                        new int[]{R.id.name, R.id.time_h, R.id.time_m, R.id.dosage,R.id.instruction},
//                        0);
//
//        setListAdapter(adapter);

        MySimpleCursorAdapter adapter =
                new MySimpleCursorAdapter(getActivity(), R.layout.patient_item,
                        current, new String[]{
                        DatabaseHelper.TITLE,
                        DatabaseHelper.TIME_H,
                        DatabaseHelper.TIME_M,
                        DatabaseHelper.SHAPE,
                        DatabaseHelper.DOSAGE,
                        DatabaseHelper.INSTRUCTION},
                        new int[]{R.id.name, R.id.time_h, R.id.time_m, R.id.shape, R.id.dosage,R.id.instruction});

        setListAdapter(adapter);

        Log.d("mydatabase", DatabaseUtils.dumpCursorToString(current));

        // onBackPress key listener
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), WelcomePage.class);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveDataFromParse();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_patient, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync:
                retrieveDataFromParse();
                break;
        }


        return (super.onOptionsItemSelected(item));
    }

    public void retrieveDataFromParse() {

        final ContentValues values = new ContentValues(DatabaseHelper.CONTENT_VALUE_COUNT);
        // get current number of rows in database table
        DatabaseHelper.preNumRows =
                DatabaseUtils.queryNumEntries(
                        db.getReadableDatabase(), DatabaseHelper.TABLE);
        // set new num of rows in database table same as current one
        DatabaseHelper.nextNumRows = DatabaseHelper.preNumRows;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Helpers.PARSE_OBJECT);
        query.whereEqualTo(DatabaseHelper.USRNAME, ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> parseObjectList, ParseException e) {
                if (e == null) {
                    // clean up array list before syncing
                    strArrList.clear();

                    for (ParseObject parseObject : parseObjectList) {

                        Helpers.currTitle = parseObject.getString(DatabaseHelper.TITLE);
                        String currHour = parseObject.getString(DatabaseHelper.TIME_H);
                        String currMin = parseObject.getString(DatabaseHelper.TIME_M);

                        if (!db.isNameExitOnDB(Helpers.currTitle) || !db.isHourExitOnDB(currHour) || !db.isMinOnDB(currMin)) {
                            // if local database does not have this item, then add it to local database

                            // saving data for alarm use
                            Bundle bundle = new Bundle();
                            bundle = getBundle(bundle, parseObject);

                            // saving data for local database use
                            ContentValues contentValues = new ContentValues(DatabaseHelper.CONTENT_VALUE_COUNT);
                            contentValues = getValues(values, parseObject);

                            // saving to local database
                            task = new InsertTask().execute(values);
//                            db.getWritableDatabase().insert(DatabaseHelper.TABLE,
//                                    DatabaseHelper.USRNAME, contentValues);

                            // indicator
                            DatabaseHelper.dbModified = true;

                            DatabaseHelper.nextNumRows =
                                    DatabaseUtils.queryNumEntries(
                                            db.getReadableDatabase(), DatabaseHelper.TABLE);

                            // set new alarm for this medication
                            new Alarm(getActivity().getApplicationContext(), bundle);




//
//                            NotificationManager mgr= (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//                            NotificationCompat.Builder normal=buildNormal(getContext(), Helpers.currTitle);
//                            NotificationCompat.InboxStyle notification= new NotificationCompat.InboxStyle(normal);

//                            mPlayer= MediaPlayer.create(getContext(), R.raw.aironthegstring);
//                            try {
//                                mPlayer.prepare();
//                            } catch (IllegalStateException e2) {
//                                e2.printStackTrace();
//                            } catch (IOException e2) {
//                                e2.printStackTrace();
//                            }
//                            mPlayer.start();

//                            mgr.notify(1,
//                                    notification
//                                            .addLine(Helpers.currTitle)
//                                            .addLine(getContext().getString(R.string.description))
//                                            .build());
//
//
//                            new CountDownTimer(10000, 1000) { //40000 milli seconds is total time, 1000 milli seconds is time interval
//
//                                public void onTick(long millisUntilFinished) {
//                                    //Toast.makeText(getContext(), Long.toString(millisUntilFinished), Toast.LENGTH_SHORT).show();
//                                }
//                                public void onFinish() {
//
//                                    NotificationManager mgr= (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//                                    NotificationCompat.Builder normal=buildNormal(getContext(), Helpers.currTitle);
//                                    NotificationCompat.InboxStyle notification= new NotificationCompat.InboxStyle(normal);
////                                    try {
////                                        mPlayer.prepare();
////                                    } catch (IllegalStateException e2) {
////                                        e2.printStackTrace();
////                                    } catch (IOException e2) {
////                                        e2.printStackTrace();
////                                    }
////                                    mPlayer.start();
//
//
//                                    mgr.notify(1,
//                                            notification
//                                                    .addLine(Helpers.currTitle)
//                                                    .addLine(getContext().getString(R.string.description))
//                                                    .build());
////                                    try {
////                                        SmsManager smsManager = SmsManager.getDefault();
////                                        //smsManager.sendTextMessage("6474013409", null, (Helpers.currTitle+getContext().getString(R.string.msgcontent)), null, null);
////                                        smsManager.sendTextMessage("12896892386", null, (Helpers.currTitle+PatientActivity.getContext().getString(R.string.msgcontent)), null, null);
////                                        Toast.makeText(PatientActivity.getContext(), "SMS sent.", Toast.LENGTH_LONG).show();
////                                    }
////                                    catch (Exception e3) {
////
////                                    }
//                                    Helpers.sendmessage();
//
//
//
//                                }
//                            }.start();



                        }
                    }
                }
            }
        });
    }

//    private NotificationCompat.Builder buildNormal(Context context, String title) {
//        NotificationCompat.Builder b=new NotificationCompat.Builder(context);
//        //Snooze snooze = new Snooze();
//
//        b.setAutoCancel(true)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setContentTitle(context.getString(R.string.getmed))
//                //.setVisibility(Notification.VISIBILITY_PUBLIC)
//                .setCategory("alarm")
//                .setPriority(2)
//                        //.setContentText(title)
//                        //.setContentIntent(buildPendingIntent(Settings.ACTION_SECURITY_SETTINGS, context))
//                .setSmallIcon(R.drawable.medicine_notify)
//                        //.setLargeIcon(R.drawable.medicine)
//                .setTicker(context.getString(R.string.getmed) + title)
//                .setPriority(Notification.PRIORITY_HIGH)
//                //.setOngoing(true)
////                .addAction(android.R.drawable.ic_media_play,
////                        context.getString(R.string.show)
////                        buildPendingIntent(FamMemActivity.class, context, extras))
//                .addAction(android.R.drawable.ic_media_play,
//                        context.getString(R.string.snooze),
//                        buildPendingIntent(Fake_Taken.class, context))
//                .addAction(android.R.drawable.ic_media_play,
//                        context.getString(R.string.taken),
//                        buildPendingIntent(Fake_Taken.class, context));
////                .addAction(android.R.drawable.ic_media_play,
////                        context.getString(R.string.snooze),
////                        buildPendingIntent(Snooze.class, context, extras));
//        //buildPendingIntent(Settings.ACTION_SETTINGS, context));
//
//        return(b);
//    }
//
//    private PendingIntent buildPendingIntent(Class intentclass, Context context) {
//        //mPlayer.stop();
//        Intent intent=new Intent(context, intentclass);
//        //cancelNotification(context, 1);
//        //mPlayer.stop();
//
//        //int ID= extras.getInt("id");
//        //cancelNotification(context, 1337);
//
//        return(PendingIntent.getActivity(context, 0, intent, 0));
//    }
    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    protected void retrieveDataFromLocalDatabase() {
        db = new DatabaseHelper(getActivity());
        // load local database
        task = new LoadCursorTask().execute();
    }

    public void syncParseObject(ParseObject parseObject) {
        parseObject.fetchInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Log.d("myupdate", "updated");
                } else {
                    Log.d("myupdate", "failed to update");
                }
            }
        });
    }

    public String getWhere(){
        String day;
        Calendar c = Calendar.getInstance();

        if (c.get(Calendar.DAY_OF_WEEK) == 1){
            day = "sunday";
        } else if (c.get(Calendar.DAY_OF_WEEK) == 2){
            day = "monday";
        } else if (c.get(Calendar.DAY_OF_WEEK) == 3){
            day = "tuesday";
        } else if (c.get(Calendar.DAY_OF_WEEK) == 4){
            day = "wednesday";
        } else if (c.get(Calendar.DAY_OF_WEEK) == 5){
            day = "thursday";
        } else if (c.get(Calendar.DAY_OF_WEEK) == 6){
            day = "friday";
        } else {
            day = "saturday";
        }

        String WHERE = "usr_name=\'"+ParseUser.getCurrentUser().getUsername()+"\' AND day_filter='1' ";
        WHERE = WHERE.replaceAll("day_filter", day);

        return WHERE;
    }

    abstract private class BaseTask<T> extends AsyncTask<T, Void, Cursor> {
        //String WHERE =  "TAG1='tagname' OR TAG2='tagname' OR TAG3='tagname' OR TAG4='tagname' OR TAG5='tagname' ";

        //String WHERE = "usr_name=\'"+ParseUser.getCurrentUser().getUsername()+"\' AND \'"+getDay()+"\'='1' ";
        String WHERE = getWhere();

        @Override
        public void onPostExecute(Cursor result) {
            ((CursorAdapter) getListAdapter()).changeCursor(result);
            task = null;
        }
        protected Cursor doQuery() {
            Cursor result =
                    db
                            .getReadableDatabase()
                            .query(DatabaseHelper.TABLE,
                                    new String[]{"ROWID AS _id",
                                            DatabaseHelper.TITLE,
                                            DatabaseHelper.TIME_H,
                                            DatabaseHelper.TIME_M,
                                            DatabaseHelper.SHAPE,
                                            DatabaseHelper.DOSAGE,
                                            DatabaseHelper.INSTRUCTION},
                                    "usr_name=\'"+ParseUser.getCurrentUser().getUsername()+"\'",
                                    //WHERE,
                                    null, null, null, DatabaseHelper.ORDER_NUM);

            result.getCount();

            Log.d("mydatabase", DatabaseUtils.dumpCursorToString(result));

            return (result);
        }
    }

    private class LoadCursorTask extends BaseTask<Void> {
        @Override
        protected Cursor doInBackground(Void... params) {
            return (doQuery());
        }
    }

    private class InsertTask extends BaseTask<ContentValues> {
        @Override
        protected Cursor doInBackground(ContentValues... values) {
            db.getWritableDatabase().insert(DatabaseHelper.TABLE,
                    DatabaseHelper.USRNAME, values[0]);

            return (doQuery());
        }
    }

    // get bundle for alarm
    private Bundle getBundle(Bundle bundle, ParseObject parseObject) {
        bundle.putString(DatabaseHelper.TITLE, parseObject.getString(DatabaseHelper.TITLE));
        bundle.putString(DatabaseHelper.TIME_H, parseObject.getString(DatabaseHelper.TIME_H));
        bundle.putString(DatabaseHelper.TIME_M, parseObject.getString(DatabaseHelper.TIME_M));
        bundle.putString(DatabaseHelper.FREQUENCY, parseObject.getString(DatabaseHelper.FREQUENCY));
        //bundle.putString(DatabaseHelper.DAY, parseObject.getString(DatabaseHelper.DAY));

        bundle.putString(DatabaseHelper.MONDAY, parseObject.getString(DatabaseHelper.MONDAY));
        bundle.putString(DatabaseHelper.TUESDAY, parseObject.getString(DatabaseHelper.TUESDAY));
        bundle.putString(DatabaseHelper.WEDNESDAY, parseObject.getString(DatabaseHelper.WEDNESDAY));
        bundle.putString(DatabaseHelper.THURSDAY, parseObject.getString(DatabaseHelper.THURSDAY));
        bundle.putString(DatabaseHelper.FRIDAY, parseObject.getString(DatabaseHelper.FRIDAY));
        bundle.putString(DatabaseHelper.SATURDAY, parseObject.getString(DatabaseHelper.SATURDAY));
        bundle.putString(DatabaseHelper.SUNDAY, parseObject.getString(DatabaseHelper.SUNDAY));

        bundle.putString(DatabaseHelper.DOSAGE, parseObject.getString(DatabaseHelper.DOSAGE));
        bundle.putString(DatabaseHelper.SHAPE, parseObject.getString(DatabaseHelper.SHAPE));
        bundle.putString(DatabaseHelper.INSTRUCTION, parseObject.getString(DatabaseHelper.INSTRUCTION));
        bundle.putString(DatabaseHelper.ORDER_NUM, parseObject.getString(DatabaseHelper.ORDER_NUM));

        return bundle;
    }

    // get values for local database
    private ContentValues getValues(ContentValues values, ParseObject parseObject) {
        values.put(DatabaseHelper.USRNAME, parseObject.getString(DatabaseHelper.USRNAME));
        values.put(DatabaseHelper.TITLE, parseObject.getString(DatabaseHelper.TITLE));
        values.put(DatabaseHelper.TIME_H, parseObject.getString(DatabaseHelper.TIME_H));
        values.put(DatabaseHelper.TIME_M, parseObject.getString(DatabaseHelper.TIME_M));
        values.put(DatabaseHelper.FREQUENCY, parseObject.getString(DatabaseHelper.FREQUENCY));
        //values.put(DatabaseHelper.DAY, parseObject.getString(DatabaseHelper.DAY));

        values.put(DatabaseHelper.MONDAY, parseObject.getString(DatabaseHelper.MONDAY));
        values.put(DatabaseHelper.TUESDAY, parseObject.getString(DatabaseHelper.TUESDAY));
        values.put(DatabaseHelper.WEDNESDAY, parseObject.getString(DatabaseHelper.WEDNESDAY));
        values.put(DatabaseHelper.THURSDAY, parseObject.getString(DatabaseHelper.THURSDAY));
        values.put(DatabaseHelper.FRIDAY, parseObject.getString(DatabaseHelper.FRIDAY));
        values.put(DatabaseHelper.SATURDAY, parseObject.getString(DatabaseHelper.SATURDAY));
        values.put(DatabaseHelper.SUNDAY, parseObject.getString(DatabaseHelper.SUNDAY));

        values.put(DatabaseHelper.DOSAGE, parseObject.getString(DatabaseHelper.DOSAGE));
        values.put(DatabaseHelper.SHAPE, parseObject.getInt(DatabaseHelper.SHAPE));
        values.put(DatabaseHelper.INSTRUCTION, parseObject.getString(DatabaseHelper.INSTRUCTION));
        values.put(DatabaseHelper.ORDER_NUM, parseObject.getString(DatabaseHelper.ORDER_NUM));

        return values;
    }

    @Override
    public void onResume() {
        super.onResume();
        // load data from local device
        retrieveDataFromLocalDatabase();
    }

    @Override
    public void onDestroy() {
        if (task != null) {
            task.cancel(false);
        }

        ((CursorAdapter) getListAdapter()).getCursor().close();
        db.close();

        super.onDestroy();
    }
}
