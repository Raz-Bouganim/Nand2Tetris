import java.io.*;

public class CodeWriter {
    private BufferedWriter bw;
    private String fileName;
    private int symbolCount = 0;
    private static int labelNum = 1;

    public CodeWriter(File file) {
        bw = null;
        File outputFile = new File(file.getAbsolutePath());
        try {
            bw = new BufferedWriter(new FileWriter(outputFile));
            fileName = file.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFileName(File file) {
        this.fileName = file.getName();
    }
    public void writeInit() {
        try {
            bw.write("@256\nD=A\n@SP\nM=D\n");
            writeCall("Sys.init", 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeLabel(String line){
        try{
            bw.write("(" + line + ")\n"); //איתי שינה ב18:08 ב17/12
        }
        catch (IOException e) {
            throw new RuntimeException("Error writing label: " + line, e);
        }
    }

    public void writeGoto(String line){
        try{
            bw.write("@" + line + "\n0;JMP\n");
        } catch (IOException e) {
            throw new RuntimeException("Error writing goto command for label: " + line, e);
        }
    }

    public void writeIf(String line){
        try{
            bw.write("@SP\nM=M-1\nA=M\nD=M\n@" + line + "\nD;JNE\n");
        }
        catch (IOException e) {
            throw new RuntimeException("Error writing if-goto command for label: " + line, e);
        }
    }


    public void writeCall(String functionName, int numArgs){
        String returnLabel = functionName + "$ret."+ labelNum++;
        try{
            bw.write("@" + returnLabel + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            for (String segment : new String[]{"LCL", "ARG", "THIS", "THAT"}) {
                bw.write("@" + segment + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            }
            bw.write("@SP\nD=M\n@5\nD=D-A\n@" + numArgs + "\nD=D-A\n@ARG\nM=D\n");
            bw.write("@SP\nD=M\n@LCL\nM=D\n");
            writeGoto(functionName);
            writeLabel(returnLabel);
        } catch (IOException e) {
            throw new RuntimeException("Error writing call to function: " + functionName, e);
        }
    }

    public void writeFunction(String functionName, int args) {
        writeLabel(functionName);
        try {
            for (int i = 0; i < args; i++) {
                writePushPop(Parser.CommandType.C_PUSH, "constant", 0);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error writing function: " + functionName, e);
        }
    }

    public void writeReturn() {
        try {
            bw.write("@LCL\nD=M\n@R14\nM=D\n");
            bw.write("@5\nA=D-A\nD=M\n@R15\nM=D\n");
            writePushPop(Parser.CommandType.C_POP, "argument", 0);
            bw.write("@ARG\nD=M+1\n@SP\nM=D\n");
            for (String segment : new String[]{"THAT", "THIS", "ARG", "LCL"}) {
                bw.write("@R14\nD=M-1\nAM=D\nD=M\n@" + segment + "\nM=D\n");
            }
            bw.write("@R15\nA=M\n0;JMP\n");
        } catch (IOException e) {
            throw new RuntimeException("Error writing return command", e);
        }
    }

    public void writeArithmetic(String line) {
        try {
            switch (line) {
                case "add":
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("M=D+M\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                    break;
                case "sub":
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("M=M-D\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                    break;

                case "neg":
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("M=-M\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                    break;
                case "and":
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("M=D&M\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                    break;
                case "or":
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("M=D|M\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                    break;

                case "not":
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("M=!M\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                    break;

                case "eq":
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("D=M-D\n");
                    bw.write("@LABEL" + symbolCount + "\n");
                    bw.write("D;JEQ\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=0\n");
                    bw.write("@ENDLABEL" + symbolCount + "\n");
                    bw.write("0;JMP\n");
                    bw.write("(LABEL" + symbolCount + ")\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=-1\n");
                    bw.write("(ENDLABEL" + symbolCount + ")\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                    symbolCount++;
                    break;

                case "gt":
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("D=M-D\n");
                    bw.write("@LABEL" + symbolCount + "\n");
                    bw.write("D;JGT\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=0\n");
                    bw.write("@ENDLABEL" + symbolCount + "\n");
                    bw.write("0;JMP\n");
                    bw.write("(LABEL" + symbolCount + ")\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=-1\n");
                    bw.write("(ENDLABEL" + symbolCount + ")\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                    symbolCount++;
                    break;

                case "lt":
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("M=M-1\n");
                    bw.write("A=M\n");
                    bw.write("D=M-D\n");
                    bw.write("@LABEL" + symbolCount + "\n");
                    bw.write("D;JLT\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=0\n");
                    bw.write("@ENDLABEL" + symbolCount + "\n");
                    bw.write("0;JMP\n");
                    bw.write("(LABEL" + symbolCount + ")\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=-1\n");
                    bw.write("(ENDLABEL" + symbolCount + ")\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                    symbolCount++;
                    break;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing arithmetic command: " + line, e);
        }
    }
    public void writePushPop(Parser.CommandType command, String segment, int index) {
        try {
            if (command == Parser.CommandType.C_PUSH) {
                switch (segment) {
                    case "constant":
                        bw.write("@" + index + "\n");
                        bw.write("D=A\n");
                        break;
                    case "local":
                        bw.write("@LCL\n");
                        bw.write("D=M\n");
                        bw.write("@" + index + "\n");
                        bw.write("A=D+A\n");
                        bw.write("D=M\n");
                        break;
                    case "argument":
                        bw.write("@ARG\n");
                        bw.write("D=M\n");
                        bw.write("@" + index + "\n");
                        bw.write("A=D+A\n");
                        bw.write("D=M\n");
                        break;
                    case "this":
                        bw.write("@THIS\n");
                        bw.write("D=M\n");
                        bw.write("@" + index + "\n");
                        bw.write("A=D+A\n");
                        bw.write("D=M\n");
                        break;
                    case "that":
                        bw.write("@THAT\n");
                        bw.write("D=M\n");
                        bw.write("@" + index + "\n");
                        bw.write("A=D+A\n");
                        bw.write("D=M\n");
                        break;
                    case "pointer":
                        bw.write("@" + (3 + index) + "\n");
                        bw.write("D=M\n");
                        break;
                    case "temp":
                        bw.write("@" + (5 + index) + "\n");
                        bw.write("D=M\n");
                        break;
                    case "static":
                        bw.write("@" + fileName.split("\\.")[0] + "." + index + "\n");
                        bw.write("D=M\n");
                        break;
                }
                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=D\n");
                bw.write("@SP\n");
                bw.write("M=M+1\n");
            }
            else {
                switch (segment) {
                    case "local":
                        bw.write("@LCL\n");
                        bw.write("D=M\n");
                        bw.write("@" + index + "\n");
                        bw.write("D=D+A\n");
                        break;
                    case "argument":
                        bw.write("@ARG\n");
                        bw.write("D=M\n");
                        bw.write("@" + index + "\n");
                        bw.write("D=D+A\n");
                        break;
                    case "this":
                        bw.write("@THIS\n");
                        bw.write("D=M\n");
                        bw.write("@" + index + "\n");
                        bw.write("D=D+A\n");
                        break;
                    case "that":
                        bw.write("@THAT\n");
                        bw.write("D=M\n");
                        bw.write("@" + index + "\n");
                        bw.write("D=D+A\n");
                        break;
                    case "pointer":
                        bw.write("@" + (3 + index) + "\n");
                        bw.write("D=A\n");
                        break;
                    case "temp":
                        bw.write("@" + (5 + index) + "\n");
                        bw.write("D=A\n");
                        break;
                    case "static":
                        bw.write("@" + fileName.split("\\.")[0] + "." + index + "\n");
                        bw.write("D=A\n");
                        break;
                }
                bw.write("@R13\n");
                bw.write("M=D\n");
                bw.write("@SP\n");
                bw.write("M=M-1\n");
                bw.write("A=M\n");
                bw.write("D=M\n");
                bw.write("@R13\n");
                bw.write("A=M\n");
                bw.write("M=D\n");
            }

        } catch (IOException e) {
            throw new RuntimeException("Error writing push/pop command", e);
        }
    }

    public void close() throws IOException {
        bw.close();
    }
}