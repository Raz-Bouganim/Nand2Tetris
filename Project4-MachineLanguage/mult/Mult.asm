//Reset R2
@2
M=0
// checking if R0 = 0
@0
D=M
@2
D;JEQ
// checking if R1 = 0
 @1
D=M
@END
D;JEQ 
// R3 = new counter, equals to the first number
@0
D=M
@3
M=D
(LOOP)
// D = second number
@1
D=M
// R[2] (answer) += secNumber
@2
M=D+M
// Reduce the counter (first number)
@3
M=M-1
D=M
@LOOP
D;JGT
(END)
@END
0;JMP