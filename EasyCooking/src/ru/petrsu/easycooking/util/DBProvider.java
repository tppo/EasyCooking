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
	
	private final String recTableName = "tblRecipes";
	private final String ingrTableName = "tblIngredients";
	private final String tagTableName = "tblTags";
	private final String favTableName = "tblFavList";
	private final String buyTableName = "tblBuyList";
	private final String recIngrTableName = "tblRecIngr";
	private final String recTagTableName = "tblRecTag";

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
	 * @param where Selection
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	private String getIDs(String tbl, String column, String where) throws SQLException{
		Cursor c = ecDB.query(tbl, new String[]{column}, where, null, null, null, null);
		
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
			return getIDs(recTableName, "rec_id", null);
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
			return getIDs(ingrTableName, "ingr_id", null);
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
			return getIDs(tagTableName, "tag_id", null);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getFavList - getter of id-list of recipes included in Favorite list
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getFavouriteList(){
		try{
			return getIDs(favTableName, "rec_id", null);
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
			return getIDs(buyTableName, "ingr_id", null);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getColumn - getter of column for element with specified id
	 * @param tbl Table with needed information
	 * @param id_col ID-column in table
	 * @param n_col needed column
	 * @param id ID of needed element
	 * @return String column for element
	 * @throws SQLException On error in query handling
	 */
	private String getColumn(String tbl, String n_col, String id_col, String id) throws SQLException{
		Cursor c = ecDB.query(tbl, new String[]{n_col}, id_col + "=" + id , null, null, null, null);

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
			return getColumn(recTableName, "rec_name", "rec_id", id);
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
			return getColumn(ingrTableName, "ingr_name", "ingr_id", id);
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
			return getColumn(tagTableName, "tag_name", "tag_id", id);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getRecipeByIngredient - getter of recipes with specified ingredient 
	 * @param ingr Ingredient name
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getRecipeByIngredient(String ingr){
		Cursor c = null;
		int ingrId = 0;
		try{
			c = ecDB.query(ingrTableName, new String[]{"ingr_id"}, "ingr_name=" + ingr.toLowerCase(), null, null, null, null);
			if(c.getCount()>0){
				ingrId = c.getInt(0);
				c.close();
				c = ecDB.query(recIngrTableName, new String[]{"rec_id"}, "ingr_id=" + ingrId, null, null, null, null);
			}
		} catch(SQLException e){
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
	 * getRecipeByTag - getter of recipes with specified ingredient 
	 * @param tag Tag name
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getRecipeByTag(String tag){
		Cursor c = null;
		int tagId = 0;
		try{
			c = ecDB.query(tagTableName, new String[]{"tag_id"}, "tag_name=" + tag.toLowerCase(), null, null, null, null);
			if(c.getCount()>0){
				tagId = c.getInt(0);	
				c.close();
				c = ecDB.query(recTagTableName, new String[]{"rec_id"}, "tag_id=" + tagId, null, null, null, null);
			}
		} catch(SQLException e){
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
	 * getRecipeByName - getter of recipes with specified name 
	 * @param name Name pattern
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getRecipeByName(String name){
		try{
			return getIDs(recTableName, "rec_id", "rec_name LIKE '"+name.toLowerCase()+"%'");
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getId - getter of id of element with specified name
	 * @param tbl Table with needed information
	 * @param id_col ID-column in table
	 * @param n_col Name-column
	 * @param id ID of needed element
	 * @return Id of element or -1 on missed element
	 * @throws SQLException On error in query handling
	 */
	private int getId(String tbl, String id_col, String n_col, String name) throws SQLException{
		Cursor c = ecDB.query(tbl, new String[]{id_col}, n_col + "=" + name.toLowerCase() , null, null, null, null);

		int result = -1;
		
		if(c.getCount() > 0)
		result = c.getInt(0);
		
		return result;
	}
	
	/**
	 * getTagId - getter of id of tag with specified name
	 * @param name Tag name
	 * @return id of tag or -1 if there is no such tag
	 */
	@JavascriptInterface
	public int getTagId(String name){
		try{
			return getId(tagTableName, "tag_id", "tag_name", name.toLowerCase());
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getIngredientId - getter of id of tag with specified name
	 * @param name Ingredient name
	 * @return id of ingredient or -1 if there is no such ingredient
	 */
	@JavascriptInterface
	public int getIngredientId(String name){
		try{
			return getId(ingrTableName, "ingr_id", "ingr_name", name.toLowerCase());
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getDescription - getter of recipe description
	 * @param id Recipe id
	 * @return String with recipe description
	 */
	@JavascriptInterface
	public String getDescription(String id){
		try{
			return getColumn(recTableName, "rec_descr", "rec_id", id);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getTimers - getter of recipe timers
	 * @param id Recipe id
	 * @return String with recipe timers
	 */
	@JavascriptInterface
	public String getTimers(String id){
		try{
			return getColumn(recTableName, "rec_timers", "rec_id", id);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getRecipeIngredients - getter of id-list of ingredients of recipe
	 * @param id Recipe id
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getRecipeIngredients(String id){
		try{
			return getIDs(recIngrTableName, "ingr_id", "rec_id=" + id);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
	
	/**
	 * getRecipeTags - getter of id-list of tags of recipe
	 * @param id Recipe id
	 * @return String that contains count of id's and list of id's with " " separator
	 */
	@JavascriptInterface
	public String getRecipeTags(String id){
		try{
			return getIDs(recTagTableName, "tag_id", "rec_id=" + id);
		} catch (SQLException e){
			throw new Error(e.getMessage());
		}
	}
}
