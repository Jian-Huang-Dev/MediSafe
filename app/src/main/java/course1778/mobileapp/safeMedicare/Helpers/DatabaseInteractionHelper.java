/***
 * Copyright (c) 2008-2012 CommonsWare, LLC
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain	a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 * by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 * <p/>
 * From _The Busy Coder's Guide to Android Development_
 * https://commonsware.com/Android
 */

package course1778.mobileapp.safeMedicare.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseInteractionHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "interactions.db";
	private static final int SCHEMA = 1;
	public static final String TABLE = "interactions";
	public static final String USR_NAME = "usr_name";
	public static final String DRUG_NAME = "drug_name";
	public static final String DRUG_INTERACTION = "drug_interaction";
	public static final String FOOD_INTERACTION = "food_interaction";
	public static final String SUPPLEMENT_INTERACTION = "supplement_interaction";
	public static final String DRUG_INTERACTION_SHOW = "drug_interaction_show";
	public static final String FOOD_INTERACTION_SHOW = "food_interaction_show";
	public static final String SUPPLEMENT_INTERACTION_SHOW = "supplement_interaction_show";
	public static final String DRUG_INTERACTION_SHOW_FALSE = "0";
	public static final String DRUG_INTERACTION_SHOW_TRUE = "1";

	public DatabaseInteractionHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
				"CREATE TABLE interactions " +
						"(usr_name TEXT, " +
						"drug_name TEXT, " +
						"drug_interaction TEXT, " +
						"food_interaction TEXT, " +
						"supplement_interaction TEXT, " +
						"drug_interaction_show TEXT, " +
						"food_interaction_show TEXT, " +
						"supplement_interaction_show TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion,
	                      int newVersion) {
		throw new RuntimeException("How did we get here?");
	}

	public Cursor getCursor() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from interactions", null);
		return res;
	}

	public void setDrugInteractionShow(String drugName, String drugInteractionName, String booleanString) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] args = new String[]{drugName, drugInteractionName};
		ContentValues cv = new ContentValues();
		cv.put(DRUG_INTERACTION_SHOW, booleanString);

		// update the field
		db.update(TABLE, cv, "drug_name=? AND drug_interaction=?", args);
	}

	public boolean isDrugInteractionExist(String drugName, String drugInteractionName) {
		String drugNameInDB, drugInteractionNameInDB;
		Cursor cursor = this.getCursor();
		cursor.moveToPosition(-1);
		while(cursor.moveToNext()) {
			drugNameInDB =
					cursor.getString(
							cursor.getColumnIndex(
									DatabaseInteractionHelper.DRUG_NAME));

			drugInteractionNameInDB =
					cursor.getString(
							cursor.getColumnIndex(
									DatabaseInteractionHelper.DRUG_INTERACTION));
			// if they both exist in dynamic interaction database
			if((drugName.equals(drugNameInDB) && drugInteractionName.equals(drugInteractionNameInDB)) ||
					(drugName.equals(drugInteractionNameInDB) && drugInteractionName.equals(drugNameInDB))) {
				return true;
			}
		}

		return false;
	}

//  public Boolean isDrugNameExitOnDB (String name) {
//    String medName;
//    Cursor cursor = this.getCursor();
//    cursor.moveToPosition(-1);
//
//    while(cursor.moveToNext()) {
//        medName = cursor.getString(cursor.getColumnIndex(DRUG_NAME));
//        if(medName.equals(name)) {
//            return true;
//        }
//    }
//      return false;
//  }

	public Boolean isItemExitOnDB(String thisItem, String ItemInDB) {
		String medHour;
		Cursor cursor = this.getCursor();
		cursor.moveToPosition(-1);

		while (cursor.moveToNext()) {
			medHour = cursor.getString(cursor.getColumnIndex(ItemInDB));
			if (medHour.equals(thisItem)) {
				return true;
			}
		}
		return false;
	}

//    public Boolean isMinOnDB (String min) {
//        String medMin;
//        Cursor cursor = this.getCursor();
//        cursor.moveToPosition(-1);
//
//        while(cursor.moveToNext()) {
//            medMin = cursor.getString(cursor.getColumnIndex(TIME_M));
//            if(medMin.equals(min)) {
//                return true;
//            }
//        }
//        return false;
//    }
}
