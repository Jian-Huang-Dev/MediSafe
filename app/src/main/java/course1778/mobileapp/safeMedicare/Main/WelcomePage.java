package course1778.mobileapp.safeMedicare.Main;

/**
 * Created by jianhuang on 16-02-21.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import course1778.mobileapp.safeMedicare.Helpers.Helpers;
import course1778.mobileapp.safeMedicare.R;

public class WelcomePage extends Activity {

    // Declare Variable
    LinearLayout famMemPageBtn, patientPageBtn;
    ImageButton famMemBtn, patientBtn;
//    Button logoutBtn;
    TextView welcomeMsg;
    String usrName;
//    EditText mTxtBox;
//    ParseObject myObject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        // Retrieve current user from Parse.com
        ParseUser currentUser = ParseUser.getCurrentUser();
        // make current user's information not visible to others
        currentUser.setACL(new ParseACL(currentUser));
        usrName = currentUser.getUsername().toString();

        welcomeMsg = (TextView) findViewById(R.id.welcomMsg);

        // family member page and patient page buttons
        famMemPageBtn = (LinearLayout) findViewById(R.id.famMemPageBtn);
        patientPageBtn = (LinearLayout) findViewById(R.id.patientPageBtn);
        patientBtn = (ImageButton) findViewById(R.id.patienBtn);
        famMemBtn = (ImageButton) findViewById(R.id.famMemBtn);
//        logoutBtn = (Button) findViewById(R.id.logoutBtn);

        // display welcome message
        welcomeMsg.setText("Welcome: " + usrName);

        // Logout Button Click Listener
//        logoutBtn.setOnClickListener(new OnClickListener() {
//
//            public void onClick(View arg0) {
//                // Logout current user
//                ParseUser.logOut();
//                Intent intent = new Intent(getApplication(),
//                        LoginActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        // direct to family member page upon click
        famMemPageBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, FamMemActivity.class);
                startActivity(intent);
            }
        });

        famMemBtn.setOnClickListener(new OnClickListener() {
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

        patientBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, FamMemActivity.class);
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
                        Log.d("mydebug", "Retrieved " + scoreList.size() + " scores");
                    }

                } else {
                    Log.d("mydebug", "Error: " + e.getMessage());
                }
            }
        });
    }
}
