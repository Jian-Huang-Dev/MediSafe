package course1778.mobileapp.safeMedicare.Main;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import course1778.mobileapp.safeMedicare.Helpers.Helpers;
import course1778.mobileapp.safeMedicare.R;

/**
 * Created by jianhuang on 16-03-14.
 */
public class DrugInteractions extends android.support.v4.app.Fragment {

    String[] saved_files;
    TextView drugInteractionsDisplay;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drug_interactions,
                container, false);

        drugInteractionsDisplay = (TextView) view.findViewById(R.id.drugInteractionsDisplay);
        saved_files = getActivity().getApplicationContext().fileList();
        displayAllDrugInteractions(saved_files);

        return view;
    }

    /**
     * Display all drug interactions
     * @param saved_files
     */
    private void displayAllDrugInteractions(String[] saved_files) {
        String drugInteractions = "";
        String drugInteractionsTxt = "";

        ArrayList<String> people_info_list_in_files = new ArrayList<String>();

        for (int i = 0; i < saved_files.length; i++) {
            // get file contents
            drugInteractions =
                    Helpers.readFile(getContext(), saved_files[i]);
        }

        // get rid of uneeded string before display on screen
        drugInteractionsTxt =
                drugInteractions.replace("[", "")
                                .replace("]", "")
                                .replace(",", "");

        if (drugInteractions.contains("[")) {
            Log.d("myfile", "found");
        }

        drugInteractionsDisplay.setText(Html.fromHtml(drugInteractionsTxt));

        Log.d("myfile", drugInteractionsTxt);



        // display them in list view
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                R.layout.list_view_text_style, android.R.id.text1, people_info_list_in_files);
//        people_list_view.setAdapter(adapter);
    }
}
