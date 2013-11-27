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
		
		app.openDB();
		app.closeDB();
	}

	private void openDB() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			stmt = c.createStatement();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Opened database successfully");
	}

	private void closeDB() {
		try {
			stmt.close();
			c.close();
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Database closed successfully");
	}
}
