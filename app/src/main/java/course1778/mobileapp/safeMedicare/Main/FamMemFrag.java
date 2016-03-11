/***
 * Copyright (c) 2008-2014 CommonsWare, LLC
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain	a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 * by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 * <p/>
 * From _The Busy Coder's Guide to Android Development_
 * https://commonsware.com/Android
 */

package course1778.mobileapp.safeMedicare.Main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TimePicker;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import course1778.mobileapp.safeMedicare.Helpers.DatabaseHelper;
import course1778.mobileapp.safeMedicare.Helpers.Helpers;
import course1778.mobileapp.safeMedicare.R;

public class FamMemFrag extends android.support.v4.app.ListFragment implements
        DialogInterface.OnClickListener {
    private DatabaseHelper db = null;
    private Cursor current = null;
    private AsyncTask task = null;
    private int notifyId = 0;

    public static final String PREFIX = "stream2file";
    public static final String SUFFIX = ".tmp";
    Cursor crs;
    SQLiteDatabase med_db;

    public static File stream2file(InputStream in) throws IOException {
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InputStream inputStream = getResources().openRawResource(R.raw.medicine);

        try {
            med_db = SQLiteDatabase.openOrCreateDatabase(stream2file(inputStream), null);
        } catch (IOException e) {
            System.out.print(e);
        }

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SimpleCursorAdapter adapter =
            new SimpleCursorAdapter(getActivity(), R.layout.fam_mem_frag,
                current, new String[]{
                DatabaseHelper.TITLE,
                DatabaseHelper.TIME_H,
                DatabaseHelper.TIME_M},
                new int[]{R.id.title, R.id.time_h, R.id.time_m},
                0);

        setListAdapter(adapter);

        if (current == null) {
            db = new DatabaseHelper(getActivity());
            task = new LoadCursorTask().execute();
        }

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fam_mem, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                add();
                break;

            case R.id.pushUpdate:
                pushUpdate();
                break;
        }

        return (super.onOptionsItemSelected(item));
    }

    private void add() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View addView = inflater.inflate(R.layout.add_edit, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.add_title).setView(addView)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, null).show();

        AutoCompleteTextView textView = (AutoCompleteTextView) addView.findViewById(R.id.title);

        Cursor crs = med_db.rawQuery("SELECT * FROM Sheet1", null);

        String[] array = new String[crs.getCount()];
        int i = 0;
        while(crs.moveToNext()){
            String uname = crs.getString(crs.getColumnIndex("Interactions"));
            array[i] = uname;
            i++;
        }
// Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(FamMemActivity.getContext(), R.layout.listlayout, R.id.listTextView,array);
        textView.setAdapter(adapter);

    }

    private void pushUpdate() {
        Log.d("mypushupdate", "push update");
        // save objects to parse.com
        ParseObject myObject = new ParseObject(Helpers.PARSE_OBJECT);
        myObject.put(ParseUser.getCurrentUser().getUsername(), Helpers.PARSE_OBJECT_VALUE);
        myObject.put(Helpers.PARSE_OBJECT_DATA_KEY, db);
        myObject.saveInBackground();
    }

    public void onClick(DialogInterface di, int whichButton) {
        // get strings from edittext boxes, then insert them into database
        ContentValues values = new ContentValues(3);
        Dialog dlg = (Dialog) di;
        EditText title = (EditText) dlg.findViewById(R.id.title);
        TimePicker tp = (TimePicker)dlg.findViewById(R.id.timePicker);

        tp.clearFocus();

        int tpMinute = tp.getCurrentMinute();
        int tpHour = tp.getCurrentHour();
        tp.setIs24HourView(true);

        String titleStr = title.getText().toString();
        String timeHStr = Integer.toString(tpHour);
        String timeMStr = Integer.toString(tpMinute);

        Log.d("mytime",Integer.toString(tpHour));
        Log.d("mytime",Integer.toString(tpMinute));

        values.put(DatabaseHelper.TITLE, titleStr);
        values.put(DatabaseHelper.TIME_H, timeHStr);
        values.put(DatabaseHelper.TIME_M, timeMStr);

        Bundle bundle = new Bundle();
        // add extras here..
        bundle.putString("title", title.getText().toString());
        bundle.putString("time_h", timeHStr);
        bundle.putString("time_m", timeMStr);
        //Alarm alarm = new Alarm(getActivity().getApplicationContext(), bundle);

        // get unique notifyId for each alarm
        int length = title.length();
        for (int i = 0; i<length; i++) {
            notifyId = (int) titleStr.charAt(i) + notifyId;
        }
        notifyId = Integer.parseInt(timeHStr + timeMStr);

        // saving it into parse.com
        ParseObject parseObject = new ParseObject(Helpers.PARSE_OBJECT);
        parseObject.put(Helpers.PARSE_OBJECT_USER, ParseUser.getCurrentUser().getUsername());
//        parseObject.put(Helpers.PARSE_BUNDLE, bundle);
        parseObject.put(DatabaseHelper.TITLE, titleStr);
        parseObject.put(DatabaseHelper.TIME_H, timeHStr);
        parseObject.put(DatabaseHelper.TIME_M, timeMStr);
        parseObject.put(Helpers.NOFITY_ID, notifyId);
        parseObject.saveInBackground();

        task = new InsertTask().execute(values);
    }


    abstract private class BaseTask<T> extends AsyncTask<T, Void, Cursor> {
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
                            DatabaseHelper.TIME_M},
                        null, null, null, null, DatabaseHelper.TITLE);

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
                    DatabaseHelper.TITLE, values[0]);

            return (doQuery());
        }
    }
}
