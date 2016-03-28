package course1778.mobileapp.safeMedicare.Main;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import course1778.mobileapp.safeMedicare.Helpers.DatabaseHelper;
import course1778.mobileapp.safeMedicare.Helpers.DatabaseInteractionHelper;
import course1778.mobileapp.safeMedicare.Helpers.DrugInteractionCustomAdapter;
import course1778.mobileapp.safeMedicare.Helpers.Helpers;
import course1778.mobileapp.safeMedicare.R;

/**
 * Created by jianhuang on 16-03-14.
 */
public class DrugInteractions extends android.support.v4.app.ListFragment {

    String[] saved_files;
    SQLiteDatabase med_interaction;
    public static final String PREFIX = "stream2file";
    public static final String SUFFIX = ".tmp";
    private Cursor crsInteractionsStatic, crsInteractionsDynamic, crsLocalDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drug_interaction_main,
                container, false);

        InputStream inputStream1 = getResources().openRawResource(R.raw.med_interaction);

        try {
            med_interaction = SQLiteDatabase.openOrCreateDatabase(stream2file(inputStream1), null);
        } catch (IOException e) {
            System.out.print(e);
        }

        //drugInteractionsDisplay = (TextView) view.findViewById(R.id.drugInteractionsDisplay);
//        saved_files = getActivity().getApplicationContext().fileList();
        displayAllDrugInteractions(saved_files);

        return view;
    }

    /**
     * Display all drug interactions
     * @param saved_files
     */
    private void displayAllDrugInteractions(String[] saved_files) {

        String localDBdrugName, drugInteractionDrugName, drugInteractionName;
        String isToShowInteraction;
        String listItem;
        ArrayList<String> interactionList = new ArrayList<String>();

        crsInteractionsStatic = med_interaction.rawQuery("SELECT * FROM DrugDrug", null);

        DatabaseHelper db = new DatabaseHelper(getContext());
        crsLocalDatabase = db.getCursor();
        DatabaseInteractionHelper dbInteraction = new DatabaseInteractionHelper(getContext());
        crsInteractionsDynamic = dbInteraction.getCursor();
        Log.d("mydatabase2", DatabaseUtils.dumpCursorToString(dbInteraction.getCursor()));

        // loop through dynamic drug interaction database
        crsInteractionsDynamic.moveToPosition(-1);
        while (crsInteractionsDynamic.moveToNext()) {
            isToShowInteraction =
                    crsInteractionsDynamic.getString(
                            crsInteractionsDynamic.getColumnIndex(
                                    DatabaseInteractionHelper.DRUG_INTERACTION_SHOW));

            if (isToShowInteraction.equals(DatabaseInteractionHelper.DRUG_INTERACTION_SHOW_TRUE)) {

                drugInteractionDrugName =
                        crsInteractionsDynamic.getString(
                                crsInteractionsDynamic.getColumnIndex(
                                        DatabaseInteractionHelper.DRUG_NAME));

                drugInteractionName =
                        crsInteractionsDynamic.getString(
                                crsInteractionsDynamic.getColumnIndex(
                                        DatabaseInteractionHelper.DRUG_INTERACTION));

                listItem = drugInteractionDrugName +
                        Helpers.STRING_SPLITER +
                        drugInteractionName;
                interactionList.add(listItem);
            }
        }

        //instantiate custom adapter
        DrugInteractionCustomAdapter adapter = new DrugInteractionCustomAdapter(interactionList, getContext());

        //handle listview and assign adapter
        setListAdapter(adapter);

//        // loop through local database
//        crsLocalDatabase.moveToPosition(-1);
//        while(crsLocalDatabase.moveToNext()) {
//
//            // drug name in local database
//            localDBdrugName = crsLocalDatabase.
//                    getString(crsLocalDatabase.
//                            getColumnIndex(DatabaseHelper.TITLE));
//
//            // first needs to check if drug name exists in our local database
//            if (db.isNameExitOnDB(localDBdrugName)) {
//                // if exists, then
//                // loop through drug intereaction database
//                crsInteractions.moveToPosition(-1);
//                while (crsInteractions.moveToNext()) {
//
//                    drugInteractionDrugName = crsInteractions.
//                            getString(crsInteractions.
//                                    getColumnIndex(DatabaseHelper.SHEET_1_DRUG_NAMES));
//
//                    if (localDBdrugName.equals(drugInteractionDrugName)) {
//                        // if the drug name matches in drug interaction database (fixed)
//                        // get drug intereaction name
//                        drugInteractionName = crsInteractions.
//                                getString(crsInteractions.
//                                        getColumnIndex(DatabaseHelper.SHEET_1_DRUG_INTERACTIONS));
//
//                        // add this interaction into our new drug interaction database (dynamic)
//                    }
//                }
//            }
//        }

//        String drugInteractions = "";
//        String drugInteractionsTxt = "";
//
//        ArrayList<String> people_info_list_in_files = new ArrayList<String>();
//
//        for (int i = 0; i < saved_files.length; i++) {
//            // get file contents
//            drugInteractions =
//                    Helpers.readFile(getContext(), saved_files[i]);
//        }
//
//        // get rid of uneeded string before display on screen
//        drugInteractionsTxt =
//                drugInteractions.replace("[", "")
//                                .replace("]", "")
//                                .replace(",", "");
//
//        if (drugInteractions.contains("[")) {
//            Log.d("myfile", "found");
//        }
//
//        drugInteractionsDisplay.setText(Html.fromHtml(drugInteractionsTxt));
//
//        Log.d("myfile", drugInteractionsTxt);



        // display them in list view
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                R.layout.list_view_text_style, android.R.id.text1, people_info_list_in_files);
//        people_list_view.setAdapter(adapter);
    }

    public static File stream2file(InputStream in) throws IOException {
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }
}
