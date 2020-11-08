/*
 * Sudoku Solver
 * Created by Jacob Curtis
 * 
 * The algorithm is a simple brute force
 * It tries the lowest possible value for each cell until it reaches an unsolvable cell
 * Then it works backwards, removing test values as it goes, until it can increase a test value
 * Then it works fowards until it reaches an unsolvable cell (and works backwards) or reaches the end
 * 
 * The puzzle is entered via a matrix of text fields
 * When the 'Solve' button is pressed, the data is read into a matrix, validated, then solved
 * The solution is displayed in a table and printed to the console
 * 
 * The radio buttons determine how the solution is displayed
 * 'Instant' solves the puzzle then displays the final solution, which is nearly instant
 * 'Watch It Run' displays the test values as they are entered and removed, with a 20 millisecond delay for better readability
 * 
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class SudokuSolver {
	public static void main(String[] args) {
		//Create the window
		JFrame frame = new JFrame("Sudoku Solver");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new SudokuPanel());
		frame.pack();
		frame.setVisible(true);
	}
	
	private static class SudokuPanel extends JPanel{
		private JTable solutionTable;
		private JTextField[] inputFields = new JTextField[81];
		private JPanel inputPanel, splitPanel, leftPanel, rightPanel;
		private JButton solveButton;
		private JRadioButton radioB1, radioB2;
		private JLabel inputLabel, outputLabel, errorLabel_1, errorLabel_2;
		private boolean realtime = false;
		private Integer[][] puzzleMatrix = new Integer[9][9];
		private String[] columnNames = {"","","","","","","","",""};

		public SudokuPanel() {
			//Create input panel, initialize input fields, set input layout
			inputPanel = new JPanel();
			for(int i = 0; i < inputFields.length; i++) {
				inputFields[i] = new JTextField("");
				inputFields[i].setHorizontalAlignment(JTextField.CENTER);
			}
			GridLayout grid = new GridLayout(9,9);
			grid.setHgap(0);
			grid.setVgap(0);
			for(int i = 0; i < inputFields.length; i++) {
				inputPanel.add(inputFields[i]);
			}
			inputPanel.setLayout(grid);
			inputPanel.setPreferredSize(new Dimension(400, 400));
			
			//Create GUI elements
			solveButton = new JButton("Solve!");
			solveButton.addActionListener(new ButtonListener());
			
			radioB1 = new JRadioButton("Instant", true);
			radioB1.setBackground(Color.white);
			radioB1.addActionListener(new RadioListener());
			radioB2 = new JRadioButton("Watch It Run", false);
			radioB2.setBackground(Color.white);
			radioB2.addActionListener(new RadioListener());
			ButtonGroup bg = new ButtonGroup();
			bg.add(radioB1);
			bg.add(radioB2);
			
			inputLabel = new JLabel("Enter puzzle here");
			inputLabel.setFont(new Font("Helvetica", Font.PLAIN, 24));
			outputLabel = new JLabel("Solution:");
			outputLabel.setFont(new Font("Helvetica", Font.PLAIN, 24));
			errorLabel_1 = new JLabel("Please Enter Numbers Between 1-9");
			errorLabel_1.setFont(new Font("Helvetica", Font.PLAIN, 24));
			errorLabel_2 = new JLabel("            Puzzle Is Invalid            ");
			errorLabel_2.setFont(new Font("Helvetica", Font.PLAIN, 24));
			
			splitPanel = new JPanel();
			splitPanel.setBackground(Color.white);
			splitPanel.setPreferredSize(new Dimension(400, 1));
			leftPanel = new JPanel();
			leftPanel.setBackground(Color.white);
			leftPanel.setPreferredSize(new Dimension(400,500));
			rightPanel = new JPanel();
			rightPanel.setBackground(Color.white);
			rightPanel.setPreferredSize(new Dimension(400,500));
			solutionTable = new JTable(); //initially blank
		
			//Add components and define layout
			leftPanel.add(inputLabel);
			leftPanel.add(inputPanel);
			add(leftPanel);
			rightPanel.add(radioB1);
			rightPanel.add(radioB2);
			rightPanel.add(solveButton);
			rightPanel.add(splitPanel);
			rightPanel.add(outputLabel);
			rightPanel.add(solutionTable);
			add(rightPanel);
			setBackground(Color.white);
			setPreferredSize(new Dimension(1000, 500));
		}
		
		//Radio buttons set boolean 'realtime', which determines how the solution is displayed
		private class RadioListener implements ActionListener{
			public void actionPerformed(ActionEvent event) {
				if(radioB1.isSelected()) {
					realtime = false;
				}
				if(radioB2.isSelected()) {
					realtime = true;
				}
			}
		}
		
		//When button is pressed - solve puzzle and display solution
		private class ButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent event) {
				//read input - if incorrect characters entered - display error message
				try{
					puzzleMatrix = readInput();
					rightPanel.remove(errorLabel_1);
					rightPanel.remove(errorLabel_2);
				}catch(NumberFormatException e) {
					rightPanel.add(errorLabel_1);
					revalidate();
					return;
				}
				
				//validate input - if fails, display error message
				if(!validateMatrix(puzzleMatrix)) {
					rightPanel.add(errorLabel_2);
					revalidate();
					return;
				}
				
				//copy to solution matrix
				Integer[][] solutionMatrix = new Integer[puzzleMatrix.length][puzzleMatrix[0].length];
				for(int i = 0; i < puzzleMatrix.length; i++) {
					for(int j = 0; j < puzzleMatrix[0].length; j++) {
						solutionMatrix[i][j] = puzzleMatrix[i][j];
					}
				}
				//Create table, set dimensions, set alignment, and print to screen
				rightPanel.remove(solutionTable);
				solutionTable = new JTable(solutionMatrix, columnNames);
				solutionTable.setRowHeight(40);
				DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
				centerRenderer.setHorizontalAlignment(JLabel.CENTER);
				for(int k = 0; k < 9; k++) {
					solutionTable.getColumnModel().getColumn(k).setPreferredWidth(40);
					solutionTable.getColumnModel().getColumn(k).setCellRenderer(centerRenderer);
				}
				solutionTable.setBorder(BorderFactory.createLineBorder(Color.black));
				rightPanel.add(solutionTable);
				revalidate();
				
				//SOLVE THE PUZZLE
				Thread solveThread = new Thread(){
					public void run() {
						int i = 0; //row
						int j = 0; //column
						int moves = 0;
						boolean forwards = true; //used to determine if program is moving to next cell or backtracking
						while(i < 9) {
							//Original cell is not blank
							if(puzzleMatrix[i][j] != 0) {
								//if moving forward, go to next cell
								if(forwards) {
									if(j == 8) {
										i++;
										j = 0;
									}else {
										j++;
									}
								}
								//if moving backward, go to previous cell
								else {
									if(j == 0) {
										i--;
										j = 8;
									}else {
										j--;
									}
								}
							}
							//Original cell is blank
							else {
								int k = 1;
								for(k = solutionMatrix[i][j]; k <= 9; k++) {
									if(k == 0) {
										//current value is 0, skip to 1 and start checking
										continue;
									}
									//Is k a valid entry?
									if(testCell(solutionMatrix, i, j, k, true)) {
										//set new entry value
										solutionMatrix[i][j] = k;
										forwards = true;	
										moves++;
										//update displayed solution and pause 20 milliseconds
										if(realtime) {
											repaint();
											try {
												Thread.sleep(20);
											}catch(InterruptedException e) {
												System.out.print("fuck");
											}
										}
										break;
									}
								}
								//If loop stopped before reaching end, entry is good, go to next cell
								if(k < 10) {
									if(j == 8) {
										i++;
										j = 0;
									}else {
										j++;
									}
									continue;
								}
								//If loop ran all the way unsuccessfully
								else {
									//Start working backwards
									forwards = false;
									//Clear current cell value
									solutionMatrix[i][j] = 0;
									//go to previous cell
									if(j == 0) {
										i--;
										j = 8;
									}else {
										j--;
									}
								}
							}
						}
						printMatrix(solutionMatrix);
						System.out.println("Solved in "+moves+" moves");
						revalidate();
					}
				};
				solveThread.start();
			}
		}
		
		//Read input from text fields to matrix
		public Integer[][] readInput() {
			Integer[][] matrix = new Integer[9][9];
			for(int i = 0; i < matrix.length; i++) {
				for(int j = 0; j < matrix[0].length; j++) {
					String temp = inputFields[(i*9)+j].getText();
					if(temp.equals("")) {
						matrix[i][j] = 0;
					}
					else {
						matrix[i][j] = Integer.parseInt(temp);
					}
				}
			}
			return matrix;
		}
		
		//Ensure no entry is out of range & no entry errors
		public boolean validateMatrix(Integer[][] matrix) {
			for(int i = 0; i < matrix.length; i++) {
				for(int j = 0; j < matrix[0].length; j++) {
					if(matrix[i][j] < 0 || matrix[i][j] > 9) {
						return false;
					}
					if(matrix[i][j] != 0) {
						if(!testCell(matrix, i, j, matrix[i][j], false)) {
							return false;
						}
					}
				}
			}
			return true;
		}
		
		//Can 'value' be entered at 'matrix[row][column]'?
		public static boolean testCell(Integer[][] matrix, int row, int column, int value, boolean solvingCheck) {
			//boolean 'solvingCheck' is true when solving and false when validating
			//it is used to skip the current cell during validation
			//test row
			for(int i = 0; i < 9; i++) {
				if(i == column && !solvingCheck) {
					continue;
				}
				if(matrix[row][i] == value) {
					return false;
				}
			}
			//test column
			for(int i = 0; i < 9; i++) {
				if(i == row && !solvingCheck) {
					continue;
				}
				if(matrix[i][column] == value) {
					return false;
				}
			}
			//test neighborhood
			int xPos = row/3;
			int yPos = column/3;
			for(int i = xPos*3; i < (xPos*3)+3; i++) {
				for(int j = yPos*3; j < (yPos*3)+3; j++) {
					if(i == row && j == column) {
						continue;
					}
					else if(matrix[i][j] == value) {
						return false;
					}
				}
			}
			//If row, column, and neighborhood passed
			return true;
		}
		
		//Utility for printing matrix to console
		public static void printMatrix(Integer[][] matrix) {
			for(int i = 0; i < matrix.length; i++) {
				for(int j = 0; j < matrix[0].length; j++) {
					System.out.print(matrix[i][j]+" ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
}
