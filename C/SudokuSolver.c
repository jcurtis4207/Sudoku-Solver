#include <stdio.h>

int printMatrix(int *p, int length);
int testCell(int *p, int row, int column, int value, int test);
int validateMatrix(int *p, int size);

int main(){
	//create puzzle matrix
	int puzzleMatrix [9][9] = {
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
	//copy puzzle to working matrix
	int workingMatrix[9][9];
	int workingSize = sizeof(workingMatrix)/sizeof(int);
	int *ptrWorking = &workingMatrix[0][0];
	int i, j;
	for(i = 0; i < 9; i++){
		for(j = 0; j < 9; j++){
			workingMatrix[i][j] = puzzleMatrix[i][j];
		}
	}
	//SOLVE THE PUZZLE
	i = 0;
	j = 0;
	int moves = 0;
	int forwards = 1; //used to determine if program is moving to next cell or backtracking
	while(i < 9){
		//Original cell is not blank
		if(puzzleMatrix[i][j] != 0){
			//if moving forward - go to next cell
			if(forwards == 1){
				if(j == 8){
					i++;
					j = 0;
				}
				else{
					j++;
				}
			}
			//if moving backward - go to previous cell
			else{
				if(j == 0){
					i--;
					j = 8;
				}
				else{
					j--;
				}
			}
		}
		//Original cell is blank
		else{
			int k;
			for(k = workingMatrix[i][j]; k < 10; k++){
				if(k == 0){
					//current value is 0, skip to 1 and start checking
					continue;
				}
				//is k a valid entry?
				int test = testCell(ptrWorking, i, j, k, 1);
				if(test == 1){
					//set new entry value
					workingMatrix[i][j] = k;
					forwards = 1;
					moves++;
					break;
				}
			}
			//if loop stopped, entry is good, move to next cell
			if(k < 10){
				if(j == 8){
					i++;
					j = 0;
				}
				else{
					j++;
				}
			}
			//if loop ran all the way unsuccessfully
			else{
				//start working backwards
				forwards = 0;
				//clear current cell
				workingMatrix[i][j] = 0;
				//go to previous cell
				if(j == 0){
					i--;
					j = 8;
				}
				else{
					j--;
				}
			}
		}
	}
	printMatrix(ptrWorking, workingSize);
	printf("\nSolved in %d moves", moves);
	return 0;
}

int validateMatrix(int *p, int size){
	int i;
	for(i = 0; i < size; i++){
		if(*(p+i) < 0 || *(p+i) > 9){
			return 0;
		}
		if(*(p+i) != 0){
			int test = testCell(p, i/9, i%9, *(p+i), 0);
			if(test == 0){
				return 0;
			}
		}
	}
	return 1;
}

int testCell(int *p, int row, int column, int value, int test){
	//'test' is 1 when solving and 0 when validating
	//it is used to skip the current cell during validation
	//test row
	int i,j;
	for(i = 0; i < 9; i++){
		if(i == column && test == 0){
			continue;
		}
		if(*(p+(row*9)+i) == value){
			return 0;
		}
	}
	//test column
	for(i = 0; i < 9; i++){
		if(i == row && test == 0){
			continue;
		}
		if(*(p+(i*9)+column) == value){
			return 0;
		}
	}
	//test neighborhood
	int xPos = row/3;
	int yPos = column/3;
	for(i = xPos*3; i < (xPos*3)+3; i++){
		for(j = yPos*3; j < (yPos*3)+3; j++){
			if(i == row && j == column){
				continue;
			}
			if(*(p+(i*9)+j) == value){
				return 0;
			}
		}
	}
	//if row, column, and neighborhood pass
	return 1;
}

int printMatrix(int *p, int length){
	int i;
	for(i = 0; i < length; i++){
		printf("%d ", *p);
		p++;
		if(i%9 == 8){
			printf("\n");
		}
	}
	return 0;
}

