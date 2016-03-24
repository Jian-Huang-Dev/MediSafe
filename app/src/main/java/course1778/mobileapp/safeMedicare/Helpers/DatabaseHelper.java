/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package course1778.mobileapp.safeMedicare.Helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME="notification.db";
  private static final int SCHEMA=1;
  public static final String TITLE="title";
  public static final String TIME_H="time_h";
  public static final String TIME_M="time_m";
  public static final String FREQUENCY="frequency";
  public static final String DAY="day";
  public static final String DOSAGE="dosage";
  public static final String INSTRUCTION="instruction";
  public static final String SHAPE="shape";
//  public static final String MONDAY="monday";
//  public static final String TUESDAY="tuesday";
//  public static final String WEDNESDAY="wednesday";
//  public static final String THURSDAY="thursday";
//  public static final String FRIDAY="friday";
//  public static final String SATURDAY="saturday";
//  public static final String SUNDAY="sunday";
  public static final String TABLE="notification";

  // for preloaded database "medicine.db"
  public static final String SHEET_1_DRUG_NAMES = "Drugs";
  public static final String SHEET_1_DRUG_INTERACTIONS = "Interactions";
  public static final String SHEET_1_INTERACTION_RESULT = "What_might_happen";

  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, SCHEMA);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE notification (title TEXT, time_h REAL, time_m REAL, dosage REAL, frequency INTEGER, day INTEGER, instruction TEXT, shape INTEGER);");
    //monday INTEGER, tuesday INTEGER, wednesday INTEGER, thursday INTEGER, friday INTEGER, saturday INTEGER, sunday INTEGER,);");


  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion,
                        int newVersion) {
    throw new RuntimeException("How did we get here?");
  }

  public Cursor getCursor(){
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor res = db.rawQuery( "select * from notification", null );
    return res;
  }

  public Boolean isNameExitOnDB (String name) {
    String medName;
    Cursor cursor = this.getCursor();
    cursor.moveToPosition(-1);

    while(cursor.moveToNext()) {
        medName = cursor.getString(cursor.getColumnIndex(TITLE));
        if(medName.equals(name)) {
            return true;
        }
    }
      return false;
  }
}
