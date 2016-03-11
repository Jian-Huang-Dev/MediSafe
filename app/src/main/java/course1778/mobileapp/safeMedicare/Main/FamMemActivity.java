package course1778.mobileapp.safeMedicare.Main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import course1778.mobileapp.safeMedicare.R;

public class FamMemActivity extends AppCompatActivity {
    private static Context context;

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
        context = getApplicationContext();
    }
    public static Context getContext()
    {
        return context;
    }
}