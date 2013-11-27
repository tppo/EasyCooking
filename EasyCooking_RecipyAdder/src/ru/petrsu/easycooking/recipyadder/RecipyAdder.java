package ru.petrsu.easycooking.recipyadder;

import javax.swing.JFrame;

import java.sql.*;

public class RecipyAdder extends JFrame {

	private static final long serialVersionUID = 1L;

	Connection c;
	Statement stmt;

	public RecipyAdder() {
		super("EasyCooking RecipyAdder");
	}

	public void init() {
		setBounds(100, 100, 400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c = null;
		stmt = null;
	}

	public static void main(String[] args) {
		RecipyAdder app = new RecipyAdder();
		app.init();
		app.setVisible(true);

		if (!app.openDB()) {
			app.initDB();
		}
		app.closeDB();
	}

	private boolean openDB() {
		System.out.println("Connecting to database");
		int count = 0;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			stmt = c.createStatement();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Connected database successfully");

		try {
			count = stmt.executeQuery(
					"SELECT count(*) FROM sqlite_master WHERE type = 'table';")
					.getInt(1);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		System.out.println("count = " + count);
		if (count > 3) {
			System.out.println("Database alredy exists");
			return true;
		} else {
			System.out.println("Database not exists");
			return false;
		}
	}

	private void closeDB() {
		System.out.println("Closing database");
		try {
			stmt.close();
			c.close();
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Database closed successfully");
	}

	private void initDB() {
		System.out.println("Initializing database structure");
		
		String createMetadataTable = "CREATE TABLE android_metadata "
				+ "(locale TEXT DEFAULT ru_RU);";
		String createIngredientsTable = "CREATE TABLE tblIngredients "
				+ "(ingr_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ "ingr_name TEXT NOT NULL UNIQUE);";
		String createBayListTable = "CREATE TABLE tblBayList "
				+ "(ingr_id INTEGER PRIMARY KEY NOT NULL REFERENCES tblIngredients (ingr_id) ON DELETE CASCADE ON UPDATE CASCADE);";
		String createRecipiesTable = "CREATE TABLE tblRecepies "
				+ "(rec_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ "rec_name TEXT NOT NULL, " + "rec_descr TEXT NOT NULL, "
				+ "rec_timers TEXT);";
		String createFavouriteListTable = "CREATE TABLE tblFavList "
				+ "(rec_id INTEGER PRIMARY KEY NOT NULL REFERENCES tblRecepies (rec_id) ON DELETE CASCADE ON UPDATE CASCADE);";
		String createRecIngrTable = "CREATE TABLE tblRecIngr "
				+ "(rec_id INTEGER NOT NULL UNIQUE REFERENCES tblRecepies (rec_id) ON DELETE CASCADE ON UPDATE CASCADE, "
				+ "ingr_id INTEGER NOT NULL UNIQUE REFERENCES tblIngredients (ingr_id) ON DELETE CASCADE ON UPDATE CASCADE);";
		String createTagsTable = "CREATE TABLE tblTags "
				+ "(tag_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ "tag_name TEXT NOT NULL UNIQUE);";
		String createRecTagTable = "CREATE TABLE tblRecTag "
				+ "(rec_id INTEGER NOT NULL UNIQUE REFERENCES tblRecepies (rec_id) ON DELETE CASCADE ON UPDATE CASCADE, "
				+ "tag_id INTEGER NOT NULL UNIQUE REFERENCES tblTags (tag_id) ON DELETE CASCADE ON UPDATE CASCADE);";
		try {
			stmt.executeUpdate(createMetadataTable);
			stmt.executeUpdate(createIngredientsTable);
			stmt.executeUpdate(createBayListTable);
			stmt.executeUpdate(createRecipiesTable);
			stmt.executeUpdate(createFavouriteListTable);
			stmt.executeUpdate(createRecIngrTable);
			stmt.executeUpdate(createTagsTable);
			stmt.executeUpdate(createRecTagTable);
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		System.out.println("Database structure succesfully initialized");
	}
}
