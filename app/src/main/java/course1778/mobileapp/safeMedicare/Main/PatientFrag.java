package course1778.mobileapp.safeMedicare.Main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import course1778.mobileapp.safeMedicare.NotificationService.Alarm;
import course1778.mobileapp.safeMedicare.R;

/**
 * Created by jianhuang on 16-03-03.
 */
public class PatientFrag extends android.support.v4.app.Fragment {

    public TextView todos;
    private ListView listView;
    private ArrayList<String> strArrList= new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        View view = inflater.inflate(R.layout.fam_mem_main_frag,
                container, false);

        TextView date = (TextView) view.findViewById(R.id.date);
        //listView = (ListView) view.findViewById(R.id.listView);
        //ListView listView = (ListView) view.findViewById(R.id.list_view);
        Calendar c = Calendar.getInstance();
        String day;
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

        String sDate = c.get(Calendar.YEAR) + "-"
                + c.get(Calendar.MONTH)
                + "-" + c.get(Calendar.DAY_OF_MONTH)
                + "   " + day;

        date.setText(sDate);

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
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Helpers.PARSE_OBJECT);
        query.whereEqualTo(Helpers.PARSE_OBJECT_USER, ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> parseObjectList, ParseException e) {
                if (e == null) {
                    // clean up array list before syncing
                    strArrList.clear();

                    for (ParseObject parseObject : parseObjectList) {

                        String string;
                        Bundle bundle = new Bundle();
                        bundle.putString(DatabaseHelper.TITLE, parseObject.getString(DatabaseHelper.TITLE));
                        bundle.putString(DatabaseHelper.TIME_H, parseObject.getString(DatabaseHelper.TIME_H));
                        bundle.putString(DatabaseHelper.TIME_M, parseObject.getString(DatabaseHelper.TIME_M));
                        string = "Medication: " +
                                parseObject.getString(DatabaseHelper.TITLE) +
                                ", At time: " +
                                parseObject.getString(DatabaseHelper.TIME_H) +
                                "h : " +
                                parseObject.getString(DatabaseHelper.TIME_M) +
                                "m";
                        strArrList.add(string);


                        Log.d("mybundle", "TITLE: " + bundle.getString(DatabaseHelper.TITLE));
                        Log.d("mybundle", "HOUR: " + bundle.getString(DatabaseHelper.TIME_H));
                        Log.d("mybundle","MIN: " + bundle.getString(DatabaseHelper.TIME_M));
                        Log.d("mybundle","ID: " + bundle.getInt(Helpers.NOFITY_ID));

                        // list file info details
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<String>(getActivity().getApplicationContext(),
                                        R.layout.list_view_text_style, android.R.id.title, strArrList.toArray(new String[0]));
                        listView.setAdapter(adapter);

                        Alarm alarm = new Alarm(getActivity().getApplicationContext(), bundle);
                    }
//                    todos.setText("");
//                    for (int i = 0; i < parseObjectList.size(); i++) {
//                        todos.append("\n");
//                        todos.append(parseObjectList.get(i).getString(DatabaseHelper.TITLE));
//                        todos.append(" ");
//                        todos.append(parseObjectList.get(i).getString(DatabaseHelper.TIME_H));
//                        todos.append(" ");
//                        todos.append(parseObjectList.get(i).getString(DatabaseHelper.TIME_M));
//                    }
                }
            }
        });
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

    @Override
    public void onResume() {
        super.onResume();
        this.retrieveDataFromParse();
    }
}
