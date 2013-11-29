package ru.petrsu.easycooking.recipyadder;

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

import java.awt.FlowLayout;
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

	TransformerFactory transformerFactory;
	Transformer transformer;

	// interface
	JTextField recNameTF;
	JTextArea recDescrTA;
	LinkedList<JTextField> recIngrList;
	LinkedList<JTextField> recTimerList;
	LinkedList<JTextField> recTagList;
	JScrollPane descrScrollPane;
	JButton addButton;
	JButton addAllButton;
	JButton addIngrButton;
	JButton deleteIngrButton;
	JButton addTimerButton;
	JButton deleteTimerButton;
	JButton addTagButton;
	JButton deleteTagButton;
	JPanel panel;
	JPanel timerPanel;
	JPanel ingrPanel;
	JPanel buttonsPanel;
	JPanel tagsPanel;

	/**
	 * Main constructor
	 */
	public RecipeAdder() {
		super("EasyCooking RecipeAdder");

		c = null;
		stmt = null;
		dbFactory = DocumentBuilderFactory.newInstance();
		transformerFactory = TransformerFactory.newInstance();
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			transformer = transformerFactory.newTransformer();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		initInterface();

	}

	private void initInterface() {
		// initializing interface
		panel = new JPanel();
		timerPanel = new JPanel();
		ingrPanel = new JPanel();
		buttonsPanel = new JPanel();
		tagsPanel = new JPanel();

		recIngrList = new LinkedList<JTextField>();
		recTimerList = new LinkedList<JTextField>();
		recTagList = new LinkedList<JTextField>();

		recNameTF = new JTextField();
		recDescrTA = new JTextArea();
		descrScrollPane = new JScrollPane(recDescrTA);
		recIngrList.addLast(new JTextField());
		recTimerList.addLast(new JTextField());
		recIngrList.getLast().setColumns(10);
		recTimerList.getLast().setColumns(10);
		recTagList.addLast(new JTextField());
		recTagList.getLast().setColumns(10);

		addButton = new JButton();
		addAllButton = new JButton();
		addIngrButton = new JButton();
		deleteIngrButton = new JButton();
		addTimerButton = new JButton();
		deleteTimerButton = new JButton();
		addTagButton = new JButton();
		deleteTagButton = new JButton();

		ingrPanel.setLayout(new FlowLayout());
		timerPanel.setLayout(new FlowLayout());
		tagsPanel.setLayout(new FlowLayout());
		panel.setLayout(new GridLayout(3, 2));
		buttonsPanel.setLayout(new FlowLayout());

		ingrPanel.add(addIngrButton);
		ingrPanel.add(deleteIngrButton);
		ingrPanel.add(recIngrList.getLast());

		timerPanel.add(addTimerButton);
		timerPanel.add(deleteTimerButton);
		timerPanel.add(recTimerList.getLast());

		tagsPanel.add(addTagButton);
		tagsPanel.add(deleteTagButton);
		tagsPanel.add(recTagList.getLast());

		buttonsPanel.add(addButton);
		buttonsPanel.add(addAllButton);

		this.add(panel);

		panel.add(recNameTF);
		panel.add(tagsPanel);
		panel.add(descrScrollPane);
		panel.add(ingrPanel);
		panel.add(timerPanel);
		panel.add(buttonsPanel);

		addIngrButton.setText("+");
		addIngrButton.addActionListener(this);
		deleteIngrButton.setText("-");
		deleteIngrButton.addActionListener(this);
		addTimerButton.setText("+");
		addTimerButton.addActionListener(this);
		deleteTimerButton.setText("-");
		deleteTimerButton.addActionListener(this);
		addTagButton.setText("+");
		addTagButton.addActionListener(this);
		deleteTagButton.setText("-");
		deleteTagButton.addActionListener(this);

		addButton.setText("Add recipe");
		addButton.addActionListener(this);
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
	 * 
	 * @param args
	 *            - arguments
	 */
	public static void main(String[] args) {
		RecipeAdder app = new RecipeAdder();
		app.init();
		app.setVisible(true);

		if (!app.openDB()) {
			app.initDB();
		}
		// app.closeDB();
	}

	/**
	 * Opens database
	 * 
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

		// getting count of tables to check if the file is empty
		try {
			count = stmt.executeQuery(
					"SELECT count(*) FROM sqlite_master WHERE type = 'table';")
					.getInt(1);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		// deciding if database is new
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
				+ "rec_name TEXT UNIQUE NOT NULL, "
				+ "rec_descr TEXT NOT NULL, " + "rec_timers TEXT);";
		String createFavouriteListTable = "CREATE TABLE tblFavList "
				+ "(rec_id INTEGER PRIMARY KEY NOT NULL REFERENCES tblRecipes (rec_id) ON DELETE CASCADE ON UPDATE CASCADE);";
		String createRecIngrTable = "CREATE TABLE tblRecIngr "
				+ "(rec_id INTEGER NOT NULL REFERENCES tblRecipes (rec_id) ON DELETE CASCADE ON UPDATE CASCADE, "
				+ "ingr_id INTEGER NOT NULL REFERENCES tblIngredients (ingr_id) ON DELETE CASCADE ON UPDATE CASCADE);";
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
	 * Adds recipe from XML-file to database
	 * 
	 * @param filename
	 *            - XML-file name
	 */
	public void addFromXML(String filename) {
		File fXmlFile = new File("./res/xml/new/" + filename);
		Document doc = null;
		try {
			doc = dBuilder.parse(fXmlFile);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		doc.getDocumentElement().normalize();

		int recId = -1;
		ResultSet rs = null;

		String recName = doc.getElementsByTagName("name").item(0)
				.getTextContent();

		String recDescr = doc.getElementsByTagName("description").item(0)
				.getTextContent();

		String recTimers = "";
		NodeList timers = doc.getElementsByTagName("timer");
		for (int i = 0; i < timers.getLength(); i++) {
			recTimers += timers.item(i).getTextContent() + "+";
		}

		LinkedList<String> recIngr = new LinkedList<String>();
		NodeList ingrs = doc.getElementsByTagName("ingredient");
		for (int i = 0; i < ingrs.getLength(); i++) {
			recIngr.add(ingrs.item(i).getTextContent());
		}

		LinkedList<String> recTag = new LinkedList<String>();
		NodeList tags = doc.getElementsByTagName("tag");
		for (int i = 0; i < tags.getLength(); i++) {
			recTag.add(tags.item(i).getTextContent());
		}

		String insertInRecipes = "INSERT INTO tblRecipes (rec_name, rec_descr, rec_timers) "
				+ "VALUES (\""
				+ recName
				+ "\", \""
				+ recDescr
				+ "\", \""
				+ recTimers + "\");";

		try {
			stmt.executeUpdate(insertInRecipes);
			recId = stmt.executeQuery(
					"SELECT rec_id FROM tblRecipes WHERE rec_name = \""
							+ recName + "\";").getInt(1);
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		for (String str : recIngr) {
			String selectIngr = "SELECT COUNT(*) FROM tblIngredients WHERE ingr_name = \""
					+ str + "\";";
			String selectIngrId = "SELECT ingr_id FROM tblIngredients WHERE ingr_name = \""
					+ str + "\";";
			int count = -1;
			int ingrId = -1;
			try {
				rs = stmt.executeQuery(selectIngr);
				count = rs.getInt(1);
				rs.close();
				System.out.println("size = " + count);
				if (count != 0) {
					rs = stmt.executeQuery(selectIngrId);
					ingrId = rs.getInt(1);
					rs.close();
				}
			} catch (SQLException e) {
				System.err.println(e.getClass().getName() + ": "
						+ e.getMessage());
				System.exit(0);
			}
			System.out.println("ingrid = " + ingrId);
			try {
				if (ingrId == -1) {
					stmt.executeUpdate("INSERT INTO tblIngredients (ingr_name) VALUES (\""
							+ str + "\");");
					rs = stmt.executeQuery(selectIngrId);
					ingrId = rs.getInt(1);
					rs.close();
				}
				stmt.executeUpdate("INSERT INTO tblRecIngr (rec_id, ingr_id) VALUES ("
						+ recId + ", " + ingrId + ");");
			} catch (SQLException e) {
				System.err.println(e.getClass().getName() + ": "
						+ e.getMessage());
				System.exit(0);
			}
		}

		for (String str : recTag) {
			String selectTag = "SELECT COUNT(*) FROM tblTags WHERE tag_name = \""
					+ str + "\";";
			String selectTagId = "SELECT tag_id FROM tblTags WHERE tag_name = \""
					+ str + "\";";
			int count = -1;
			int tagId = -1;
			try {
				rs = stmt.executeQuery(selectTag);
				count = rs.getInt(1);
				rs.close();
				System.out.println("size = " + count);
				if (count != 0) {
					rs = stmt.executeQuery(selectTagId);
					tagId = rs.getInt(1);
					rs.close();
				}
			} catch (SQLException e) {
				System.err.println(e.getClass().getName() + ": "
						+ e.getMessage());
				System.exit(0);
			}
			System.out.println("tagid = " + tagId);
			try {
				if (tagId == -1) {
					stmt.executeUpdate("INSERT INTO tblTags (tag_name) VALUES (\""
							+ str + "\");");
					rs = stmt.executeQuery(selectTagId);
					tagId = rs.getInt(1);
					rs.close();
				}
				stmt.executeUpdate("INSERT INTO tblRecTag (rec_id, tag_id) VALUES ("
						+ recId + ", " + tagId + ");");

			} catch (SQLException e) {
				System.err.println(e.getClass().getName() + ": "
						+ e.getMessage());
				System.exit(0);
			}
		}

	}

	public String createXmlFromInput() {
		if (recNameTF.getText().isEmpty() || recDescrTA.getText().isEmpty()) {
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

		for (JTextField tf : recIngrList) {
			if (!tf.getText().isEmpty()) {
				Element recIngr = doc.createElement("ingredient");
				recIngr.setTextContent(tf.getText());
				rootElement.appendChild(recIngr);
			}
		}

		for (JTextField tf : recTimerList) {
			if (!tf.getText().isEmpty()) {
				Element recTimer = doc.createElement("timer");
				recTimer.setTextContent(tf.getText());
				rootElement.appendChild(recTimer);
			}
		}

		for (JTextField tf : recTagList) {
			if (!tf.getText().isEmpty()) {
				Element recTag = doc.createElement("tag");
				recTag.setTextContent(tf.getText());
				rootElement.appendChild(recTag);
			}
		}

		DOMSource source = new DOMSource(doc);
		String filename = doc.hashCode() + ".xml";
		StreamResult result = new StreamResult(new File("./res/xml/new/"
				+ filename));
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
		if (e.getSource() == addIngrButton) {
			recIngrList.addLast(new JTextField());
			recIngrList.getLast().setColumns(10);
			ingrPanel.add(recIngrList.getLast());
		}
		if (e.getSource() == deleteIngrButton) {
			if (recIngrList.size() > 1) {
				ingrPanel.remove(recIngrList.getLast());
				recIngrList.removeLast();
			}
		}
		if (e.getSource() == addTimerButton) {
			recTimerList.addLast(new JTextField());
			recTimerList.getLast().setColumns(10);
			timerPanel.add(recTimerList.getLast());
		}
		if (e.getSource() == deleteTimerButton) {
			if (recTimerList.size() > 1) {
				timerPanel.remove(recTimerList.getLast());
				recTimerList.removeLast();
			}
		}
		if (e.getSource() == addTagButton) {
			recTagList.addLast(new JTextField());
			recTagList.getLast().setColumns(10);
			tagsPanel.add(recTagList.getLast());
		}
		if (e.getSource() == deleteTagButton) {
			if (recTagList.size() > 1) {
				tagsPanel.remove(recTagList.getLast());
				recTagList.removeLast();
			}
		}
		if (e.getSource() == addButton) {
			addFromXML(createXmlFromInput());
		}
		this.paintComponents(getGraphics());
	}
}
