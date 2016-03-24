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
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import course1778.mobileapp.safeMedicare.Helpers.DatabaseHelper;
import course1778.mobileapp.safeMedicare.Helpers.Helpers;
import course1778.mobileapp.safeMedicare.R;

public class FamMemFrag extends android.support.v4.app.ListFragment implements
        DialogInterface.OnClickListener {
    private DatabaseHelper db = null;
    private Cursor current = null;
    private AsyncTask task = null;
    private int notifyId = 0;
    private static final String[] items = {"Once a Day", "Twice a Day", "Three Times a Day", "Four Times a Day", "Five Times a Day", "Six Times a Day", "Seven Times a Day", "Eight Times a Day", "Nine Times a Day", "Ten Times a Day"};

    FileOutputStream outputStream;

    public static final String PREFIX = "stream2file";
    public static final String SUFFIX = ".tmp";
    Cursor crsList, crsInteractions;

    private ArrayList<String> drug_interaction_list = new ArrayList<String>();
    SQLiteDatabase med_interaction, med_list;

    AutoCompleteTextView textView;

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

        InputStream inputStream1 = getResources().openRawResource(R.raw.med_interaction);

        try {
            med_interaction = SQLiteDatabase.openOrCreateDatabase(stream2file(inputStream1), null);
        } catch (IOException e) {
            System.out.print(e);
        }

        InputStream inputStream2 = getResources().openRawResource(R.raw.med_list);

        try {
            med_list = SQLiteDatabase.openOrCreateDatabase(stream2file(inputStream2), null);
        } catch (IOException e) {
            System.out.print(e);
        }

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //String format = "%1$02d"; // two digits

//        Calendar c = Calendar.getInstance();
//
//        String sDate = c.get(Calendar.YEAR) + "-"
//                + c.get(Calendar.MONTH)
//                + "-" + c.get(Calendar.DAY_OF_MONTH)
//                + " at " + c.get(Calendar.HOUR_OF_DAY)
//                + ":" + c.get(Calendar.MINUTE);

        SimpleCursorAdapter adapter =
            new SimpleCursorAdapter(getActivity(), R.layout.fam_mem_frag,
                current, new String[]{
                DatabaseHelper.TITLE,
                    //String.format(format, DatabaseHelper.TIME_H),
                DatabaseHelper.TIME_H,
                    //String.format(format, DatabaseHelper.TIME_M)},
                    DatabaseHelper.TIME_M},
                new int[]{R.id.title, R.id.time_h, R.id.time_m},
                    0);

//        TextView textView = new TextView(getContext());
//        textView.setText("Date");
//
//        adapter.addHeaderView(textView);



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
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));

        builder.setTitle(R.string.add_title).setView(addView)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, null).show();

        Spinner spin=(Spinner)addView.findViewById(R.id.spinner);
        //spin.setOnItemSelectedListener(FamMemActivity.getContext());
        ArrayAdapter<String> aa=new ArrayAdapter<String>(FamMemActivity.getContext(),R.layout.spinner_item_text,items);
        aa.setDropDownViewResource(
                R.layout.spinner_dropdown_item);
        spin.setAdapter(aa);

        // field for user adding medication name
        textView = (AutoCompleteTextView) addView.findViewById(R.id.title);

        /** sheet 1 displays all the drug interactions;
         * sheet 2 displays the list of all drugs
          */
        crsList = med_list.rawQuery("SELECT * FROM Sheet1", null);
        crsInteractions = med_interaction.rawQuery("SELECT * FROM Sheet1", null);

        String[] array = new String[crsList.getCount()];
        int i = 0;
        while(crsList.moveToNext()){
            String uname = crsList.getString(crsList.getColumnIndex("Name"));
            array[i] = uname;
            i++;
        }

        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(FamMemActivity.getContext(), R.layout.listlayout, R.id.listTextView, array);
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

    // listen to "ok" button state from alertDialog
    public void onClick(DialogInterface di, int whichButton) {
        // clear array list
        drug_interaction_list.clear();

        // loop through database
        crsInteractions.moveToPosition(-1);
        while(crsInteractions.moveToNext()) {
            String drugName, interactionName, interactionResult, medNameFieldTxt;

            // drug names
            drugName = crsInteractions.
                    getString(crsInteractions.
                            getColumnIndex(DatabaseHelper.SHEET_1_DRUG_NAMES));


            // corresponding interacted drugs/foods
            interactionName = crsInteractions.
                    getString(crsInteractions.
                            getColumnIndex(DatabaseHelper.SHEET_1_DRUG_INTERACTIONS));

            // interaction result
            interactionResult = crsInteractions.
                    getString(crsInteractions.
                            getColumnIndex(DatabaseHelper.SHEET_1_INTERACTION_RESULT));

            // medication name entered by user
            medNameFieldTxt = textView.getText().toString();

            // check if newly entered medication name matches current drug name
            if(drugName.equals(medNameFieldTxt)) {
                /**if found, check if the corresponding interaction
                 * drug in the list of all added drugs by user
                 */
                if(db.isNameExitOnDB(interactionName)) {
                    // interaction found
                    Log.d("myinteraction", "Found Interaction");
                    // inflate a dialog to display the drug interaction warning
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View resultView = inflater.inflate(R.layout.drug_interaction_result, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle(R.string.drug_interaction_result_title).setView(resultView)
                            .setPositiveButton(R.string.ok, null).show();

                    // html string format
                    TextView interactionResultView = (TextView) resultView.findViewById(R.id.resultView);
                    String htmlString = "<br> <b>" + medNameFieldTxt + "</b>" + " and " + "<b>" +
                            interactionName + "</b>" + " have drug interaction! " + "<br> <br>" +
                            interactionResult + "<br> <br>" + getString(R.string.interaction_warning);

                    interactionResultView.setText(Html.fromHtml(htmlString));

                    // two line spaces before add new interactoin
                    drug_interaction_list.add(htmlString);
                    drug_interaction_list.add("<br> <br>");
                }
            }
        }

        // write the list of drug interactions into file
        Helpers.writeToFile(getContext(), drug_interaction_list, "drug_interaction_list");

        Log.d("mydatabase", DatabaseUtils.dumpCursorToString(db.getCursor()));

        // get strings from edittext boxes, then insert them into database
        ContentValues values = new ContentValues(7);
        Dialog dlg = (Dialog) di;
        EditText title = (EditText) dlg.findViewById(R.id.title);
        TimePicker tp = (TimePicker)dlg.findViewById(R.id.timePicker);
        Spinner mySpinner=(Spinner) dlg.findViewById(R.id.spinner);
        String fre = mySpinner.getSelectedItem().toString();
        EditText dosage = (EditText) dlg.findViewById(R.id.dosage);
        EditText instruction = (EditText) dlg.findViewById(R.id.instruction);
        RadioGroup radioButtonGroup = (RadioGroup) dlg.findViewById(R.id.radioGroup);
        int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
        View radioButton = radioButtonGroup.findViewById(radioButtonID);
        int shape = radioButtonGroup.indexOfChild(radioButton)/2;
        int Fre;
        int day = 0;

        if (fre == "Ten Times a Day"){
            Fre = 10;
        } else if (fre == "Twice a Day"){
            Fre = 2;
        } else if (fre == "Three Times a Day"){
            Fre = 3;
        } else if (fre == "Four Times a Day"){
            Fre = 4;
        } else if (fre == "Five Times a Day"){
            Fre = 5;
        } else if (fre == "Six Times a Day"){
            Fre = 6;
        } else if (fre == "Seven Times a Day"){
            Fre = 7;
        } else if (fre == "Eight Times a Day"){
            Fre = 8;
        } else if (fre == "Nine Times a Day"){
            Fre = 9;
        } else {
            Fre = 1;
        }


        if (((CheckBox) dlg.findViewById(R.id.MonCheck)).isChecked()){
            day = day + 1;
        }
        if (((CheckBox) dlg.findViewById(R.id.TueCheck)).isChecked()){
            day = day + 2;
        }
        if (((CheckBox) dlg.findViewById(R.id.WedCheck)).isChecked()){
            day = day + 4;
        }
        if (((CheckBox) dlg.findViewById(R.id.ThuCheck)).isChecked()){
            day = day + 8;
        }
        if (((CheckBox) dlg.findViewById(R.id.FriCheck)).isChecked()){
            day = day + 16;
        }
        if (((CheckBox) dlg.findViewById(R.id.SatCheck)).isChecked()){
            day = day + 32;
        }
        if (((CheckBox) dlg.findViewById(R.id.SunCheck)).isChecked()){
            day = day + 64;
        }

        // clear focus before retrieving the min and hr
        tp.clearFocus();

        int tpMinute = tp.getCurrentMinute();
        int tpHour = tp.getCurrentHour();
        tp.setIs24HourView(true);

        String titleStr = title.getText().toString();
        String timeHStr = Integer.toString(tpHour);
        String timeMStr = Integer.toString(tpMinute);
        String dosageStr = dosage.getText().toString();
        String instructionStr = instruction.getText().toString();

        Log.d("mytime",Integer.toString(tpHour));
        Log.d("mytime",Integer.toString(tpMinute));

        values.put(DatabaseHelper.TITLE, titleStr);
        values.put(DatabaseHelper.TIME_H, timeHStr);
        values.put(DatabaseHelper.TIME_M, timeMStr);
        values.put(DatabaseHelper.FREQUENCY, Fre);
        values.put(DatabaseHelper.DAY, day);
        values.put(DatabaseHelper.DOSAGE, dosageStr);
        values.put(DatabaseHelper.INSTRUCTION, instructionStr);
        values.put(DatabaseHelper.SHAPE,shape);

        Bundle bundle = new Bundle();
        // add extras here..
        bundle.putString("title", title.getText().toString());
        bundle.putString("time_h", timeHStr);
        bundle.putString("time_m", timeMStr);
        bundle.putInt("frequency", Fre);
        bundle.putInt("day", day);
        bundle.putString("dosage", dosageStr);
        bundle.putString("instruction", instructionStr);
        bundle.putInt("shape", shape);
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
                            DatabaseHelper.TIME_M,
                        DatabaseHelper.SHAPE},
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
