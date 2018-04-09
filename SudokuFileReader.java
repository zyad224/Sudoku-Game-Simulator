import java.awt.Color;
import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;
import java.util.Iterator;

/**
 * This class implements the file reader for the Sudoku Game.
 * 
 * it receives 2D arraylist<string> filedata from the sudoku.java to fill it
 * with numbers from the file provided.
 * 
 * it also receives the Board board and StatusConsole s from the sudoku.java.
 * After reading the file path from the user using the JFileChoose, it parses
 * the file and updates the Board by numbers and updates the status console.
 * 
 * if the file is corrupted or having a bad format, it asks the user to input a
 * new file using the showMessageDialouge method.
 * 
 * @author Zeyad Abdelwahab
 * @version 1.0
 *
 */
public class SudokuFileReader implements Runnable {

	private JFileChooser jfc; // fileChooser obj
	private File selectedFile; // the file selected by the user
	private ArrayList<ArrayList<String>> fileData; // 2D array of the numbers filled from the file
	private Board board; // Board of the game (grid layout 9x9)
	private JTextField[][] boardArray = new JTextField[9][9]; // actual JTextField array 9x9
	private StatusConsole s; // status console
	boolean result; // result after reading and parsing the file

	/**
	 * Constructor of the Sudoku File Reader. it receives an empty 2D array
	 * list<string>, Board of the game, and the StatusConsole. it sets the
	 * editabillity of the board to true in order for the user to change whatever
	 * cell he wants.
	 * 
	 * @param fileData  2D arrayList<String> to be filled with numbers from the file
	 *           
	 * @param board Board of the sudoku game (gridlayout 9x9)
	 *            
	 * @param s Status Console of the game
	 *            
	 */
	public SudokuFileReader(ArrayList<ArrayList<String>> fileData, Board board, StatusConsole s) {
		this.fileData = fileData;
		this.board = board;
		this.boardArray = board.getboardArray();
		this.s = s;
		board.setEditability(true);
	}

	/**
	 * The thread of the sudoku file reader starts here. This method receives the
	 * file from the user using the JFileChoose then pass the file (selectedFile) to
	 * the readFile() method which parse the file and returns true/false.
	 * 
	 * if the readFile() returns true, then the file has been succefully read. if
	 * the readFile() returns false, then the file is corrupted and with bad format.
	 * 
	 * The run method check on the result of the readFile(), if true it updates the
	 * Board and the status console. If the result is false, it shows the user a
	 * notification telling the user to add a new file
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub

		jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = jfc.getSelectedFile(); // file selected by the user
			result = readFile(selectedFile); // result of the readFile method
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				/*
				 * if result=true, update the console with Ready and Loading(name of the file).
				 * Iterate on the 2D arrayList filedata and remove character with '_' from each
				 * cell and set the background color of the cells with single number.
				 */
				if (result) {
					s.updateConsole("Ready"); // insert ready in the console
					s.updateConsole("Loading:" + selectedFile.toString()); // insert name of file in console
					s.getStatusConsole(); // show info in the console

					// iterate on the fileData to remove '_' from the cells and update the
					// boardArray
					Iterator itr = fileData.iterator();
					int k = 0;
					for (int i = 0; i < fileData.size(); i++) {
						for (int j = 0; j < fileData.get(i).size(); j++) {
							StringBuilder temp = new StringBuilder(fileData.get(i).get(j));

							for (int z = 0; z < 9; z++) {

								String firstChar = Character.toString(temp.charAt(0));
								if (!firstChar.equals("_"))
									boardArray[k][z].setText(firstChar);

								temp.delete(0, 1);
							}

							if (k < 9)
								k++;

						}
					}

					// iterate on the boardArray to set the background color of cells with single
					// number
					for (int i = 0; i < 9; i++)
						for (int j = 0; j < 9; j++)
							if (!(boardArray[i][j].getText().equals("")))
								board.setCellBackground(Color.YELLOW, i, j);

				} else {
					// if file is corrupted, let the user upload new file
					JOptionPane.showMessageDialog(null, "Please Choose another File, Wrong Sudoku File");
				}

			}
		});

	}

	/**
	 * This method reads and parses the selected file by the user. it ignores extra
	 * lines if the file contains >9 lines. it ignores extra characters if line
	 * contains >9. any character other than a decimal is treated as blank cell.
	 * 
	 * if any line has <9 chars, this method returns false (corrupted file). if the
	 * file contains fewer than 9 lines, this method return false (corrupted file).
	 * 
	 * @param selectedFile file to be parsed
	 *            
	 * @return true: if reading is finished, false: corrupted file found
	 */
	private boolean readFile(File selectedFile) {

		BufferedReader br = null;

		try {

			br = new BufferedReader(new FileReader(selectedFile));
			String line = ""; // line read from file
			StringBuilder updatedLine; // line after modification (removing extra chars ..etc)
			int lineCounter = 0; // counter to count number of lines in file
			boolean[] decimalOrNot; // array with size 9 like the line to check if a char is decimal or not

			// Iterates on the file and read lines one by one
			while (((line = br.readLine()) != null) && (lineCounter < 9)) {

				ArrayList<String> temp = new ArrayList<String>();
				updatedLine = new StringBuilder(line); // line to be modified
				decimalOrNot = new boolean[9]; // boolean array to check if a char is decimal or not

				// if a line has <9 chars, return false
				if (updatedLine.length() < 9)
					return false;

				// if a line has >9 chars, delete the extra chars
				if (updatedLine.length() > 9) {
					updatedLine.delete(9, updatedLine.length());
					updatedLine.replace(0, 9, line.substring(0, 9));
				}

				/*
				 * checks if each char is decimal or not in the line. if its not decimal update
				 * the decimalOrNot array to true in the char specific position.
				 */
				for (int i = 0; i < updatedLine.length(); i++) {
					if (updatedLine.charAt(i) != '_') {
						try {
							int j = Integer.parseInt(updatedLine.substring(i, i + 1));

						} catch (NumberFormatException e) {
							decimalOrNot[i] = true;
						}
					}
				}

				/*
				 * iterate on the decimalOrNot and find un-decimal chars. After that update its
				 * position in the line.
				 */
				for (int i = 0; i < decimalOrNot.length; i++) {
					if (decimalOrNot[i] == true) {
						updatedLine.setCharAt(i, '_');
					}

				}

				// add the line to the array and increase the line counter
				temp.add(updatedLine.toString());
				fileData.add(temp);
				lineCounter++;

			}

			// if line counter less than 9, return false (corrupted file)
			if (lineCounter < 8)
				return false;

		} catch (FileNotFoundException e) {
			System.out.println(e);

		} catch (IOException e) {
			System.out.println(e);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// return true after reading the file
		return true;

	}

}
