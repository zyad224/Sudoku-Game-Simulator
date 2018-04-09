import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextField;

/**
 * This class implements the solving procedure of the Sudoku Game.
 * This solving procedure uses 5 rules to solve the puzzle.
 * 1- Penciling.
 * 2- Single Square Candidate.
 * 3- Single Candidate Square.
 * 4- Number Claiming.
 * 5- Pairs.
 * 
 * @author Zeyad Abdelwahab
 * @version 1.0
 *
 */
public class SudokuSolver implements Runnable {

	private Board board;                                          //Board of the Sudoku Game(gridlayout 9x9)
	private StatusConsole s;                                     //Status Console of the Game
	private JTextField[][] boardArray = new JTextField[9][9];   // actual JTextField array 9x9
	

	/**
	 * Constructor of the SudokuSolver.
	 * It receives the Board of the game and the Status Console.
	 * It sets the editability of the cells to false in order to start the solving procedure.
	 * @param board Board of the game
	 * @param s Status Console of the game
	 */
	public SudokuSolver(Board board, StatusConsole s) {

		this.board = board;
		this.s = s;
		this.boardArray = board.getboardArray();

		board.setEditability(false);

	}
	
	
    /**
     * The thread of the solving procedure starts here.
     * it passes the boardArray 9x9 to the solver() method that runs the solving procedure.
     */
	@Override
	public void run() {

		solver(boardArray);

	}
	
	
    /**
     * This method calls the penciling(),pairs(), numberClaiming(), singleCandidateSquare methods.
     * It checks for any in consistency in the puzzle and set the background of cells to red/yellow.
     * It decides if the solving procedure is stuck/succeeds.
     * It updates the status console depending on the state of the solver.
     * It uses Thread.sleep() to show the user that work is being done.
     * It caught any Interrupt from the interrupt button.
     * 
     * @param boardArray The puzzle array 
     */
	private void solver(JTextField[][] boardArray) {

		try {
			
			//update the status console
			s.updateConsole("Working");
			s.getStatusConsole();
			
			//do penciling and sleep for 3 seconds
			penciling();
			Thread.sleep(3000);
			
			// get the time of the system now
			long startTime = System.currentTimeMillis();
			
			
			/*
			 * This loop will do SingleCandidateSquare, SingleSquareCandidates,
			 * Pairs, and NumberClaiming rules.
			 * It will exit when the solver solved the puzzle correctly or if an Interrupt is occurred.
			 */
			while ((!solverSucceded()) && (!Thread.currentThread().interrupted())) {
				
				long endTime;        
				long totalTime;
				
				//set the background of single cells to yellow and sleep for 2 seconds
				colorSingleCandidates(0, 9, 0, 9);
				Thread.sleep(2000);
				
				//do SingleCandidateSquare, SingleSquareCandidates, and Pairs then sleep for 2 seconds
				updateCandidates(0, 9, 0, 9);
				singleSquareCandidates();
				pairs();
				Thread.sleep(2000);
				
				//set the background of single cells to yellow
				colorSingleCandidates(0, 9, 0, 9);

				//calculate the total time spent from the start of the loop
				endTime = System.currentTimeMillis();
				totalTime = endTime - startTime;
				System.out.println("time:" + totalTime);
				
				
				/*if the total time is between 44 seconds and 45 seconds, do number claiming
				 *number claiming will be done once in the life of the solver.
				 *When all the other methods fails to solve, the solver will use the number claiming just once.
				 */
				if (totalTime >= 44000 && totalTime <= 45000) {
					
					numberClaiming();

				}

				/*if the total time is between 100seconds and 102 seconds and the solver
				 * didnt solve the puzzle yet, then this means the solver is stuck.
				 * This part updates the status console and check for any in consistency
				 * in the puzzle. if found, it updates the status console.
				 */
				if (totalTime >= 100000 && totalTime <= 102000) {
					s.updateConsole("Solver Stuck");
					s.getStatusConsole();
					checkForInconsistency();
					if (board.inConsistency()) {
						s.updateConsole("InConsisteny Found");
						s.getStatusConsole();
					}
					
				}
			}

			//if solver exits the loop, then this means the puzzle is solved and updates the console.
			s.updateConsole("Solver Succeeds");
			s.getStatusConsole();
			

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block

			/*
			 * update the console when and interrupt button is clicked
			 */
			s.updateConsole("Solver Interrupted");
			s.getStatusConsole();
			board.setEditability(true);
			e.printStackTrace();
		}

	}
	
	
	
	
	/**
	 * This method creates the candidates list for each cell in the puzzle.
	 * It loops on each box of the 9 boxes. it deletes from a list that contains numbers(1 to 9) the numbers found in
	 * in that box. After that it loops on each empty cell in that box, then searches for single numbers in that row and
	 * column. if any number in the row or column found on the list, it deletes it from the list too.
	 * The remaining numbers in that list is the candidates that is inserted in that cell.
	 * 
	 * It does the same for each empty cell.
	 * 
	 */
	private void penciling() {

		//list that contains the indexes ot the 9 boxes
		List<Integer> list1 = Arrays.asList(0, 2, 0, 2, 0, 2, 3, 5, 0, 2, 6, 8, 3, 5, 0, 2, 3, 5, 3, 5, 3, 5, 6, 8, 6,
				8, 0, 2, 6, 8, 3, 5, 6, 8, 6, 8);
		Iterator iter = list1.iterator();
		
		while (iter.hasNext()) {
			ArrayList<String> numbers = new ArrayList<String>();    // list of numbers between 1-9 (all possible values)
			numbers.add("1");
			numbers.add("2");
			numbers.add("3");
			numbers.add("4");
			numbers.add("5");
			numbers.add("6");
			numbers.add("7");
			numbers.add("8");
			numbers.add("9");
			
			// get the 4 indexes of each box
			int xi = (Integer) (iter.next());
			int xf = (Integer) (iter.next());
			int yi = (Integer) (iter.next());
			int yf = (Integer) (iter.next());
			
			//loop on each box, any numbers in the box found in the numbers list, delete it from the list
			for (int i = xi; i <= xf; i++)
				for (int j = yi; j <= yf; j++)
					if (!(boardArray[i][j].getText().equals(""))) {
						String temp = boardArray[i][j].getText();
						if (numbers.contains(temp))
							numbers.remove(temp);
					}

			//loop on each empty cell in that box and creates the candidate list for each cell
			for (int i = xi; i <= xf; i++)
				for (int j = yi; j <= yf; j++)
					if ((boardArray[i][j].getText().equals(""))) {
						StringBuilder candidate = new StringBuilder();
						ArrayList<String> numbersCopy = getCopyOfNumbers(numbers);
						
						//if any number found on that cell row, delete it from the numbers list
						for (int k = 0; k < 9; k++) {
							if (boardArray[i][k].getText().length() == 1)
								if (numbersCopy.contains(boardArray[i][k].getText()))
									numbersCopy.remove(boardArray[i][k].getText());
						}

						//if any number found on that cell column, delete it from the numbers list
						for (int k = 0; k < 9; k++) {
							if (boardArray[k][j].getText().length() == 1)
								if (numbersCopy.contains(boardArray[k][j].getText()))
									numbersCopy.remove(boardArray[k][j].getText());
						}

						//iterate on the numbers list and create the candidate for that cell
						Iterator itr = numbersCopy.iterator();
						while (itr.hasNext()) {
							candidate.append(itr.next());
						}

						// insert the candidate in that cell
						boardArray[i][j].setText(candidate.toString());
					}

		}
	}

	
	
