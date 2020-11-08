def main():
    #create input puzzle
    puzzle_matrix = [
        [5,3,0,0,7,0,0,0,0],
        [6,0,0,1,9,5,0,0,0],
        [0,9,8,0,0,0,0,6,0],
        [8,0,0,0,6,0,0,0,3],
        [4,0,0,8,0,3,0,0,1],
        [7,0,0,0,2,0,0,0,6],
        [0,6,0,0,0,0,2,8,0],
        [0,0,0,4,1,9,0,0,5],
        [0,0,0,0,8,0,0,7,9]]
    #copy input puzzle to working puzzle
    working_matrix = [[0 for x in range(9)] for y in range(9)]
    for i in range(len(puzzle_matrix)):
        for j in range(len(puzzle_matrix[0])):
            working_matrix[i][j] = puzzle_matrix[i][j]
    i = 0
    j = 0
    moves = 0
    forwards = True #used to determine if program is moving to next cell or backtracking
    while i < 9:
        #puzzle cell is not blank
        if puzzle_matrix[i][j] != 0:
            #'forward' determines if moving forwards or backwards
            i,j = move_cell(i, j, forwards)
        #puzzle cell is blank
        else:
            valid = False   #used to determine if entry was set
            k = working_matrix[i][j]
            for k in range(k,10):
                if k == 0:
                    #current value is 0, skip to 1 and start checking
                    continue
                #is k a valid entry?
                test = test_cell(working_matrix, i, j, k, True)
                if test == True:
                    #set new entry value
                    working_matrix[i][j] = k
                    forwards = True
                    moves = moves + 1
                    valid = True
                    break
            #if entry is valid - go to next cell
            #if entry wasn't valid - start working backwards, clear current cell, go to previous cell
            if valid == False:
                forwards = False
                working_matrix[i][j] = 0
            i,j = move_cell(i, j, valid)
    print_matrix(working_matrix)
    print("\nSolved in "+str(moves)+" moves")
    return

def move_cell(i, j, test):
    if test == True:
        if j == 8:
            i = i + 1
            j = 0
        else:
            j = j + 1
    else:
        if j == 0:
            i = i - 1
            j = 8
        else:
            j = j - 1
    return i,j

def validate_matrix(matrix):
    for i in range(len(matrix)):
        for j in range(len(matrix[0])):
            if matrix[i][j] < 0 or matrix[i][j] > 9:
                return False
            if matrix[i][j] != 0:
                test = test_cell(matrix, i, j, matrix[i][j], False)
                if test == False:
                    return False
    return True
                
def test_cell(matrix, row, column, value, solvingCheck):
    #'solvingCheck' is true when solving and false when validating
    #it is used to skip current cell when validating
    #test row
    for i in range(9):
        if i == column and solvingCheck == False:
            continue
        if matrix[row][i] == value:
            return False
    #test column
    for i in range(9):
        if i == row and solvingCheck == False:
            continue
        if matrix[i][column] == value:
            return False    
    #test neighborhood
    x_position = row // 3
    y_position = column // 3
    for i in range(x_position*3, x_position*3+3):
        for j in range(y_position*3, y_position*3+3):
            if i == row and j == column:
                continue
            if matrix[i][j] == value:
                return False
    return True

def print_matrix(matrix):
    for i in range(len(matrix)):
        for j in range(len(matrix[i])):
            print(matrix[i][j], end=" ")
        print("",end="\n")
    return 
    
if __name__ == '__main__':
    main()