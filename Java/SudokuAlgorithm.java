public class SudokuAlgorithm {
	public static void main(String[] args) {
		int[][] puzzleMatrix = {
				{5,3,0,0,7,0,0,0,0},
			    {6,0,0,1,9,5,0,0,0},
			    {0,9,8,0,0,0,0,6,0},
			    {8,0,0,0,6,0,0,0,3},
			    {4,0,0,8,0,3,0,0,1},
			    {7,0,0,0,2,0,0,0,6},
			    {0,6,0,0,0,0,2,8,0},
			    {0,0,0,4,1,9,0,0,5},
			    {0,0,0,0,8,0,0,7,9}
		};
		
		//copy to solution matrix
		Integer[][] solutionMatrix = new Integer[puzzleMatrix.length][puzzleMatrix[0].length];
		for(int i = 0; i < puzzleMatrix.length; i++) {
			for(int j = 0; j < puzzleMatrix[0].length; j++) {
				solutionMatrix[i][j] = puzzleMatrix[i][j];
			}
		}
		
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
					if(testCell(solutionMatrix, i, j, k)) {
						//set new entry value
						solutionMatrix[i][j] = k;
						forwards = true;	
						moves++;
						//update displayed solution and pause 20 milliseconds
						
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
	}
	
	
	public static boolean testCell(Integer[][] matrix, int row, int column, int value) {
		//test row
		for(int i = 0; i < 9; i++) {
			if(matrix[row][i] == value) {
				return false;
			}
		}
		//test column
		for(int i = 0; i < 9; i++) {
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