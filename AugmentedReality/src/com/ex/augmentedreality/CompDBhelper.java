package com.ex.augmentedreality;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ex.augmentedreality.ComponentTable;

public class CompDBhelper extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "ComponentDB";
	
	private static final String CREATE_COMP_TABLE = "CREATE TABLE components ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "SFI TEXT, " + "manufacturer TEXT, " + "LastFix TEXT, "
			+ "FixType TEXT);";
	
	private static final String CREATE_PROCESS_TABLE = "CREATE TABLE process_value ( "
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "temperature REAL, "
			+ "pressure REAL, " + "rpm REAL, " + "torque REAL, "
			+ "oil_level REAL);";

	public CompDBhelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create book table
		

		// create components table
		db.execSQL(CREATE_PROCESS_TABLE);
		db.execSQL(CREATE_COMP_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS process_value");
		db.execSQL("DROP TABLE IF EXISTS components");
		
		

		// create fresh components table
		this.onCreate(db);
	}

	// ----------------------------------------------------------------------

	// Components table name
	private static final String TABLE_COMPONENTS = "components";
	// Process values table name
	private static final String TABLE_PROCESSVALUE = "process_value";

	// Components Table Columns1 names
	private static final String KEY_ID = "id";
	private static final String KEY_SFI = "SFI";
	private static final String KEY_MANUFACTURER = "manufacturer";
	private static final String KEY_LASTFIX = "LastFix";
	private static final String KEY_FIXTYPE = "FixType";
	
	// Components Table Columns2 names
	private static final String KEY_TEMPERATURE = "temperature";
	private static final String KEY_PRESSURE = "pressure";
	private static final String KEY_RPM = "rpm";
	private static final String KEY_TORQUE = "torque"; 
	private static final String KEY_OIL_LEVEL = "oil_level";
	
	

	private static final String[] COLUMNS1 = { KEY_ID, KEY_SFI,
		KEY_MANUFACTURER, KEY_LASTFIX, KEY_FIXTYPE };
	
	private static final String[] COLUMNS2 = {KEY_ID,KEY_TEMPERATURE, KEY_PRESSURE, 
		KEY_RPM, KEY_TORQUE, KEY_OIL_LEVEL };

	
	
	
	// Methods for adding component and process values:
	
	public void addComponent(ComponentTable component) {
		Log.d("addComponent", component.toString());
		// 1. get reference to writable database
		SQLiteDatabase db = this.getWritableDatabase();
		
		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
	
		values.put(KEY_SFI, component.getSFI());
		values.put(KEY_MANUFACTURER, component.getManufacturer());
		values.put(KEY_LASTFIX, component.getLastFix());
		values.put(KEY_FIXTYPE, component.getFixType());
		
		

		// 3. insert
		db.insert(TABLE_COMPONENTS, null, values);

		db.close();
	}

	
	public void addProcessValue(ProcessValueTable processvalue) { 
		Log.d("addProcessValue", processvalue.toString());
		// 1. get reference to writable database
		SQLiteDatabase dbP = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();

		values.put(KEY_TEMPERATURE, processvalue.getTemperature());
		values.put(KEY_PRESSURE, processvalue.getPressure());
		values.put(KEY_RPM, processvalue.getRPM());
		values.put(KEY_TORQUE, processvalue.getTorque());
		values.put(KEY_OIL_LEVEL, processvalue.getOil_Level());
	
		// 3. insert
		dbP.insert(TABLE_PROCESSVALUE, null, values);

		dbP.close();
	}
	
	
	
	
	public ComponentTable getComponent(String SFI) {

		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();

		// 2. build query
		
		Cursor cursor = db.query(TABLE_COMPONENTS, COLUMNS1, null, null , null , null, SFI);

		// 3. If we got results get the first one
		if (cursor != null)
			cursor.moveToFirst();

		// 4. build component objects
		ComponentTable component = new ComponentTable();
		
		
	
		component.setId(Integer.parseInt(cursor.getString(0)));
		component.setSFI(cursor.getString(1));
		component.setManufacturer(cursor.getString(2));
		component.setLastFix(cursor.getString(3));
		component.setFixType(cursor.getString(4));

		Log.d("getComponent(" + SFI + ")", component.toString());

		return component;

	}
	
	

	public ProcessValueTable getProcessValue(String SFI) {

		// 1. get reference to readable DB
		SQLiteDatabase dbP = this.getReadableDatabase();

		// 2. build query
		Cursor cursor = dbP.query(TABLE_PROCESSVALUE, COLUMNS2, null, null , null , null, SFI);

		
		// 3. If we got results get the first one
		if (cursor != null)
			cursor.moveToFirst();

		// 4. build processValue objects
		ProcessValueTable processvalue = new ProcessValueTable();
	

		processvalue.setId(Integer.parseInt(cursor.getString(0)));									
		processvalue.setTemperature(Float.parseFloat(cursor.getString(1)));
		processvalue.setPressure(Float.parseFloat(cursor.getString(2)));
		processvalue.setRPM(Float.parseFloat(cursor.getString(3)));
		processvalue.setTorque(Float.parseFloat(cursor.getString(4)));
		processvalue.setOil_Level(Float.parseFloat(cursor.getString(5)));

	
		Log.d("getProcessValue(" + SFI + ")", processvalue.toString());

		return processvalue;

	}
	
	

}