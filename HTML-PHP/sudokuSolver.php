<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <link rel="stylesheet" type="text/css" href="sudokuSolver-style.css">
        <link href='https://fonts.googleapis.com/css?family=Ubuntu:bold' rel='stylesheet' type='text/css'>
        <link href='https://fonts.googleapis.com/css?family=Vollkorn' rel='stylesheet' type='text/css'>
        <title>Sudoku Solver</title> 
        <?php
            function readInput(){
                # populate input and solution matrices
                $inputMatrix = array();
                $solutionMatrix = array();
                for($i = 0; $i < 9; $i++){
                    for($j = 0; $j < 9; $j ++){
                        $value = $_POST[$i."_".$j];
                        # convert blank fields into 0s
                        if($value == ''){
                            $value = 0;
                        }
                        # check if input is invalid
                        if(!is_numeric($value) || $value < 0 || $value > 9){
                            echo "Input is Invalid";
                            exit;
                        }
                        $inputMatrix[$i][$j] = $value;
                        $solutionMatrix[$i][$j] = $value;
                    }
                }
                # check if puzzle is solvable
                $valid = validateMatrix($inputMatrix);
                if(!$valid){
                    echo "Input isn't a valid puzzle";
                    exit;
                }
                # solve the puzzle
                solvePuzzle($inputMatrix, $solutionMatrix);
            }
            # check if user entered a valid puzzle
            function validateMatrix($matrix){
                for($i = 0; $i < 9; $i++){
                    for($j = 0; $j < 9; $j++){
                        if($matrix[$i][$j] < 0 || $matrix[$i][$j] > 9){
                            return false;
                        }
                        if($matrix[$i][$j] != 0){
                            if(!testCell($matrix, $i, $j, $matrix[$i][$j], false)){
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
            # test if given value can be added into given cell
            function testCell($matrix, $row, $column, $value, $solvingCheck){
                # boolean 'solvingCheck' is true when solving and false when validating
                # it is used to skip the current cell during validation
                # test row and column
                for($i = 0; $i < 9; $i++){
                    if($i == $column && !$solvingCheck){
                        continue;
                    }
                    else if($i == $row && !$solvingCheck){
                        continue;
                    }
                    else if($matrix[$row][$i] == $value){
                        return false;
                    }
                    else if($matrix[$i][$column] == $value){
                        return false;
                    }
                }
                # test neighborhood
                $xPos = intdiv($row,3);
                $yPos = intdiv($column,3);
                for($i = $xPos*3; $i < ($xPos*3)+3; $i++){
                    for($j = $yPos*3; $j < ($yPos*3)+3; $j++){
                        if($i == $row && $j == $column){
                            continue;
                        }
                        else if($matrix[$i][$j] == $value){
                            return false;
                        }
                    }
                }
                # if row, column, and neighborhood passed
                return true;
            }
            # solve the puzzle
            function solvePuzzle($inputMatrix, $solutionMatrix){
                $i = 0; # row
                $j = 0; # column
                $moves = 0; #tracks number of attempted moves
                $forwards = true; # used to determine if program is moving to next cell or backtracking
                while($i < 9){
                    # Original cell is not blank
                    if($inputMatrix[$i][$j] != 0){
                        # if moving forward, go to next cell
                        if($forwards){
                            if($j == 8){
                                $i++;
                                $j = 0;
                            }else{
                                $j++;
                            }
                        }
                        # if moving backwards, go to previous cell
                        else{
                            if($j == 0){
                                $i--;
                                $j = 8;
                            }else{
                                $j--;
                            }
                        }
                    }
                    # Original cell is blank
                    else{
                        $k = 1;
                        for($k = $solutionMatrix[$i][$j]; $k <= 9; $k++){
                            if($k == 0){
                                #current value is 0, skip to 1 and start checking
                                continue;
                            }
                            # Is k a valid entry
                            if(testCell($solutionMatrix, $i, $j, $k, true)){
                                # set new entry value
                                $solutionMatrix[$i][$j] = $k;
                                $forwards = true;
                                $moves++;
                                break;
                            }
                        }
                        # if loop stopped before reaching end, entry is good, go to next cell
                        if($k < 10){
                            if($j == 8){
                                $i++;
                                $j = 0;
                            }else{
                                $j++;
                            }
                        }
                        # if loop ran all the way unsuccessfully
                        else{
                            $forwards = false;
                            # clear current cell value
                            $solutionMatrix[$i][$j] = 0;
                            # go to previous cell
                            if($j == 0){
                                $i--;
                                $j = 8;
                            }else{
                                $j--;
                            }
                        }
                    }
                }
                # output solution to screen
                outputToTable($solutionMatrix, $inputMatrix, $moves);
            }
            # output matrix to html table
            function outputToTable($solutionMatrix, $inputMatrix, $moves){
                $output = '';
                $output .= "<table id=\"outputTable\">";
                for($i = 0; $i < 9; $i++){
                    $output .= "<tr>";
                    for($j = 0; $j < 9; $j++){
                        $output .= "<td>";
                        # identify numbers from original input
                        if($inputMatrix[$i][$j] != 0){
                            $output .= "<span id=\"original\">" . $solutionMatrix[$i][$j] . "</span>";
                        }
                        # don't display 0s
                        else if($solutionMatrix[$i][$j] == 0){
                            $output .= '';
                        }
                        else{
                            $output .= $solutionMatrix[$i][$j];
                        }
                        $output .= "</td>";
                    }
                    $output .= "</tr>";
                }
                $output .= "</table>";
                $output .= "<h2>Solved in " . $moves . " moves</h2>";
                echo $output;
            }
        ?>
    </head>
    <body>
        <div id="solution">
                <h1>Solution</h1>
                <?php readInput() ?>
        </div>
    </body>
</html>