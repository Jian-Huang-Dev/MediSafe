package course1778.mobileapp.safeMedicare.Authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import course1778.mobileapp.safeMedicare.Main.WelcomePage;
import course1778.mobileapp.safeMedicare.R;

/**
 * Created by jianhuang on 16-03-04.
 */
public class SignupActivity extends Activity {
    // Declare Variables
    Button registerBtn;
    ImageButton loginBtn;
    String usrnameTxt, phoneNumTxt, pwdTxt, pwdTxt2, emailTxt;
    EditText phoneNum, pwd, pwd2, usrname, email;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        usrname = (EditText) findViewById(R.id.usrname);
        phoneNum = (EditText) findViewById(R.id.phoneNum);
        email = (EditText) findViewById(R.id.email);
        pwd = (EditText) findViewById(R.id.pwd);
        pwd2 = (EditText) findViewById(R.id.pwd2);

        // register button
        registerBtn = (Button) findViewById(R.id.registerBtn);
        loginBtn = (ImageButton) findViewById(R.id.loginBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                usrnameTxt = usrname.getText().toString();
                phoneNumTxt = phoneNum.getText().toString();
                emailTxt = email.getText().toString();
                pwdTxt = pwd.getText().toString();
                pwdTxt2 = pwd2.getText().toString();

                // Force user to fill up the form
                if (usrnameTxt.equals("") ||
                        phoneNumTxt.equals("") ||
                        emailTxt.equals("") ||
                        pwdTxt.equals("") ||
                        pwdTxt2.equals("")) {

                    Toast.makeText(getApplicationContext(),
                            getString(R.string.msg_complete_form),
                            Toast.LENGTH_LONG).show();

                } else if (!pwdTxt.equals(pwdTxt2)) {// both pwds must match
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.msg_password_mismatch),
                            Toast.LENGTH_LONG).show();
                } else {
                    // Save new user data into Parse.com Data Storage
                    ParseUser user = new ParseUser();
                    user.setUsername(usrnameTxt);
                    // set user phone number into user object
                    user.put(usrnameTxt, phoneNumTxt);
                    user.setEmail(emailTxt);
                    user.setPassword(pwdTxt);

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.msg_signup_login_success),
                                        Toast.LENGTH_LONG).show();

                                // now login
                                loginNow(usrnameTxt, pwdTxt);

                            } else {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.msg_signup_error), Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginNow(String usrnameTxt, String pwdTxt) {
        ParseUser.logInInBackground(usrnameTxt, pwdTxt,
                new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // If user exist and authenticated, send user to WelcomePage.class
                            Intent intent = new Intent(
                                    SignupActivity.this,
                                    WelcomePage.class);
                            startActivity(intent);
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
}

