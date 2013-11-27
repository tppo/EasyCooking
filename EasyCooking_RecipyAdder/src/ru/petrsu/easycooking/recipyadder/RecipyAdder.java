package ru.petrsu.easycooking.recipyadder;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.LinkedList;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class RecipyAdder extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	/**
	 * Connection to database
	 */
	Connection c;
	/**
	 * Statement of database
	 */
	Statement stmt;
	
	/**
	 * Factory for XML proceeding
	 */
	DocumentBuilderFactory dbFactory;
	/**
	 * Builder for XML proceeding
	 */
	DocumentBuilder dBuilder;
	
	//interface
	JTextField recNameTF;
	JTextArea recDescrTA;
	LinkedList<JTextField> recIngrList;
	JScrollPane descrScrollPane;
	JButton addButton;
	JButton addAllButton;
	JButton addIngrButton;
	JButton deleteIngrButton;

	/**
	 * Main constructor
	 */
	public RecipyAdder() {
		super("EasyCooking RecipyAdder");
		
		c = null;
		stmt = null;
		
		dbFactory = DocumentBuilderFactory.newInstance();
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		//initializing interface
		recIngrList = new LinkedList<JTextField>();
		
		recNameTF = new JTextField();
		recDescrTA = new JTextArea();
		descrScrollPane = new JScrollPane(recDescrTA);
		recIngrList.addLast(new JTextField());
		
		addButton = new JButton();
		addAllButton = new JButton();
		addIngrButton = new JButton();
		deleteIngrButton = new JButton();
	}

	/**
	 * App initialization
	 */
	public void init() {
		setBounds(100, 100, 400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * App working
	 * @param args - arguments 
	 */
	public static void main(String[] args) {
		RecipyAdder app = new RecipyAdder();
		app.init();
		app.setVisible(true);

		if (!app.openDB()) {
			app.initDB();
		}
		app.closeDB();
	}

	/**
	 * Opens database
	 * @return true If database already exists
	 * @return false If database file not found
	 */
	private boolean openDB() {
		System.out.println("Connecting to database");
		int count = 0;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:./res/test.db");
			stmt = c.createStatement();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Connected database successfully");

		//getting count of tables to check if the file is empty
		try {
			count = stmt.executeQuery(
					"SELECT count(*) FROM sqlite_master WHERE type = 'table';")
					.getInt(1);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		//deciding if database is new
		System.out.println("count = " + count);
		if (count > 3) {
			System.out.println("Database alredy exists");
			return true;
		} else {
			System.out.println("Database not exists");
			return false;
		}
	}

	/**
	 * Closes database
	 */
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

	/**
	 * Initializes database structure
	 */
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
	
	/**
	 * Adds recipy from XML-file to database
	 * @param filename - XML-file name
	 */
	public void addFromXML(String filename){
		File fXmlFile = new File("./res/xml/"+filename);
		Document doc = null;
		try {
			doc = dBuilder.parse(fXmlFile);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		doc.getDocumentElement().normalize();
		
		String recName = doc.getElementsByTagName("name").item(0).getTextContent();
		LinkedList<String> recIngr = new LinkedList<String>();
		NodeList ingrs = doc.getElementsByTagName("ingredient");
		for(int i = 0; i<ingrs.getLength(); i++){
			recIngr.add(ingrs.item(i).getTextContent());
		}
		
		String recDescr = doc.getElementsByTagName("description").item(0).getTextContent();
		
		String recTimers = "";
		NodeList timers = doc.getElementsByTagName("timer");
		for(int i = 0; i<timers.getLength(); i++){
			recTimers += timers.item(i).getTextContent() + ";";
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		
	}
}
