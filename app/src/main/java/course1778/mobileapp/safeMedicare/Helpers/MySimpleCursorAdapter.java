package course1778.mobileapp.safeMedicare.Helpers;

import android.content.Context;
import android.database.Cursor;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import course1778.mobileapp.safeMedicare.R;

/**
 * Created by lang on 27/03/16.
 */
public class MySimpleCursorAdapter extends SimpleCursorAdapter {

    public MySimpleCursorAdapter(Context context, int layout, Cursor cur,
                                 String[] from, int[] to) {
        super(context, layout, cur, from, to);
    }

    @Override public void setViewImage(ImageView iv, String text)
    {
        if (text.equals("6")) {
            iv.setImageResource(R.drawable.plaster);
        } else if (text.equals("2")) {
        iv.setImageResource(R.drawable.capsule);
        } else if (text.equals("3")) {
            iv.setImageResource(R.drawable.needle);
        } else if (text.equals("4")) {
            iv.setImageResource(R.drawable.three);
        } else if (text.equals("5")) {
            iv.setImageResource(R.drawable.powder);
        } else {
            iv.setImageResource(R.drawable.tablet);
        }
    }

}
