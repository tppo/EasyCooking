package ru.petrsu.easycooking.recipyadder;

import javax.swing.JFrame;

public class RecipyAdder extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public RecipyAdder(){
		super("EasyCooking RecipyAdder");
	}

	public void init(){
		setBounds(100, 100, 400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		RecipyAdder app = new RecipyAdder(); 
		app.init(); 
	    app.setVisible(true); 
	}
}
