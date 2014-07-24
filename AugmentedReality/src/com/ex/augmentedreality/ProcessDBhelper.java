package com.ex.augmentedreality;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ProcessDBhelper extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "ProcessValueDB";

	public ProcessDBhelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase dbP) {
		// SQL statement to create book table
		String CREATE_BOOK_TABLE = "CREATE TABLE process_value ( "
				+ "id TEXT, " + "temperature REAL, "
				+ "pressure REAL, " + "rpm REAL, " + "torque REAL, "
				+ "oil_level REAL )";

		// create process value table
		dbP.execSQL(CREATE_BOOK_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase dbP, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		dbP.execSQL("DROP TABLE IF EXISTS process_value");

		// create frezh process value table
		this.onCreate(dbP);
	}

	// ----------------------------------------------------------------------

	// Process Value table name
	private static final String TABLE_PROCESSVALUE = "process_value";

	// Process Value Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_TEMPERATURE = "temperature";
	private static final String KEY_PRESSURE = "pressure";
	private static final String KEY_RPM = "rpm";
	private static final String KEY_TORQUE = "torque"; // String?????????
	private static final String KEY_OIL_LEVEL = "oil_level";

	private static final String[] COLUMNS = {KEY_ID, KEY_TEMPERATURE, KEY_PRESSURE, // String?????????
			KEY_RPM, KEY_TORQUE, KEY_OIL_LEVEL };

	public void addProcessValue(ProcessValueTable processvalue) {
		Log.d("addProcessValue", processvalue.toString());
		// 1. get reference to writable database
		SQLiteDatabase dbP = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_ID, processvalue.getTemperature());
		values.put(KEY_TEMPERATURE, processvalue.getTemperature());
		values.put(KEY_PRESSURE, processvalue.getPressure());
		values.put(KEY_RPM, processvalue.getRPM());
		values.put(KEY_TORQUE, processvalue.getTorque());
		values.put(KEY_OIL_LEVEL, processvalue.getOil_Level());

		// 3. insert
		dbP.insert(TABLE_PROCESSVALUE, null, values);

		dbP.close();
	}

	public ProcessValueTable getProcessValue(String id) {

		// 1. get reference to readable DB
		SQLiteDatabase dbP = this.getReadableDatabase();

		// 2. build query
		Cursor cursor = dbP.query(TABLE_PROCESSVALUE, COLUMNS, null, null,
				null, null, null, null);

		// 3. If we got results get the first one
		if (cursor != null)
			cursor.moveToFirst();

		// 4. build book objects
		ProcessValueTable processvalue = new ProcessValueTable();
		// processvalue.setId(Integer.parseInt(cursor.getString(0)));

		processvalue.setId(Integer.parseInt(id));									
		processvalue.setTemperature(Float.parseFloat(cursor.getString(1)));
		processvalue.setPressure(Float.parseFloat(cursor.getString(2)));
		processvalue.setRPM(Float.parseFloat(cursor.getString(3)));
		processvalue.setTorque(Float.parseFloat(cursor.getString(4)));
		processvalue.setOil_Level(Float.parseFloat(cursor.getString(5)));
	 

		Log.d("getProcessValue(" + id + ")", processvalue.toString());

		return processvalue;

	}

}
