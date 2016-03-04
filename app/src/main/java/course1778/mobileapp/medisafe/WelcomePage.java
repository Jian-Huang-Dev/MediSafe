package course1778.mobileapp.medisafe;

/**
 * Created by jianhuang on 16-02-21.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class WelcomePage extends Activity {

    // Declare Variable
    Button logout, updateBtn, syncBtn, famMemPageBtn, patientPageBtn;
    TextView mTxtInfo;
    EditText mTxtBox;
    ParseObject myObject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        // Retrieve current user from Parse.com
        ParseUser currentUser = ParseUser.getCurrentUser();
        String struser = currentUser.getUsername().toString();

        TextView txtuser = (TextView) findViewById(R.id.txtuser);

        // Set the currentUser String into TextView
        txtuser.setText("You are logged in as " + struser);

        // Locate Button in welcome.xml
        logout = (Button) findViewById(R.id.logout);
        updateBtn = (Button) findViewById(R.id.updateBtn);
        syncBtn = (Button) findViewById(R.id.syncBtn);

        famMemPageBtn = (Button) findViewById(R.id.famMemPageBtn);
        patientPageBtn = (Button) findViewById(R.id.patientPageBtn);

        mTxtBox = (EditText) findViewById(R.id.txtBox);
        mTxtInfo = (TextView) findViewById(R.id.txtInfo);


        updateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = mTxtBox.getText().toString();

                if (!TextUtils.isEmpty(str)) {
                    // save objects to parse.com
                    myObject = new ParseObject(Helpers.PARSE_OBJECT);
                    myObject.put(ParseUser.getCurrentUser().getUsername(), Helpers.PARSE_OBJECT_VALUE);
                    myObject.put(Helpers.PARSE_OBJECT_DATA_KEY, str);
                    myObject.saveInBackground();
                }

                // display on screen
                mTxtInfo.setText(str);
            }
        });

        syncBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            retrieveDataFromParse();
            }
        });

        // Logout Button Click Listener
        logout.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                ParseUser.logOut();
                Intent intent = new Intent(getApplication(),
                        LoginSignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // direct to family member page upon click
        famMemPageBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, FamMemActivity.class);
                startActivity(intent);
            }
        });

        // direct to patient page upon click
        patientPageBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, PatientActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveDataFromParse();
    }

    public void retrieveDataFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Helpers.PARSE_OBJECT);
        query.whereEqualTo(ParseUser.getCurrentUser().getUsername(), Helpers.PARSE_OBJECT_VALUE);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() >= 1) {
                        mTxtInfo.setText(scoreList.get(scoreList.size() - 1).
                                getString(Helpers.PARSE_OBJECT_DATA_KEY));
                        Log.d("mydebug", "Retrieved " + scoreList.size() + " scores");
                    }

                } else {
                    Log.d("mydebug", "Error: " + e.getMessage());
                }
            }
        });
    }
}
