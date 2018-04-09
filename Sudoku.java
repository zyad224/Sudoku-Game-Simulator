
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * This class implements the Sudoku JFrame including the
 * buttons(Load,Run,Interrupt,Clear,Exit), status console, and the grid layout
 * that is responsible to hold the numbers.
 * 
 * This class contains the actionPerformed function to handle different button
 * clicks. When the user clicks each time on load or run buttons, new threads
 * start to load the file and to run the solver in order not to block the GUI.
 * 
 * This class contains 2 functions: makeButton() and actionPerformed()
 *
 * @author Zeyad Abdelwahab
 * @version 1.0
 *
 */

public class Sudoku extends JFrame implements ActionListener {

	private JPanel rowOfButtons; // row of buttons
	private String[] buttonNames = { "LOAD", "RUN", "INTERRUPT", "CLEAR", "QUIT" };
	ArrayList<ArrayList<String>> fileData; // 2D array list to fill the numbers from the file into it
	Board b; // Game Board
	StatusConsole s; // status console
	Thread solver; // thread responsible to run the solving procedure

	/**
	 * Constructor of the Sudoku Game. it initialize the width and height of the
	 * Sudoku JFrame, it creates new object of the Status Console, Board, and row of
	 * clickable buttons.
	 * 
	 * Status Console: its responsible to show the user the different states of the game.
	 * Board: its responsible to create the 9x9 grid for the game row of buttons.
	 * buttons: its the row that contains different buttons with different functions.
	 * 
	 */
	public Sudoku() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		this.setBounds((int) width / 4, (int) height / 4, (int) width / 2, (int) height / 2);
		Container contentPane = this.getContentPane();

		// creates a new status console and add it to the content pane
		s = new StatusConsole();
		contentPane.add(s.getStatusConsole(), BorderLayout.SOUTH);

		// creates the row of buttons and add it to the content pane
		JPanel j = new JPanel(new GridLayout(1, 5));
		rowOfButtons = makeButton(j, buttonNames, this);
		contentPane.add(rowOfButtons, BorderLayout.NORTH);

		// creates the board of the game and add it to the content pane
		b = new Board();
		contentPane.add(b.getPlayingBoardPanel(), BorderLayout.CENTER);

	}

	/**
	 * This function is responsible to create buttons with their action listener and
	 * add each button in the JPanel
	 * 
	 * @param p panel that holds the buttons
	 *            
	 * @param buttonName array of button names
	 *            
	 * @param target action listener target
	 *            
	 * @return JPanel full of clickable buttons
	 */
	private JPanel makeButton(JPanel p, String[] buttonName, ActionListener target) {

		for (String s : buttonName) {
			JButton b = new JButton(s);
			p.add(b);
			b.addActionListener(target);
		}

		return p;
	}

	/**
	 * This is the action performed function. This function handles the clicks of
	 * the buttons. 
	 * LOAD : start SudokuFileReader in a new thread.
	 * RUN: start Solver in a new thread .
	 * INTERRUPT: interrupt the solver. 
	 * CLEAR: clear the board from the numbers. 
	 * QUIT: close the program.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		String command = e.getActionCommand();

		switch (command) {
		case "LOAD":
			fileData = new ArrayList<ArrayList<String>>();
			new Thread(new SudokuFileReader(fileData, b, s)).start(); // b: board, s: status console, filedate: empty 2D array															
			break;
		case "RUN":
			solver = new Thread(new SudokuSolver(b, s)); // b: board, s: status console
			solver.start();
			break;
		case "INTERRUPT":
			solver.interrupt(); // interrupt the solving procedure
			break;
		case "CLEAR":
			b.clear(); // clear the board
			break;
		case "QUIT":
			System.exit(0);
			break;
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Sudoku d = new Sudoku();
		d.setTitle("Sudoku");
		d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		d.setVisible(true);

	}

}
