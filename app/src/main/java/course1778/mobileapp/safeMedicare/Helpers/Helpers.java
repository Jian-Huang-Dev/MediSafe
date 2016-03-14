package course1778.mobileapp.safeMedicare.Helpers;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import course1778.mobileapp.safeMedicare.R;

/**
 * Created by jianhuang on 16-02-21.
 */
public class Helpers {
    public static final String PARSE_OBJECT = "parse_object";
    public static final String PARSE_OBJECT_USER = "user";
    public static final String PARSE_OBJECT_VALUE = "my_value";
    public static final String PARSE_OBJECT_DATA_KEY = "data";
    public static final String NOFITY_ID = "notify_id";

    /**
     * Read details from a file
     * @param full_file_name
     * @return
     */
    public static String readFile(Context context, String full_file_name) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(full_file_name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // convert stringbuilder to string and split the whole file into multiple people
        return sb.toString();
    }

    /** Write ArrayList<Strin> into file with user defined file name
     *
     * @param string_array_list
     * @param file_name
     */
    public static void writeToFile(Context context, ArrayList<String> string_array_list, String file_name) {
        FileOutputStream outputStream;
        String string_to_write;
        String full_file_name = file_name + context.getString(R.string.file_format);
        try {
            outputStream = context.openFileOutput(full_file_name, Context.MODE_PRIVATE);

            string_to_write = Arrays.toString(string_array_list.toArray());

            outputStream.write(string_to_write.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
