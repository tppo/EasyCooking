package ru.petrsu.easycooking.recipyadder;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.*;
import java.util.LinkedList;

import org.w3c.dom.*;

/**
 * 
 * @author Anton Andreev
 *
 */
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

	/**
	 * Factory for document transforming
	 */
	TransformerFactory transformerFactory;

	/**
	 * Document transformer
	 */
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
	JLabel ingrLabel;
	JLabel tagLabel;
	JLabel timerLabel;

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
			JOptionPane.showMessageDialog(this, e.getMessage());
			System.exit(0);
		}
		// for newlining in xml-docs
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		initInterface();
	}

	/**
	 * Initializes interface structure
	 */
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

		recIngrList.addLast(new JTextField());
		recTimerList.addLast(new JTextField());
		recTagList.addLast(new JTextField());
		recIngrList.getLast().setColumns(10);
		recTimerList.getLast().setColumns(10);
		recTagList.getLast().setColumns(10);

		descrScrollPane = new JScrollPane(recDescrTA);

		addButton = new JButton();
		addAllButton = new JButton();
		addIngrButton = new JButton();
		deleteIngrButton = new JButton();
		addTimerButton = new JButton();
		deleteTimerButton = new JButton();
		addTagButton = new JButton();
		deleteTagButton = new JButton();

		ingrLabel = new JLabel("Ingredients");
		tagLabel = new JLabel("Tags");
		timerLabel = new JLabel("Timers");

		ingrPanel.setLayout(new FlowLayout());
		timerPanel.setLayout(new FlowLayout());
		tagsPanel.setLayout(new FlowLayout());
		panel.setLayout(new GridLayout(3, 2));
		buttonsPanel.setLayout(new FlowLayout());

		ingrPanel.add(ingrLabel);
		ingrPanel.add(addIngrButton);
		ingrPanel.add(deleteIngrButton);
		ingrPanel.add(recIngrList.getLast());

		timerPanel.add(timerLabel);
		timerPanel.add(addTimerButton);
		timerPanel.add(deleteTimerButton);
		timerPanel.add(recTimerList.getLast());

		tagsPanel.add(tagLabel);
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
		deleteIngrButton.setText("-");
		addTimerButton.setText("+");
		deleteTimerButton.setText("-");
		addTagButton.setText("+");
		deleteTagButton.setText("-");

		addIngrButton.addActionListener(this);
		deleteIngrButton.addActionListener(this);
		addTimerButton.addActionListener(this);
		deleteTimerButton.addActionListener(this);
		addTagButton.addActionListener(this);
		deleteTagButton.addActionListener(this);

		addButton.setText("Add recipe");
		addAllButton.setText("Add from new XMLs");
		addButton.addActionListener(this);
		addAllButton.addActionListener(this);

	}

	/**
	 * App initialization
	 */
	public void init() {
		setBounds(100, 100, 400, 400);

		File xmlDir = new File("./res/xml/");
		File resDir = new File("./res/");
		File newDir = new File("./res/xml/new");
		File oldDir = new File("./res/xml/old");

		if (!resDir.exists()) {
			resDir.mkdir();
		}
		if (!xmlDir.exists()) {
			xmlDir.mkdir();
		}
		if (!newDir.exists()) {
			newDir.mkdir();
		}
		if (!oldDir.exists()) {
			oldDir.mkdir();
		}

		// Operation on close
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				closeDB();
				dispose();// close frame
				System.exit(0);
			}
		});
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
			// connection to database
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:./res/ec.db");
			stmt = c.createStatement();

			// counting tables in database
			count = stmt.executeQuery(
					"SELECT count(*) FROM sqlite_master WHERE type = 'table';")
					.getInt(1);

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			JOptionPane.showMessageDialog(this, e.getMessage());
			System.exit(0);
		}

		System.out.println("Connected database successfully");

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
			JOptionPane.showMessageDialog(this, e.getMessage());
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
		String createBuyListTable = "CREATE TABLE tblBuyList "
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
				+ "(rec_id INTEGER NOT NULL REFERENCES tblRecipes (rec_id) ON DELETE CASCADE ON UPDATE CASCADE, "
				+ "tag_id INTEGER NOT NULL REFERENCES tblTags (tag_id) ON DELETE CASCADE ON UPDATE CASCADE);";
		
		String createIndexIngrName = "CREATE INDEX index_ingr_name ON tblIngredients (ingr_name);";
		String createIndexRecName = "CREATE INDEX index_rec_name ON tblRecipes (rec_name);";
		String createIndexTagName = "CREATE INDEX index_tag_name ON tblTags (tag_name);";
		
		try {
			stmt.executeUpdate(createMetadataTable);
			stmt.executeUpdate(createIngredientsTable);
			stmt.executeUpdate(createBuyListTable);
			stmt.executeUpdate(createRecipesTable);
			stmt.executeUpdate(createFavouriteListTable);
			stmt.executeUpdate(createRecIngrTable);
			stmt.executeUpdate(createTagsTable);
			stmt.executeUpdate(createRecTagTable);
			stmt.executeUpdate(createIndexTagName);
			stmt.executeUpdate(createIndexRecName);
			stmt.executeUpdate(createIndexIngrName);
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			JOptionPane.showMessageDialog(this, e.getMessage());
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
		int recId = -1;
		ResultSet rs = null;

		// doc parsing
		try {
			doc = dBuilder.parse(fXmlFile);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			JOptionPane.showMessageDialog(this, e.getMessage());
			System.exit(0);
		}

		doc.getDocumentElement().normalize(); // doc normalization

		String recName = doc.getElementsByTagName("name").item(0)
				.getTextContent().toLowerCase();

		String recDescr = doc.getElementsByTagName("description").item(0)
				.getTextContent();

		// timers will be in one string, delimiter is "+"
		String recTimers = "";
		NodeList timers = doc.getElementsByTagName("timer");
		for (int i = 0; i < timers.getLength(); i++) {
			recTimers += timers.item(i).getTextContent() + " ";
		}

		LinkedList<String> recIngr = new LinkedList<String>();
		NodeList ingrs = doc.getElementsByTagName("ingredient");
		for (int i = 0; i < ingrs.getLength(); i++) {
			recIngr.add(ingrs.item(i).getTextContent().toLowerCase());
		}

		LinkedList<String> recTag = new LinkedList<String>();
		NodeList tags = doc.getElementsByTagName("tag");
		for (int i = 0; i < tags.getLength(); i++) {
			recTag.add(tags.item(i).getTextContent().toLowerCase());
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
			JOptionPane.showMessageDialog(this, e.getMessage());
			System.exit(0);
		}

		// inserting ingredients
		for (String str : recIngr) {
			String selectIngr = "SELECT COUNT(*) FROM tblIngredients WHERE ingr_name = \""
					+ str + "\";";
			String selectIngrId = "SELECT ingr_id FROM tblIngredients WHERE ingr_name = \""
					+ str + "\";";

			int count = -1;
			int ingrId = -1;

			try {
				// Checking if ingredient already in DB
				rs = stmt.executeQuery(selectIngr);
				count = rs.getInt(1);
				rs.close();
				System.out.println("ingr count = " + count);
				if (count != 0) {
					rs = stmt.executeQuery(selectIngrId);
					ingrId = rs.getInt(1);
					rs.close();
				}

				// putting in if not in and making link to recipe
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
				JOptionPane.showMessageDialog(this, e.getMessage());
				System.exit(0);
			}
		}

		// inserting tags
		for (String str : recTag) {
			String selectTag = "SELECT COUNT(*) FROM tblTags WHERE tag_name = \""
					+ str + "\";";
			String selectTagId = "SELECT tag_id FROM tblTags WHERE tag_name = \""
					+ str + "\";";

			int count = -1;
			int tagId = -1;

			try {
				// checking if tag already in
				rs = stmt.executeQuery(selectTag);
				count = rs.getInt(1);
				rs.close();
				System.out.println("size = " + count);
				if (count != 0) {
					rs = stmt.executeQuery(selectTagId);
					tagId = rs.getInt(1);
					rs.close();
				}
				// inserting if not, making link to recipe
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
				JOptionPane.showMessageDialog(this, e.getMessage());
				System.exit(0);
			}
		}
	}

	/**
	 * Makes XML from input
	 * 
	 * @return Name of new XML-file
	 */
	public String createXmlFromInput() {
		// name and descr are required fields
		if (recNameTF.getText().isEmpty() || recDescrTA.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Empty name or descr fields");
			return null;
		}

		// init doc struct
		Document doc = dBuilder.newDocument();
		Element rootElement = doc.createElement("recipe");
		doc.appendChild(rootElement);

		Element recName = doc.createElement("name");
		recName.setTextContent(recNameTF.getText().toLowerCase());
		rootElement.appendChild(recName);

		Element recDescr = doc.createElement("description");
		recDescr.setTextContent(recDescrTA.getText());
		rootElement.appendChild(recDescr);

		// adding all list things
		for (JTextField tf : recIngrList) {
			if (!tf.getText().isEmpty()) {
				Element recIngr = doc.createElement("ingredient");
				recIngr.setTextContent(tf.getText().toLowerCase());
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
				recTag.setTextContent(tf.getText().toLowerCase());
				rootElement.appendChild(recTag);
			}
		}

		// transforming and writing to file
		DOMSource source = new DOMSource(doc);
		String filename = doc.hashCode() + ".xml";
		StreamResult result = new StreamResult(new File("./res/xml/new/"
				+ filename));
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return filename;
	}

	/**
	 * Moves handled XMLs to folder "old"
	 * 
	 * @param filename
	 *            - Name of XML-file
	 */
	public void moveXML(String filename) {
		try {
			File oldFile = new File("./res/xml/new/" + filename);

			if (oldFile
					.renameTo(new File("./res/xml/old/" + oldFile.getName()))) {
				System.out.println("The file " + filename
						+ " was moved successfully");
			} else {
				System.out.println("The File was not moved.");
			}

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			JOptionPane.showMessageDialog(this, e.getMessage());
			System.exit(0);
		}
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
			String fname = createXmlFromInput();
			if(fname!=null){
				addFromXML(fname);
				moveXML(fname);
			}
		}
		if (e.getSource() == addAllButton) {
			LinkedList<String> l = new LinkedList<String>();
			File[] xmlFiles = new File("./res/xml/new/").listFiles();

			for (File file : xmlFiles) {
				if (file.isFile()) {
					l.add(file.getName());
				}
			}
			for (String fname : l) {
				addFromXML(fname);
				moveXML(fname);
			}
		}
		//repaint needed to display addition or deletion of text fields
		this.paintComponents(getGraphics()); 	
	}
}
