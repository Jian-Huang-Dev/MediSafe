package course1778.mobileapp.medisafe;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class FamMemActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fam_mem_activity);

        FamMemFrag listFrag = new FamMemFrag();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, listFrag);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }
}