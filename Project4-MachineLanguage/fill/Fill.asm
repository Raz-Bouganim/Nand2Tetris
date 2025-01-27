// Save screen address in R0
(RESTART)
@SCREEN
D=A
@0
M=D	
// Checks if a key is pressed
(KBDCHECK)
@KBD
D=M
@BLACK
D;JGT	
@WHITE
D;JEQ	
@KBDCHECK
0;JMP
// Set the flag to black
(BLACK)
@1
M=-1
@FILL
0;JMP
// Set the flag to white
(WHITE)
@1
M=0	
@FILL
0;JMP
// Filling the screen with black or white depending on the flag
(FILL)
@1	// Check flag
D=M	
@0
A=M	
M=D
@0
D=M+1	// Move to the following screen pixel
@KBD
D=A-D	// Loop condition
@0
M=M+1	// Inc to the next pixel
A=M
@FILL
D;JGT	// Loop until the whole screen is filled
@RESTART
0;JMP