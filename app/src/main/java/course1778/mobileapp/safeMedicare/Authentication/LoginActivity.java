package course1778.mobileapp.safeMedicare.Authentication;

/**
 * Created by jianhuang on 16-02-21.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import course1778.mobileapp.safeMedicare.Main.WelcomePage;
import course1778.mobileapp.safeMedicare.R;

public class LoginActivity extends Activity {
    // Declare Variables
    Button loginBtn, signupBtn;
    String usrnameTxt;
    String pwdTxt;
    EditText pwd;
    EditText usrname;

    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usrname = (EditText) findViewById(R.id.username);
        pwd = (EditText) findViewById(R.id.password);

        // login and signup buttons
        loginBtn = (Button) findViewById(R.id.loginBtn);
        signupBtn = (Button) findViewById(R.id.signupBtn);

        loginBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                usrnameTxt = usrname.getText().toString();
                pwdTxt = pwd.getText().toString();

                // Send data to Parse.com for verification
                ParseUser.logInInBackground(usrnameTxt, pwdTxt,
                        new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    // If user exist and authenticated, send user to WelcomePage.class
                                    Intent intent = new Intent(
                                            LoginActivity.this,
                                            WelcomePage.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),
                                            "Successfully Logged in",
                                            Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "No such user exist, please signup",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        // direct to sign up page
        signupBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
