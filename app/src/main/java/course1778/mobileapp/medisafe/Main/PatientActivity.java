package course1778.mobileapp.medisafe.Main;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import course1778.mobileapp.medisafe.R;

/**
 * Created by jianhuang on 16-03-03.
 */
public class PatientActivity extends AppCompatActivity {
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
    }
}
