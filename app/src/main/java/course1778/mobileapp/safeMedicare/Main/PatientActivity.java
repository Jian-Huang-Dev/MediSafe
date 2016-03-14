package course1778.mobileapp.safeMedicare.Main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import course1778.mobileapp.safeMedicare.R;

/**
 * Created by jianhuang on 16-03-03.
 */
public class PatientActivity extends AppCompatActivity {

    private static Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_activity);

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
}
