package course1778.mobileapp.safeMedicare.Main;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseUser;

import course1778.mobileapp.safeMedicare.Authentication.LoginActivity;
import course1778.mobileapp.safeMedicare.Helpers.CustomAdapter;
import course1778.mobileapp.safeMedicare.R;

public class PatientActivity extends AppCompatActivity {
    private static Context context;
    private ListView drawerList;
    private ArrayAdapter<String> drawerAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private String activityTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_activity);

        // action bar set up
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        activityTitle = getTitle().toString();

        drawerList = (ListView)findViewById(R.id.navList);

        // setup
        addDrawerItems();
        setupDrawer();


        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            FragmentTransaction transaction;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {

                    case 0:
                        //Home page
                        PatientFrag patientFrag = new PatientFrag();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmentContainer, patientFrag);
                        transaction.addToBackStack(null);
                        // Commit the transaction
                        transaction.commit();
                        drawerLayout.closeDrawers();
                        break;

                    case 1:
                        // drug interaction page
                        DrugInteractions drugInterac = new DrugInteractions();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmentContainer, drugInterac);
                        transaction.addToBackStack(null);
                        // Commit the transaction
                        transaction.commit();
                        drawerLayout.closeDrawers();
                        break;

                    case 5:
                        // log out
                        ParseUser.logOut();
                        Intent intent = new Intent(getApplication(),
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    default:
                        break;
                }
            }
        });

        PatientFrag patientFrag = new PatientFrag();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, patientFrag);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
        context = getApplicationContext();
    }
    public static Context getContext()
    {
        return context;
    }

    private void addDrawerItems() {
        String[] osArray = { "Medication List", "Drug Interaction", "Food Interaction", "Supplement Interaction","Setting  ", "Log Out "  };
        //drawerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        int[] picArray = {R.drawable.patient, R.drawable.exclamation, R.drawable.food, R.drawable.supplement, R.drawable.setting, R.drawable.logout};
        drawerList.setAdapter(new CustomAdapter(this, osArray,picArray));
    }

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                null,
                R.string.drawer_open,
                R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(activityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Activate the navigation drawer toggle
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    // handle configuration change such as portrait <--> landscape mode change
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}