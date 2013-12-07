package ru.petrsu.easycooking.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.webkit.JavascriptInterface;

public class DBProvider extends SQLiteOpenHelper {

	private static String DB_PATH;
	private static String DB_NAME = "ec.db";
	private SQLiteDatabase ecDB;
	private final Context appContext;

	public DBProvider(Context context) {
		super(context, DB_NAME, null, 1);
		this.appContext = context;

		//DB_PATH = appContext.getFilesDir().getPath()
	//			+ "/data/ru.petrsu.easycooking/databases/";
		DB_PATH = "/data/data/ru.petrsu.easycooking/databases/";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void close() {

		if (ecDB != null)
			ecDB.close();

		super.close(); 

	}

	private boolean checkDB() {
		File dbFile = new File(DB_PATH + DB_NAME);
		boolean check = dbFile.exists();
		System.out.println("existence="+check);
		dbFile = null;
		return check;
	}

	private void copyDB() throws IOException {

		this.getWritableDatabase();

		try {
			InputStream myInput = appContext.getAssets().open(DB_NAME);

			String outFileName = DB_PATH + DB_NAME;
			OutputStream myOutput = new FileOutputStream(outFileName);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}
			System.out.println("pp");
			myOutput.flush();
			myOutput.close();
			myInput.close();
			System.out.println("ff");
		} catch (IOException e) {
			throw new Error("Error copying database ; "+e.getMessage());
		}
	}

	public void openDB() throws SQLException, SQLiteException {
		String myPath = DB_PATH + DB_NAME;

		if (this.checkDB()) {
			ecDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		} else {
			try {
				this.copyDB();
			} catch (IOException ioe) {
				throw new Error("Unable to create database");
			}

			ecDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		}
	}
	
	/**
	 * getRecipes - recipes id-list getter
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getRecipes(){
		Cursor c = null;
		try{
			c = ecDB.query("tblRecipes", new String[]{"rec_id"}, null, null, null, null, null);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
		int count = c.getCount();

		String result = count + " ";
		
		for(int i = 0; i < count; i++){
			result += c.getInt(i) + " ";
		}
		
		return result;
	}
	
	/**
	 * getIngredients - ingredients id-list getter
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getIngredients(){
		Cursor c = null;
		try{
			c = ecDB.query("tblIngredients", new String[]{"ingr_id"}, null, null, null, null, null);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
		int count = c.getCount();

		String result = count + " ";
		
		for(int i = 0; i < count; i++){
			result += c.getInt(i) + " ";
		}
		
		return result;
	}
	
	/**
	 * getTags - Tags id-list getter
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getTags(){
		Cursor c = null;
		try{
			c = ecDB.query("tblTags", new String[]{"tag_id"}, null, null, null, null, null);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
		int count = c.getCount();

		String result = count + " ";
		
		for(int i = 0; i < count; i++){
			result += c.getInt(i) + " ";
		}
		
		return result;
	}
	
	/**
	 * getFavList - getter of id-list of recipes included in Favorite list
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getFavList(){
		Cursor c = null;
		try{
			c = ecDB.query("tblFavList", new String[]{"rec_id"}, null, null, null, null, null);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
		int count = c.getCount();

		String result = count + " ";
		
		for(int i = 0; i < count; i++){
			result += c.getInt(i) + " ";
		}
		
		return result;
	}
	
	/**
	 * getBuyList - getter of id-list of ingredients included in Buy list
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getBuyList(){
		Cursor c = null;
		try{
			c = ecDB.query("tblBuyList", new String[]{"ingr_id"}, null, null, null, null, null);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
		int count = c.getCount();

		String result = count + " ";
		
		for(int i = 0; i < count; i++){
			result += c.getInt(i) + " ";
		}
		
		return result;
	}
	
}
