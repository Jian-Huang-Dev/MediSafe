package course1778.mobileapp.safeMedicare.Main;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
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
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import course1778.mobileapp.safeMedicare.Helpers.DatabaseHelper;
import course1778.mobileapp.safeMedicare.Helpers.Helpers;
import course1778.mobileapp.safeMedicare.NotificationService.Alarm;
import course1778.mobileapp.safeMedicare.R;

/**
 * Created by jianhuang on 16-03-03.
 */
public class PatientFrag extends android.support.v4.app.ListFragment {

    public TextView todos;
    //private ListView listView;
    private ArrayList<String> strArrList= new ArrayList<String>();
    private DatabaseHelper db = null;
    private Cursor current = null;
    private AsyncTask task = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        retrieveDataFromLocalDatabase();

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_frag,
                container, false);

        todos = (TextView) view.findViewById(R.id.todos);
        //listView = (ListView) view.findViewById(R.id.list_view);

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

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Helpers.PARSE_OBJECT);
        query.whereEqualTo(Helpers.PARSE_OBJECT_USER, ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> parseObjectList, ParseException e) {
                if (e == null) {
                    // clean up array list before syncing
                    strArrList.clear();

                    for (ParseObject parseObject : parseObjectList) {

                        String string;

                        // saving data for alarm use
                        Bundle bundle = new Bundle();
                        bundle.putString(DatabaseHelper.TITLE, parseObject.getString(DatabaseHelper.TITLE));
                        bundle.putString(DatabaseHelper.TIME_H, parseObject.getString(DatabaseHelper.TIME_H));
                        bundle.putString(DatabaseHelper.TIME_M, parseObject.getString(DatabaseHelper.TIME_M));
                        bundle.putString(DatabaseHelper.FREQUENCY, parseObject.getString(DatabaseHelper.FREQUENCY));
                        bundle.putString(DatabaseHelper.DAY, parseObject.getString(DatabaseHelper.DAY));
                        bundle.putString(DatabaseHelper.DOSAGE, parseObject.getString(DatabaseHelper.DOSAGE));
                        bundle.putString(DatabaseHelper.SHAPE, parseObject.getString(DatabaseHelper.SHAPE));
                        bundle.putString(DatabaseHelper.INSTRUCTION, parseObject.getString(DatabaseHelper.INSTRUCTION));

                        // saving data for local database use
                        values.put(DatabaseHelper.TITLE, parseObject.getString(DatabaseHelper.TITLE));
                        values.put(DatabaseHelper.TIME_H, parseObject.getString(DatabaseHelper.TIME_H));
                        values.put(DatabaseHelper.TIME_M, parseObject.getString(DatabaseHelper.TIME_M));
                        values.put(DatabaseHelper.FREQUENCY, parseObject.getString(DatabaseHelper.FREQUENCY));
                        values.put(DatabaseHelper.DAY, parseObject.getString(DatabaseHelper.DAY));
                        values.put(DatabaseHelper.DOSAGE, parseObject.getString(DatabaseHelper.DOSAGE));
                        values.put(DatabaseHelper.SHAPE, parseObject.getString(DatabaseHelper.SHAPE));
                        values.put(DatabaseHelper.INSTRUCTION, parseObject.getString(DatabaseHelper.INSTRUCTION));

                        // saving to local database
                        task = new InsertTask().execute(values);

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

                        Alarm alarm = new Alarm(getActivity().getApplicationContext(), bundle);
                    }
                    // list file info details
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getActivity().getApplicationContext(),
                                    R.layout.list_view_text_style, android.R.id.title, strArrList.toArray(new String[0]));
//                    listView.setAdapter(adapter);
                    setListAdapter(adapter);
                }
            }
        });
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
                                    null, null, null, DatabaseHelper.TITLE);

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
