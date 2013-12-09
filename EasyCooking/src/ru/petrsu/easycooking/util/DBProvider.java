package ru.petrsu.easycooking.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.webkit.JavascriptInterface;

/**
 * 
 * @author Anton Andreev
 *
 */
public class DBProvider extends SQLiteOpenHelper {

	/**
	 * Path to database in cache
	 */
	private static String DB_PATH;
	
	/**
	 * Name of database file
	 */
	private static String DB_NAME = "ec.db";
	
	/**
	 * EasyCooking database
	 */
	private SQLiteDatabase ecDB;
	
	/**
	 * Application context
	 */
	private final Context appContext;

	/**
	 * Constructs DBProvider by application context
	 * @param context Application context
	 */
	@SuppressLint("SdCardPath")
	public DBProvider(Context context) {
		super(context, DB_NAME, null, 1);
		this.appContext = context;

		//DB_PATH = appContext.getFilesDir().getPath()
		//		+ "/data/ru.petrsu.easycooking/databases/";
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

	/**
	 * Checks if database already exists in cache
	 * @return true If database exists in cache
	 * @return false If database does not exists in cache
	 */
	private boolean checkDB() {
		File dbFile = new File(DB_PATH + DB_NAME);
		boolean check = dbFile.exists();
		System.out.println("existence="+check);
		dbFile = null;
		return check;
	}

	/**
	 * Copies database file to cache
	 */
	private void copyDB() {

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
			
			myOutput.flush();
			myOutput.close();
			myInput.close();
		} catch (IOException e) {
			throw new Error("Error copying database ; " + e.getMessage());
		}
	}

	/**
	 * Opens database file
	 * @throws SQLiteException On opening issues
	 */
	public void openDB() throws SQLiteException {
		String myPath = DB_PATH + DB_NAME;

		if (!this.checkDB()) {
			this.copyDB();
		} 
		
		ecDB = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	/**
	 * getIDs - id-list getter
	 * @param tbl Table with needed info
	 * @param column ID-column 
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	private String getIDs(String tbl, String[] column) throws SQLException{
		Cursor c = ecDB.query(tbl, column, null, null, null, null, null);
		
		int count = c.getCount();

		String result = count + " ";
		
		for(int i = 0; i < count; i++){
			result += c.getInt(i) + " ";
		}
		
		return result;
	}
	
	/**
	 * getRecipes - recipes id-list getter
	 * @return String that contains count of id's and list of id's with " " separator
	 * @throws SQLException On error in query handling
	 */
	@JavascriptInterface
	public String getRecipes(){
		try{
			return getIDs("tblRecipes", new String[]{"rec_id"});
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getIngredients - ingredients id-list getter
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getIngredients(){
		try{
			return getIDs("tblIngredients", new String[]{"ingr_id"});
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getTags - Tags id-list getter
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getTags(){
		try{
			return getIDs("tblTags", new String[]{"tag_id"});
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getFavList - getter of id-list of recipes included in Favorite list
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getFavList(){
		try{
			return getIDs("tblFavList", new String[]{"rec_id"});
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getBuyList - getter of id-list of ingredients included in Buy list
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getBuyList(){
		try{
			return getIDs("tblBuyList", new String[]{"ingr_id"});
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getName - getter of name of element with specified id
	 * @param tbl Table with needed information
	 * @param id_col ID-column in table
	 * @param n_col Name-column
	 * @param id ID of needed element
	 * @return String Name of element
	 * @throws SQLException On error in query handling
	 */
	private String getName(String tbl, String[] n_col, String id_col, String id) throws SQLException{
		Cursor c = ecDB.query(tbl, n_col, id_col + "=" + id , null, null, null, null);

		String result = "";
		
		if(c.getCount() > 0)
		result = c.getString(0);
		
		return result;
	}
	
	/**
	 * getRecipeName - getter of name of recipe with specified id
	 * @return String Name of recipe
	 */
	@JavascriptInterface
	public String getRecipeName(String id){
		try{
			return getName("tblRecipes", new String[]{"rec_name"}, "rec_id", id);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getIngredientName - getter of name of ingredient with specified id
	 * @return String Name of ingredient
	 */
	@JavascriptInterface
	public String getIngredientName(String id){
		try{
			return getName("tblIngredients", new String[]{"ingr_name"}, "ingr_id", id);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getTagName - getter of name of recipe with specified id
	 * @return String Name of tag
	 */
	@JavascriptInterface
	public String getTagName(String id){
		try{
			return getName("tblTags", new String[]{"tag_name"}, "tag_id", id);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
}
