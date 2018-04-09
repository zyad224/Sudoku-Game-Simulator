import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JTextArea;

/**
 * This class implements the status console of the sudoku game.
 * it consists of a JTextArea and a List<string>.
 * The JText area is of fixed size of 7 lines.
 * The List<string> keeps the information written in the JTextArea.
 * If the JTextArea is full(contains 7 lines), the first line is removed and
 * then the JtextArea is updated from the List<string>
 * 
 * @author Zeyad Abdelwahab
 * @version 1.0
 */
public class StatusConsole {

	private JTextArea statusConsole;                              //JtextArea for the console
	private List<String> consoleInfo = new LinkedList<String>(); // list contains the lines written in the console

	/**
	 * Constructor of the status console, it initialize the dimensions 
	 * of the status console with length of 7 and width of screensize/2.
	 * it sets the background of the console to cyan.
	 */
	public StatusConsole() {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		this.statusConsole = new JTextArea(7, (int) width / 2);
		this.statusConsole.setBackground(Color.cyan);
	}

	/**
	 * This method checks if the list<string> is empty or not.
	 * if not empty, it will iterate on the list getting all the information from it.
	 * It will save all the info in a multiline string then update the JText Area with that
	 * string.
	 * The synchronized keyword is used to prevent 2 threads calling this
	 * function in the same time
	 * @return JTextArea filled with the information in the list<string>
	 */
	public synchronized JTextArea getStatusConsole() {
		if (!consoleInfo.isEmpty()) {
			Iterator itr = consoleInfo.iterator();
			String multiLine = "";
			while (itr.hasNext()) {
				multiLine += itr.next() + "\n";
			}
			statusConsole.setText(multiLine);
		}

		return this.statusConsole;
	}

	/**
	 * This method inserts new information in the List<string> consoleInfo.
	 * This method is used by the SudokuFileReader and SudokuSolver.
	 * The synchronized keyword is used to prevent 2 threads calling this
	 * function in the same time.
	 * 
	 * @param info  information to be inserted in the consoleInfo list
	 */
	public synchronized void updateConsole(String info) {

		if (consoleInfo.size() == 7) {
			consoleInfo.remove(0);
		}
		consoleInfo.add(info);
	}

}
