package ru.petrsu.easycooking.recipyadder;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;
import java.util.LinkedList;

import org.w3c.dom.*;

public class RecipeAdder extends JFrame implements ActionListener {

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
	LinkedList<JTextField> recTimerList;
	JScrollPane descrScrollPane;
	JButton addButton;
	JButton addAllButton;
	JButton addIngrButton;
	JButton deleteIngrButton;
	JButton addTimerButton;
	JButton deleteTimerButton;

	/**
	 * Main constructor
	 */
	public RecipeAdder() {
		super("EasyCooking RecipeAdder");
		
		c = null;
		stmt = null;
		dbFactory = DocumentBuilderFactory.newInstance();
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		initInterface();
	
	}
	
	private void initInterface(){
		//initializing interface
		recIngrList = new LinkedList<JTextField>();
		recTimerList = new LinkedList<JTextField>();
		
		recNameTF = new JTextField();
		recDescrTA = new JTextArea();
		descrScrollPane = new JScrollPane(recDescrTA);
		recIngrList.addLast(new JTextField());
		recTimerList.addLast(new JTextField());
		
		addButton = new JButton();
		addAllButton = new JButton();
		addIngrButton = new JButton();
		deleteIngrButton = new JButton();
		addTimerButton = new JButton();
		deleteTimerButton = new JButton();

		JPanel panel = new JPanel();
		JPanel timerPanel = new JPanel();
		JPanel ingrPanel = new JPanel();
		JPanel buttonsPanel = new JPanel();
		
		ingrPanel.setLayout(new FlowLayout());
		timerPanel.setLayout(new FlowLayout());
		panel.setLayout(new GridLayout(4,2));
		buttonsPanel.setLayout(new FlowLayout());
		
		ingrPanel.add(addIngrButton);
		ingrPanel.add(deleteIngrButton);
		ingrPanel.add(recIngrList.getLast());
		
		timerPanel.add(addTimerButton);
		timerPanel.add(deleteTimerButton);
		timerPanel.add(recTimerList.getLast());
		
		buttonsPanel.add(addButton);
		buttonsPanel.add(addAllButton);
		
		this.add(panel);
		
		panel.add(recNameTF);
		panel.add(new JPanel());
		panel.add(descrScrollPane);
		panel.add(ingrPanel);
		panel.add(timerPanel);
		panel.add(new JPanel());
		panel.add(buttonsPanel);
		
		addIngrButton.setText("+");
		deleteIngrButton.setText("-");
		addTimerButton.setText("+");
		deleteTimerButton.setText("-");
		
		addButton.setText("Add recipe");
		addAllButton.setText("Add from new XMLs");
		
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
		RecipeAdder app = new RecipeAdder();
		app.init();
		app.setVisible(true);

		if (!app.openDB()) {
			app.initDB();
		}
		app.createXmlFromInput();
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
		String createRecipesTable = "CREATE TABLE tblRecipes "
				+ "(rec_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ "rec_name TEXT NOT NULL, " + "rec_descr TEXT NOT NULL, "
				+ "rec_timers TEXT);";
		String createFavouriteListTable = "CREATE TABLE tblFavList "
				+ "(rec_id INTEGER PRIMARY KEY NOT NULL REFERENCES tblRecipes (rec_id) ON DELETE CASCADE ON UPDATE CASCADE);";
		String createRecIngrTable = "CREATE TABLE tblRecIngr "
				+ "(rec_id INTEGER NOT NULL UNIQUE REFERENCES tblRecipes (rec_id) ON DELETE CASCADE ON UPDATE CASCADE, "
				+ "ingr_id INTEGER NOT NULL UNIQUE REFERENCES tblIngredients (ingr_id) ON DELETE CASCADE ON UPDATE CASCADE);";
		String createTagsTable = "CREATE TABLE tblTags "
				+ "(tag_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ "tag_name TEXT NOT NULL UNIQUE);";
		String createRecTagTable = "CREATE TABLE tblRecTag "
				+ "(rec_id INTEGER NOT NULL UNIQUE REFERENCES tblRecipes (rec_id) ON DELETE CASCADE ON UPDATE CASCADE, "
				+ "tag_id INTEGER NOT NULL UNIQUE REFERENCES tblTags (tag_id) ON DELETE CASCADE ON UPDATE CASCADE);";
		try {
			stmt.executeUpdate(createMetadataTable);
			stmt.executeUpdate(createIngredientsTable);
			stmt.executeUpdate(createBayListTable);
			stmt.executeUpdate(createRecipesTable);
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
	
	public String createXmlFromInput() {
		if(recNameTF.getText() == "" || recDescrTA.getText() == ""){
			return "";
		}
		
		Document doc = dBuilder.newDocument();
		Element rootElement = doc.createElement("recipe");
		doc.appendChild(rootElement);
		
		Element recName = doc.createElement("name");
		recName.setTextContent(recNameTF.getText());
		rootElement.appendChild(recName);
		
		Element recDescr = doc.createElement("description");
		recDescr.setTextContent(recDescrTA.getText());
		rootElement.appendChild(recDescr);
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);	
		String filename = doc.hashCode() + ".xml";
		StreamResult result = new StreamResult(new File("./res/xml/new/" + filename));
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return filename;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == addButton){
			
		}
		
	}
}
