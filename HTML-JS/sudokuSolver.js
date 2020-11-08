/*** Program begins here when 'solve' button is pressed ***/
function readInput(inputForm){
	// read input fields to 2 9x9 matrices
	var inputMatrix = new Array(9);
	var solutionMatrix = new Array(9);
	for(var i = 0; i < inputMatrix.length; i++){
		inputMatrix[i] = new Array(9);
		solutionMatrix[i] = new Array(9);
		for(var j = 0; j < 9; j++){
			// convert from 1d array to 2d matrix coordinates
			var k = i*9 + j;
			// extract input field value
			var x = inputForm[k].value;
			// convert blank fields into 0's
			if(x == ""){
				x = 0;
			}
			// stop and alert if input is invalid
			if(isNaN(x) || x < 0 || x > 9){
				alert("Enter only numbers between 1 and 9\nCell("+(i+1)+","+(j+1)+"): "+x);
				return;
			}
			inputMatrix[i][j] = x;
			solutionMatrix[i][j] = x;
		}
	}
	console.log("Input matrix creation successful");

	// check if puzzle is solvable
	var valid = validateMatrix(inputMatrix);
	if(!valid){
		alert("Input isn't a valid puzzle");
		return;
	}
	console.log("Validation Passed");
	
	// read radio buttons and slider
	var realtime;
	var delay;
	if(inputForm.radio.value == "radio-realtime"){
		realtime = true;
		delay = inputForm.slider.value;
	}
	else{
		realtime = false;
		delay = 0;
	}
	
	// hide instructions button
	document.getElementById("inst-button").innerHTML = "";
	
	// solve the puzzle
	outputToTable(solutionMatrix,inputMatrix);
	solvePuzzle(inputMatrix,solutionMatrix,realtime,delay);
	// 'instant' mode needs to manually output result
	if(!realtime){
		outputToTable(solutionMatrix,inputMatrix);
	}
}
/*** Program terminates here ***/

// output matrix to html table
function outputToTable(solutionMatrix,inputMatrix){
	var output = "";
	output += "<table id=\"outputTable\">";
	for(var i = 0; i < 9; i++){
		output += "<tr>";
		for(var j = 0; j < 9; j++){
			output += "<td>"
			// identify numbers from original input
			if(inputMatrix[i][j] != 0){
				output += "<span id=\"original\">"+solutionMatrix[i][j]+"</span>";
			}
			// don't display 0's
			else if(solutionMatrix[i][j] == 0){
				output += "";
			}
			else{
				output += solutionMatrix[i][j];
			}
			output += "</td>";
		}
		output += "</tr>";
	}
	output += "</table>";
	document.getElementById("startForm").innerHTML = output;
}

// check if user entered a valid puzzle
function validateMatrix(matrix){
	for(var i = 0; i < matrix.length; i++) {
		for(var j = 0; j < matrix[0].length; j++) {
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

// test if given value can be added into given cell
function testCell(matrix,row,column,value,solvingCheck){
	// boolean 'solvingCheck' is true when solving and false when validating
	// it is used to skip the current cell during validation
	// test row and column
	for(var i = 0; i < 9; i++){
		if(i == column && !solvingCheck){
			continue;
		}
		else if(i == row && !solvingCheck) {
			continue;
		}
		else if(matrix[row][i] == value){
			return false;
		}
		else if(matrix[i][column] == value){
			return false;
		}
	}
	// test neighborhood 
	var xPos = Math.floor(row / 3);
	var yPos = Math.floor(column / 3);
	for(var i = xPos*3; i < (xPos*3)+3; i++){
		for(var j = yPos*3; j < (yPos*3)+3; j++){
			if(i == row && j == column){
				continue;
			}
			else if(matrix[i][j] == value){
				return false
			}
		}
	}
	// if row, column, and neighborhood passed
	return true;
}

// solve the puzzle
async function solvePuzzle(inputMatrix,solutionMatrix,realtime,delay){
	var i = 0; //row
	var j = 0; //column
	var moves = 0; //tracks number of attempted moves
	var forwards = true; //used to determine if program is moving to next cell or backtracking
	while(i < 9){
		// Original cell is not blank
		if(inputMatrix[i][j] != 0){
			// if moving forward, go to next cell
			if(forwards){
				if(j == 8){
					i++;
					j = 0;
				}else{
					j++;
				}
			}
			// if moving backward, go to previous cell
			else{
				if(j == 0){
					i--;
					j = 8;
				}else{
					j--;
				}
			}
		}
		// Original cell is blank
		else{
			var k = 1;
			for(k = solutionMatrix[i][j]; k <=9; k++){
				if(k == 0){
					//current value is 0, skip to 1 and start checking
					continue;
				}
				// Is k a valid entry
				if(testCell(solutionMatrix,i,j,k,true)){
					// set new entry value
					solutionMatrix[i][j] = k;
					forwards = true;
					moves++;
					// if realtime is true, the table will update each new entry and pause 'delay'ms
					if(realtime){
						var temp = document.getElementById("outputTable");
						temp.rows[i].cells[j].innerHTML = k;
						await sleep(delay);
					}
					break;
				}
			}
			// if loop stopped before reaching end, entry is good, go to next cell
			if(k < 10){
					if(j == 8){
						i++;
						j = 0;
					}else{
						j++;
					}
					continue;
			}
			// if loop ran all the way unsuccessfully
			else{
				// start working backwards
				forwards = false;
				// clear current cell value
				solutionMatrix[i][j] = 0;
				// go to previous cell
				if(j == 0){
					i--;
					j = 8;
				}else{
					j--;
				}
			}
		}
	}
	// insert commas into 'moves'
	var movesFormatted = new Intl.NumberFormat().format(moves);
	console.log("Completed in "+movesFormatted+" moves");
	document.getElementById("heading").innerHTML = "<h2>Solution</h2><p>Solved in "+movesFormatted+" moves</p>";
}

// used to add a delay when updating the table in realtime
function sleep(milliseconds){
	return new Promise(resolve => setTimeout(resolve, milliseconds));
}