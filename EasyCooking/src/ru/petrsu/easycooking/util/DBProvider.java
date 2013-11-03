package ru.petrsu.easycooking.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBProvider extends SQLiteOpenHelper {

	private static String DB_PATH;
	private static String DB_NAME = "ec.db";
	private SQLiteDatabase ecDataBase;
	private final Context appContext;

	public DBProvider(Context context) {
		super(context, DB_NAME, null, 1);
		this.appContext = context;
		
		DB_PATH = appContext.getFilesDir().getPath() + "/data/ru.petrsu.easycooking/databases/";
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

		if (ecDataBase != null)
			ecDataBase.close();

		super.close();

	}

	private boolean checkDataBase() {
		File dbFile = new File(DB_PATH + DB_NAME);
		boolean check = dbFile.exists();
		dbFile = null;
		return check;
	}

	private void copyDataBase() throws IOException {

		// Открываем локальную БД как входящий поток
		InputStream myInput = appContext.getAssets().open(DB_NAME);

		// Путь ко вновь созданной БД
		String outFileName = DB_PATH + DB_NAME;

		// Открываем пустую базу данных как исходящий поток
		OutputStream myOutput = new FileOutputStream(outFileName);

		// перемещаем байты из входящего файла в исходящий
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// закрываем потоки
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	private void createDataBase() throws IOException {

		this.getWritableDatabase();

			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
	}

	public void openDataBase() throws SQLException,SQLiteException {
		String myPath = DB_PATH + DB_NAME;

		if (this.checkDataBase()) {
			ecDataBase = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		} else {
			try {
				this.createDataBase();
			} catch (IOException ioe) {
				throw new Error("Unable to create database");
			}

			ecDataBase = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		}
	}

}