	/**
	 * This method iterates through all the puzzle searching for single numbers.
	 * if a single number found, this method calls updateCandidatesInColumns(), updateCandidatesInRow() to
	 * remove that single number from all the candidate lists in that number column and row.
	 * @param xi   0
	 * @param xf   9
	 * @param yi   0
	 * @param yf   9
	 */
	private void updateCandidates(int xi, int xf, int yi, int yf) {
		
		//search for single numbers
		for (int i = xi; i < xf; i++)
			for (int j = yi; j < yf; j++) {
				String temp = boardArray[i][j].getText();
				if ((temp.length() == 1)) {         //if found
					int colRight = j;
					int colLeft = j;
					int rowDown = i;
					int rowUp = i;
					
					// send the row of the number and iterates right and left looking for similar number in that row
					updateCandidatesInColumns(colRight, colLeft, i, temp);
					//send the column of the number and iterates up and down looking for similar numbers in that column
					updateCandidatesInRows(rowDown, rowUp, j, temp);

				}

			}
	}

	
	/**
	 * This method receives a single number in the puzzle then iterates through the column that 
	 * this number resides in, if that number appears in any candidate list in the column, this method
	 * removes it from that candidate list of that cell and update it.
	 * 
	 * @param rowDown   value used to iterate down the column
	 * @param rowUp     value used to iterate up the column
	 * @param positionOfElement the column that the number resides in the puzzle
	 * @param element the number
	 */
	private void updateCandidatesInRows(int rowDown, int rowUp, int positionOfElement, String element) {

		/*
		 * iterate down the column, if the number found
		 * in any candidate list, delete it from the list and update the cell in the puzzle
		 */
		while (rowDown < 9) {
			rowDown++;
			if (rowDown < 9) {
				StringBuilder candidateDown = new StringBuilder(boardArray[rowDown][positionOfElement].getText());
				for (int k = 0; k < candidateDown.length(); k++)
					if (candidateDown.charAt(k) == element.charAt(0)) {
						candidateDown.deleteCharAt(k);

					}
				boardArray[rowDown][positionOfElement].setText(candidateDown.toString());

			}
		}

		/*
		 * iterate up the column, if the number found
		 * in any candidate list, delete it from the list and update the cell in the puzzle
		 */
		while (rowUp >= 0) {
			rowUp--;
			if (rowUp >= 0) {
				StringBuilder candidateUp = new StringBuilder(boardArray[rowUp][positionOfElement].getText());
				for (int k = 0; k < candidateUp.length(); k++)
					if (candidateUp.charAt(k) == element.charAt(0)) {
						candidateUp.deleteCharAt(k);

					}

				boardArray[rowUp][positionOfElement].setText(candidateUp.toString());

			}
		}
	}

	
	/**
	 * This method receives a single number in the puzzle then iterates through the row that 
	 * this number resides in, if that number appears in any candidate list in the row, this method
	 * removes it from that candidate list of that cell and update it.
	 * 
	 * @param colRight  value used to iterate right in the row
	 * @param colLeft   value used to iterate left  in the row
	 * @param positionOfElement the row that the number resides in
	 * @param element  the number
	 */
	private void updateCandidatesInColumns(int colRight, int colLeft, int positionOfElement, String element) {
		
		/*
		 * iterate right the row, if the number found
		 * in any candidate list, delete it from the list and update the cell in the puzzle
		 */
		while (colRight < 9) {
			colRight++;
			if (colRight < 9) {
				StringBuilder candidateRight = new StringBuilder(boardArray[positionOfElement][colRight].getText());
				for (int k = 0; k < candidateRight.length(); k++)
					if (candidateRight.charAt(k) == element.charAt(0)) {
						candidateRight.deleteCharAt(k);

					}
				boardArray[positionOfElement][colRight].setText(candidateRight.toString());

			}
		}
		
		
		/*
		 * iterate left the row, if the number found
		 * in any candidate list, delete it from the list and update the cell in the puzzle
		 */
		while (colLeft >= 0) {
			colLeft--;
			if (colLeft >= 0) {
				StringBuilder candidateLeft = new StringBuilder(boardArray[positionOfElement][colLeft].getText());
				for (int k = 0; k < candidateLeft.length(); k++)
					if (candidateLeft.charAt(k) == element.charAt(0)) {
						candidateLeft.deleteCharAt(k);

					}

				boardArray[positionOfElement][colLeft].setText(candidateLeft.toString());

			}
		}

	}

	
	
