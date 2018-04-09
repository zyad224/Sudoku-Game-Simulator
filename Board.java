import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This class implements the Board of the sudoku game.
 * it consists of a grid layout of 9x9 JPanel and a JTextField array of 9x9.
 * After initializing the JTextField array, it adds it on the JPanel grid layout
 * @author Zeyad Abdelwahab
 * @version 1.0
 */

public class Board extends JPanel implements ActionListener{
	
	private JPanel Board;
	private JTextField[][]board;
	
	
	/**
	 * Constructor of the Board of the sudoku game.
	 * it creates a JPanel grid layout of 9x9 called Board.
	 * it creates a JTextFiled array [9][9] called board.
	 * it iterates on the board and creates a JTextField on each array cell, it also sets each
	 * cell to be Editable and sets the JTextField in the center of the cell.
	 * 
	 * After that it adds the JTextField array [9][9] to the JPanel Board
	 */
	public Board() {
		Board = new JPanel(new GridLayout(9, 9)); // JPanel gridlayout of 9x9
		Board.setBackground(Color.BLACK);
		board = new JTextField[9][9]; // JTextField array of 9x9

		/*
		 * iterates on the JTextField array[9][9] to initialize each cell in the array
		 * to a new JTextField, sets each cell to be editable, and sets the alignment of
		 * each JTextField to be in the center.
		 * 
		 * At the end, it adds each cell to the gridlayout JPanel Board.
		 */
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {

				board[i][j] = new JTextField();
				board[i][j].setEditable(true);
				board[i][j].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
				Font font = new Font("Arial", Font.PLAIN, 20);

				board[i][j].setFont(font);

				board[i][j].setHorizontalAlignment(JTextField.CENTER);

				Board.add(board[i][j]);

			}
		}

	}

	/**
	 * This method returns the JPanel gridlayout 9x9 Board.
	 * Synchronized keyword is used to prevent 2 threads to use this function in the same time
	 * @return JPanel Board gridlayout 9x9 JPanel
	 */
	public synchronized JPanel getPlayingBoardPanel() {
		return this.Board;
	}
	
	/**
	 * This method returns the JTextField array 9x9 board.
	 * Synchronized keyword is used to prevent 2 threads to use this function in the same time
	 * @return JTextField array 9x9 board
	 */
	public synchronized JTextField[][] getboardArray() {
		return this.board;
	}
	
	/**
	 * This method sets the color of the cell in the grid layout.
	 * Synchronized keyword is used to prevent 2 threads to use this function in the same time
	 * @param c  color to be set
	 * @param x  row of the cell
	 * @param y  col of the cell
	 */
	public  synchronized void  setCellBackground(Color c,int x,int y) {
		
		board[x][y].setBackground(c);
		
	}
	

	/**
	 * This method clears the grid layout from all the number.
	 * it also sets the background of the cells to white.
	 * Synchronized keyword is used to prevent 2 threads to use this function in the same time
	 */
	public synchronized void clear() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				board[i][j].setText("");
				board[i][j].setBackground(Color.WHITE);
			}
		}
		
	}

	/**
	 * This method return true if any red cell found in the board gridlayout.
	 * if not found any red cell, it returns false.
	 * Synchronized keyword is used to prevent 2 threads to use this function in the same time
	 * @return true: if red cell found, false: if cells are not red
	 */
	public synchronized boolean inConsistency() {
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				if(board[i][j].getBackground()==Color.RED)
					return true;
		return false;
	}
	
	/**
	 * This method set the editability of the board cells.
	 * Synchronized keyword is used to prevent 2 threads to use this function in the same time
	 * @param editable true to set cells as editable, false to set the cells as uneditable.
	 */
	public synchronized void setEditability(boolean editable) {
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				board[i][j].setEditable(editable);
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		//Graphics2D g2 = (Graphics2D) g;
		// g2.setStroke(new BasicStroke(10));
		//g2.drawLine(0, 0, 20, 20);
		
	
	}

}
