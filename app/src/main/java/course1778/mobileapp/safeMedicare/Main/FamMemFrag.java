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
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.Calendar;

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

    ListView listView;

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
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fam_mem_main_frag,
                container, false);

        TextView date = (TextView) view.findViewById(R.id.date);
        //listView = (ListView) view.findViewById(R.id.listView);
        //ListView listView = (ListView) view.findViewById(R.id.list_view);
        Calendar c = Calendar.getInstance();
        String day, month;

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

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
//                new SimpleCursorAdapter(getActivity(), R.layout.fam_mem_frag,
//                        current, new String[]{
//                        DatabaseHelper.TITLE,
//                        //String.format(format, DatabaseHelper.TIME_H),
//                        DatabaseHelper.TIME_H,
//                        //String.format(format, DatabaseHelper.TIME_M)},
//                        DatabaseHelper.TIME_M},
//                        new int[]{R.id.title, R.id.time_h, R.id.time_m},
//                        0);
//
//        listView.setAdapter(adapter);
//
//        if (current == null) {
//            db = new DatabaseHelper(getActivity());
//            task = new LoadCursorTask().execute();
//        }
//
//        // onBackPress key listener
//        view.setFocusableInTouchMode(true);
//        view.requestFocus();
//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    Intent intent = new Intent(getActivity().getApplicationContext(), WelcomePage.class);
//                    startActivity(intent);
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ListView listView = (ListView) view.findViewById(R.id.list_view);

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




        setListAdapter(adapter);
        //listView.setAdapter(adapter);


//        if (current == null) {
            db = new DatabaseHelper(getActivity());
            task = new LoadCursorTask().execute();
//        }


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