	/**
	 * This method iterates through all the puzzle searching for 2-digit numbers.
	 * if a 2-digit number found, this method calls pairsRows() & pairsCols() to search the column and the row
	 * of that number searching for a similar 2-digit number. if found the method loops on the row or the column
	 * deleting similar numbers from candidate list in each cell except of the 2 similar pairs.
	 */
	private void pairs() {
		
		//searching for any 2-digit number
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				if(boardArray[i][j].getText().length()==2) {  //if found
					int colRight=j;
					int colLeft=j;
					int rowDown=i;
					int rowUp=i;
					String temp=boardArray[i][j].getText();
					
					//send the column of that number and iterate up and down looking for the similar pair in that column
					pairsRows(rowDown,rowUp,j,temp);
					
					//send the row of that number and iterate right and left searching for similar pair in that row
					pairsCols(colRight,colLeft,i,temp);
					
				}
			}
		}
	}

	
	
	/**
	 * This method receives a 2-digit number in the puzzle then iterates through the row that
	 * this number resides. if a pair found in that row, this function iterates through that row,
	 * removing similar numbers from candidate lists in each list and leaves the 2 pairs unchanged.
	 * 
	 * @param colRight  value used to iterate right in the row
	 * @param colLeft   value used to iterate left in the row
	 * @param positionOfElement the row that the 2-digit number resides in
	 * @param element the 2-digit number
	 */
	private void pairsCols(int colRight,int colLeft, int positionOfElement, String element) {
		ArrayList<String>candidates;
		
		//searching for the pair right the row
		while(colRight<9 ) {
			candidates=new ArrayList<String>();
			colRight++;
			if(colRight<9 ) {
			StringBuilder candidateUp=new StringBuilder(boardArray[positionOfElement][colRight].getText());
			
            // if pair found
			if(candidateUp.equals(element)) {
				
				// add the 2 digits to a candidate list
				for(int k=0;k<candidateUp.length();k++) {
					candidates.add(Character.toString(candidateUp.charAt(k)));
				}
				
				// iterate through the row, if any cell has any of the 2 digits, delete it and update the cell
				for(int i=0;i<9;i++) {
					StringBuilder temp2=new StringBuilder(boardArray[positionOfElement][i].getText());
					if((!temp2.equals(element))&&(!temp2.equals(candidateUp))) {
						for(int j=0;j<temp2.length();j++) {
						    if(candidates.contains(String.valueOf(temp2.charAt(j))))
				          		temp2.deleteCharAt(j);
					  }
					}
					// update the cell
					boardArray[positionOfElement][i].setText(temp2.toString());
					
				}
				
			}

			
			}		
		}
		
		//searching for the pair left the row
		while(colLeft>=0 ) {
			candidates=new ArrayList<String>();
			colLeft--;
			if(colLeft>=0 ) {
			StringBuilder candidateUp=new StringBuilder(boardArray[positionOfElement][colLeft].getText());
			
            // if pair found
			if(candidateUp.equals(element)) {
				// add the 2 digits to a candidate list
				for(int k=0;k<candidateUp.length();k++) {
					candidates.add(Character.toString(candidateUp.charAt(k)));
				}
				
				//iterate through the row, if any cell has any of the 2 digits, delete it and update the cell
				for(int i=0;i<9;i++) {
					StringBuilder temp2=new StringBuilder(boardArray[positionOfElement][i].getText());
					if((!temp2.equals(element))&&(!temp2.equals(candidateUp))) {
						for(int j=0;j<temp2.length();j++) {
						    if(candidates.contains(String.valueOf(temp2.charAt(j))))
				          		temp2.deleteCharAt(j);
					  }
					}
					
					//update the cell
					boardArray[positionOfElement][i].setText(temp2.toString());
					
				}
				
			}

			
			}		
		}
	}
	
	
	/**
	 * 
	 * This method receives a 2-digit number in the puzzle then iterates through the column that
	 * this number resides. if a pair found in that column, this function iterates through that column,
	 * removing similar numbers from candidate lists in each list and leaves the 2 pairs unchanged.
	 * 
	 * @param rowDown   value used to iterate down in the column
	 * @param rowUp     value used to iterate up in the column
	 * @param positionOfElement the column that the 2-digit number resides in
	 * @param element  the 2-digit number
	 */
	private void pairsRows(int rowDown,int rowUp,int positionOfElement,String element) {
		
		ArrayList<String>candidates;

		//searching for the pair down the column
		while(rowDown<9 ) {
			candidates=new ArrayList<String>();
			rowDown++;
			if(rowDown<9 ) {
			StringBuilder candidateDown=new StringBuilder(boardArray[rowDown][positionOfElement].getText());
			
			// if pair found
			if(candidateDown.equals(element)) {
				
				// add the 2 digits to a candidate list
				for(int k=0;k<candidateDown.length();k++) {
					candidates.add(Character.toString(candidateDown.charAt(k)));
				}
				
				// iterate through the column, if any cell has any of the 2 digits, delete it and update the cell
				for(int i=0;i<9;i++) {
					StringBuilder temp2=new StringBuilder(boardArray[i][positionOfElement].getText());
					if((!temp2.equals(element))&&(!temp2.equals(candidateDown))) {
						for(int j=0;j<temp2.length();j++) {
						    if(candidates.contains(String.valueOf(temp2.charAt(j))))
				          		temp2.deleteCharAt(j);
					  }
					}
					
					// update the cell
					boardArray[i][positionOfElement].setText(temp2.toString());
					
				}
				
			}

			
			}		
		}
		
		//searching for the pair up the column
		while(rowUp>=0 ) {
			candidates=new ArrayList<String>();
			rowUp--;
			if(rowUp>=0 ) {
			StringBuilder candidateUp=new StringBuilder(boardArray[rowUp][positionOfElement].getText());
			
            // if pair found
			if(candidateUp.equals(element)) {
				
				// add the 2 digits to a candidate list
				for(int k=0;k<candidateUp.length();k++) {
					candidates.add(Character.toString(candidateUp.charAt(k)));
				}
				
				// iterate through the column, if any cell has any of the 2 digits, delete it and update the cell
				for(int i=0;i<9;i++) {
					StringBuilder temp2=new StringBuilder(boardArray[i][positionOfElement].getText());
					if((!temp2.equals(element))&&(!temp2.equals(candidateUp))) {
						for(int j=0;j<temp2.length();j++) {
						    if(candidates.contains(String.valueOf(temp2.charAt(j))))
				          		temp2.deleteCharAt(j);
					  }
					}
					
					//update the cell
					boardArray[i][positionOfElement].setText(temp2.toString());
					
				}
				
			}

			
			}		
		}
	
	}

	
	/**
	 * This method implements the single square candidate rule.
	 * It iterates on the all 9 boxes, putting each number in each candidate list in a list called candidates.
	 * 
	 * It iterates on the box again, getting each candidate list in each cell, then iterate on each number on that
	 * candidate list and calculates how many times the number appeared on the list called candidates. if the number 
	 * appeared just once, then this means its the only number appeared once in that box and that cell will be updated by that number.
	 */
	private void singleSquareCandidates() {
		ArrayList<String> candidates;      // list that full of single candidate numbers
		
		// list that contains the indexs of the 9 boxes
		List<Integer> list1 = Arrays.asList(0, 2, 0, 2, 0, 2, 3, 5, 0, 2, 6, 8, 3, 5, 0, 2, 3, 5, 3, 5, 3, 5, 6, 8, 6,
				8, 0, 2, 6, 8, 3, 5, 6, 8, 6, 8);
		Iterator itr = list1.iterator();

		//loop on each box
		while (itr.hasNext()) {
			int xi = (Integer) (itr.next());
			int xf = (Integer) (itr.next());
			int yi = (Integer) (itr.next());
			int yf = (Integer) (itr.next());
			candidates = new ArrayList<String>();
			
			//loop on each candidate list on the box and put its numbers in the list called candidates
			for (int i = xi; i <= xf; i++) {
				for (int j = yi; j <= yf; j++) {
					StringBuilder temp = new StringBuilder(boardArray[i][j].getText());
					for (int k = 0; k < temp.length(); k++)
						candidates.add(Character.toString(temp.charAt(k)));
				}
			}
			
			//loop on each character on each candidate list and calculates the occurrence of that number 
			// in the list called candidates. if occurrence is one then this means the number appeared once in the area
			// update the cell with that number
			for (int i = xi; i <= xf; i++) {
				for (int j = yi; j <= yf; j++) {
					StringBuilder temp = new StringBuilder(boardArray[i][j].getText());
					if (temp.length() > 1) {
						for (int k = 0; k < temp.length(); k++) {
							int occurrences = Collections.frequency(candidates, String.valueOf((temp.charAt(k))));
							if (occurrences == 1)
								boardArray[i][j].setText(String.valueOf(temp.charAt(k)));
						}
					}
				}
			}
		}

	}
	
	
	

	/**
	 * This method is used to copy the content of an array list in another array list
	 * @param numbers list to copy
	 * @return ArrayList<String> numbersCopy 
	 */
	private ArrayList<String> getCopyOfNumbers(ArrayList<String> numbers) {
		ArrayList<String> numbersCopy = new ArrayList<String>();
		Iterator itr = numbers.iterator();
		while (itr.hasNext()) {
			numbersCopy.add((String) itr.next());
		}

		return numbersCopy;
	}

	
	/**
	 * This method iterates on the puzzle searching for cells with single number.
	 * if found one, it sets the background color of this cell to yellow.
	 * 
	 * @param xi   0
	 * @param xf   9
	 * @param yi   0
	 * @param yf   9
	 */
	private void colorSingleCandidates(int xi, int xf, int yi, int yf) {
		for (int i = xi; i < xf; i++)
			for (int j = yi; j < yf; j++) {
				String temp = boardArray[i][j].getText();
				if ((temp.length() == 1))
					board.setCellBackground(Color.YELLOW, i, j);

			}

	}
	
	
	
	
	
	/**
	 * This method checks for inconsistency in the puzzle.
	 * its only called when the solving procedure is stuck.
	 * for each cell, it calls checkInConsistencyinRow(), checkInConsistencyinCol
	 * to check for similar cell in the row and col of the cell.
	 * if a similar cell found it set the background of it to red.
	 */
	private void checkForInconsistency() {
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				
				String element=boardArray[i][j].getText();
				int colRight=j;
				int colLeft=j;
				int rowDown=i;
				int rowUp=i;
				
				// check for in consistency  in column of the cell
				checkInConsistencyInRow(rowDown,rowUp,i,j,element);
				
				// check for in consistency in row of the cell
				checkInConsistencyInCol(colRight,colLeft,i,j,element);
				
			}
		}
	}
	
	
	
	/**
	 * This method checks for inconsistency in a row.
	 * 
	 * @param colRight   value used to iterate right the row
	 * @param colLeft    value used to iterate left the row
	 * @param ElementRow element row position
	 * @param ElementCol element col position
	 * @param element    element checking for inconsistency
	 */
	private void checkInConsistencyInCol(int colRight,int colLeft,int ElementRow,int ElementCol,String element) {
		
		// iterate right the row 
		while(colRight<9 ) {
			colRight++;
			if(colRight<9 ) {
			StringBuilder candidateRight=new StringBuilder(boardArray[ElementRow][colRight].getText());
			
			
			    // if inconsistency found
				if(candidateRight.toString().equals(element)) {
				
                    // set the background of the 2 inconsistent elements to red
					board.setCellBackground(Color.RED, ElementRow, ElementCol);
					board.setCellBackground(Color.RED, ElementRow, colRight);	
				}
			}		
		}
		
		//iterate left the row
		while(colLeft>=0 ) {
			colLeft--;
			if(colLeft>=0 ) {
			StringBuilder candidateLeft=new StringBuilder(boardArray[ElementRow][colLeft].getText());
			
			   // if inconsistency found
			   if(candidateLeft.toString().equals(element)) {
				   
				// set the background of the 2 inconsistent elements to red   
			    board.setCellBackground(Color.RED, ElementRow, ElementCol);
				board.setCellBackground(Color.RED, ElementRow, colLeft);
				
			}
						
			}		
		}
	}

	
	/**
	 * This method checks for inconsistency in column
	 * 
	 * @param rowDown    value used to iterate down the column
	 * @param rowUp      value used to iterate up the column
	 * @param ElementRow element row position
	 * @param ElementCol element col position
	 * @param element    element checking for inconsistency
	 */
	private void checkInConsistencyInRow(int rowDown,int rowUp,int ElementRow,int ElementCol,String element) {
		
		//iterate down the column
		while(rowDown<9 ) {
			rowDown++;
			if(rowDown<9 ) {
			StringBuilder candidateDown=new StringBuilder(boardArray[rowDown][ElementCol].getText());
			  
			    // if inconsistency found
				if(candidateDown.toString().equals(element)) {
					//set the background of the 2 inconsistent elements to red
					board.setCellBackground(Color.RED, ElementRow, ElementCol);
					board.setCellBackground(Color.RED, rowDown, ElementCol);	
				}
			}		
		}
		
		//iterate up the column
		while(rowUp>=0 ) {
			rowUp--;
			if(rowUp>=0 ) {
			StringBuilder candidateUp=new StringBuilder(boardArray[rowUp][ElementCol].getText());
			   // if inconsistency found
			   if(candidateUp.toString().equals(element)) {
				//set the background of the 2 inconsistent elements to red   
			    board.setCellBackground(Color.RED, ElementRow, ElementCol);
				board.setCellBackground(Color.RED, rowUp, ElementCol);
				
			}
						
			}		
		}
	}
	
	
	
	/**
	 * This method implements the number claiming rule.
	 * It searches if a number in a candidate list only appeared in one row of the box.
	 * If yes, it eliminates all the similar numbers in that row starting from outside the box.
	 * It uses two methods numberClaimingRightRow() and numberClaimingLeftROw()
	 */
	private void numberClaiming() {
		ArrayList<String> candidates;
		List<Integer> list1 = Arrays.asList(0, 2, 0, 2, 0, 2, 3, 5, 0, 2, 6, 8, 3, 5, 0, 2, 3, 5, 3, 5, 3, 5, 6, 8, 6,
				8, 0, 2, 6, 8, 3, 5, 6, 8, 6, 8);
		Iterator itr = list1.iterator();

		while (itr.hasNext()) {
			int xi = (Integer) (itr.next());
			int xf = (Integer) (itr.next());
			int yi = (Integer) (itr.next());
			int yf = (Integer) (itr.next());
			boolean found = false;
			for (int i = xi; i <= xf; i++) {
				for (int j = yi; j <= yf; j++) {
					if (boardArray[i][j].getText().length() > 1) {
						String temp = boardArray[i][j].getText();
						for (int k = 0; k < temp.length(); k++) {
							String tempChar = String.valueOf(temp.charAt(k));
							int tempRow = i;
							while (tempRow < xf + 1) {
								tempRow++;
								if (tempRow < xf + 1) {
									for (int z = xi; z <= xf; z++) {
										if (boardArray[tempRow][z].getText().length() > 1) {
											String temp2 = boardArray[tempRow][z].getText();
											for (int f = 0; f < temp2.length(); f++) {
												String temp2Char = String.valueOf(temp2.charAt(f));
												if (temp2Char.equals(tempChar)) {
													found = true;
												}
											}
										}
									}
								}
							}
							tempRow = i;
							while (tempRow > xi - 1) {
								tempRow--;
								if (tempRow > xi - 1) {
									for (int z = xi; z <= xf; z++) {
										if (boardArray[tempRow][z].getText().length() > 1) {
											String temp2 = boardArray[tempRow][z].getText();
											for (int f = 0; f < temp2.length(); f++) {
												String temp2Char = String.valueOf(temp2.charAt(f));
												if (temp2Char.equals(tempChar)) {
													found = true;
												}
											}
										}
									}
								}
							}

							if (!found) {
								int colRight = yf;
								int colLeft = yi;
								int positionOfElement = i;
								numberClaimingRightRow(colRight, positionOfElement, tempChar);
								numberClaimingRightRow(colLeft, positionOfElement, tempChar);

							}
						}
					}
				}
			}
		}

	}
	
	/**
	 * This method is used by numberClaiming() method to eliminate all the similar numbers
	 * that resides on the right of the element and outside the box
	 * @param colRight            value to iterate right of the row
	 * @param positionOfElement   the row of the number
	 * @param element             the number
	 */
	private void numberClaimingRightRow(int colRight,int positionOfElement,String element) {
		while(colRight<9 ) {
			colRight++;
			if(colRight<9 ) {
			StringBuilder candidateRight=new StringBuilder(boardArray[positionOfElement][colRight].getText());
			for(int k=0;k<candidateRight.length();k++)
				if(candidateRight.charAt(k)==element.charAt(0)) {
					candidateRight.deleteCharAt(k);

					
				}
			boardArray[positionOfElement][colRight].setText(candidateRight.toString());

			
			}		
		}
	}
	
	/**
	 * This method is used by numberClaiming() method to eliminate all the similar numbers
	 * that resides on the left of the element and outside the box
	 * @param colLeft            value to iterate left of the row
	 * @param positionOfElement  the column of the number
	 * @param element            the number
	 */
	private void numberClaimingLeftRow(int colLeft,int positionOfElement,String element) {
		while(colLeft>=0 ) {
			colLeft--;
			if(colLeft>=0 ) {
			StringBuilder candidateLeft=new StringBuilder(boardArray[positionOfElement][colLeft].getText());
			for(int k=0;k<candidateLeft.length();k++)
			   if(candidateLeft.charAt(k)==element.charAt(0)) {
				candidateLeft.deleteCharAt(k);
				
			}
			
			boardArray[positionOfElement][colLeft].setText(candidateLeft.toString());
			
			}		
		}
	}
	
	
	/**
	 * This method is used to check if the solving procedure has solved the puzzle or not yet
	 * @return true if succeeds, false otherwise
	 */
	private boolean solverSucceded() {
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				if ((boardArray[i][j].getText().length() != 1) && (boardArray[i][j].getBackground() != Color.YELLOW))
					return false;
		return true;
	}

	
}