//            case R.id.pushUpdate:
//                pushUpdate();
//                break;
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
        crsInteractions = med_interaction.rawQuery("SELECT * FROM DrugDrug", null);

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
        // get strings from edittext boxes, then insert them into database
        ContentValues values = new ContentValues(DatabaseHelper.CONTENT_VALUE_COUNT);
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
        //int day = 0;
        int monday = 0, tuesday=0, wednesday=0, thursday=0, friday=0, saturday=0, sunday=0;

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


        //give different
        if (((CheckBox) dlg.findViewById(R.id.MonCheck)).isChecked()){
            monday = monday + 1;
        }
        if (((CheckBox) dlg.findViewById(R.id.TueCheck)).isChecked()){
            tuesday = tuesday + 1;
        }
        if (((CheckBox) dlg.findViewById(R.id.WedCheck)).isChecked()){
            wednesday = wednesday + 1;
        }
        if (((CheckBox) dlg.findViewById(R.id.ThuCheck)).isChecked()){
            thursday = thursday + 1;
        }
        if (((CheckBox) dlg.findViewById(R.id.FriCheck)).isChecked()){
            friday = friday + 1;
        }
        if (((CheckBox) dlg.findViewById(R.id.SatCheck)).isChecked()){
            saturday = saturday + 1;
        }
        if (((CheckBox) dlg.findViewById(R.id.SunCheck)).isChecked()){
            sunday = sunday + 1;
        }

        // clear focus before retrieving the min and hr
        tp.clearFocus();

        int tpMinute = tp.getCurrentMinute();
        int tpHour = tp.getCurrentHour();
        // an order number to order the list view items
        int orderNum = tpHour * 60 + tpMinute;
        tp.setIs24HourView(true);

        String titleStr = title.getText().toString();
        String timeHStr = Helpers.StringFormatter(tpHour, "00");
        String timeMStr = Helpers.StringFormatter(tpMinute, "00");
        String dosageStr = dosage.getText().toString();
        String instructionStr = instruction.getText().toString();

        Log.d("mytime", Integer.toString(tpHour));
        Log.d("mytime", Integer.toString(tpMinute));

        values.put(DatabaseHelper.USRNAME, ParseUser.getCurrentUser().getUsername());
        values.put(DatabaseHelper.TITLE, titleStr);
        values.put(DatabaseHelper.TIME_H, timeHStr);
        values.put(DatabaseHelper.TIME_M, timeMStr);
        values.put(DatabaseHelper.FREQUENCY, Fre);
        //values.put(DatabaseHelper.DAY, day);

        values.put(DatabaseHelper.MONDAY, monday);
        values.put(DatabaseHelper.TUESDAY, tuesday);
        values.put(DatabaseHelper.WEDNESDAY, wednesday);
        values.put(DatabaseHelper.THURSDAY, thursday);
        values.put(DatabaseHelper.FRIDAY, friday);
        values.put(DatabaseHelper.SATURDAY,saturday);
        values.put(DatabaseHelper.SUNDAY,sunday);

        values.put(DatabaseHelper.DOSAGE, dosageStr);
        values.put(DatabaseHelper.INSTRUCTION, instructionStr);
        values.put(DatabaseHelper.SHAPE, shape);
        values.put(DatabaseHelper.ORDER_NUM, orderNum);

        Bundle bundle = new Bundle();
        // add extras here..
        bundle.putString(DatabaseHelper.TITLE, title.getText().toString());
        bundle.putString(DatabaseHelper.TIME_H, timeHStr);
        bundle.putString(DatabaseHelper.TIME_M, timeMStr);
        bundle.putInt(DatabaseHelper.FREQUENCY, Fre);
        //bundle.putInt(DatabaseHelper.DAY, day);

        bundle.putInt(DatabaseHelper.MONDAY, monday);
        bundle.putInt(DatabaseHelper.TUESDAY, tuesday);
        bundle.putInt(DatabaseHelper.WEDNESDAY, wednesday);
        bundle.putInt(DatabaseHelper.THURSDAY, thursday);
        bundle.putInt(DatabaseHelper.FRIDAY, friday);
        bundle.putInt(DatabaseHelper.SATURDAY, saturday);
        bundle.putInt(DatabaseHelper.SUNDAY, sunday);

        bundle.putString(DatabaseHelper.DOSAGE, dosageStr);
        bundle.putString(DatabaseHelper.INSTRUCTION, instructionStr);
        bundle.putInt(DatabaseHelper.SHAPE, shape);
        bundle.putInt(DatabaseHelper.ORDER_NUM, orderNum);
        //Alarm alarm = new Alarm(getActivity().getApplicationContext(), bundle);

        // get unique notifyId for each alarm
        int length = title.length();
        for (int i = 0; i<length; i++) {
            notifyId = (int) titleStr.charAt(i) + notifyId;
        }
        notifyId = Integer.parseInt(timeHStr + timeMStr);

        // saving it into parse.com
        ParseObject parseObject = new ParseObject(Helpers.PARSE_OBJECT);
        parseObject.put(DatabaseHelper.USRNAME, ParseUser.getCurrentUser().getUsername());
        parseObject.put(DatabaseHelper.TITLE, titleStr);
        parseObject.put(DatabaseHelper.TIME_H, timeHStr);
        parseObject.put(DatabaseHelper.TIME_M, timeMStr);
        parseObject.put(DatabaseHelper.FREQUENCY, Fre);
        //parseObject.put(DatabaseHelper.DAY, day);

        parseObject.put(DatabaseHelper.MONDAY, monday);
        parseObject.put(DatabaseHelper.TUESDAY, tuesday);
        parseObject.put(DatabaseHelper.WEDNESDAY, wednesday);
        parseObject.put(DatabaseHelper.THURSDAY, thursday);
        parseObject.put(DatabaseHelper.FRIDAY, friday);
        parseObject.put(DatabaseHelper.SATURDAY,saturday);
        parseObject.put(DatabaseHelper.SUNDAY,sunday);

        parseObject.put(DatabaseHelper.DOSAGE, dosageStr);
        parseObject.put(DatabaseHelper.INSTRUCTION, instructionStr);
        parseObject.put(DatabaseHelper.SHAPE, shape);
        parseObject.put(DatabaseHelper.NOFITY_ID, notifyId);
        parseObject.put(DatabaseHelper.ORDER_NUM, orderNum);
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
                            "usr_name=\'"+ParseUser.getCurrentUser().getUsername()+"\'",
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
                    DatabaseHelper.TITLE, values[0]);

            return (doQuery());
        }
    }
}
