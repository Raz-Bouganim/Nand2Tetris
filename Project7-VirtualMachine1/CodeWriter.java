import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private BufferedWriter bw;
    private int labelCounter;

    public CodeWriter(String filename) throws IOException {
        bw = new BufferedWriter(new FileWriter(filename));
        labelCounter = 0;
    }

    public void writeArithmetic(String command) throws IOException {
        bw.write("// " + command + "\n");

        if (command.equals("add")) {
            bw.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=D+M\n");
        } else if (command.equals("sub")) {
            bw.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=M-D\n");
        } else if (command.equals("neg")) {
            bw.write("@SP\nA=M-1\nM=-M\n");
        } else if (command.equals("and")) {
            bw.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=D&M\n");
        } else if (command.equals("or")) {
            bw.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=D|M\n");
        } else if (command.equals("not")) {
            bw.write("@SP\nA=M-1\nM=!M\n");
        } else if (command.equals("eq") || command.equals("gt") || command.equals("lt")) {
            String jumpCommand = command.equals("eq") ? "JEQ" : command.equals("gt") ? "JGT" : "JLT";

            bw.write("@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n");
            bw.write("@TRUE" + labelCounter + "\n");
            bw.write("D;" + jumpCommand + "\n");
            bw.write("@SP\nA=M-1\nM=0\n");
            bw.write("@END" + labelCounter + "\n");
            bw.write("0;JMP\n");
            bw.write("(TRUE" + labelCounter + ")\n");
            bw.write("@SP\nA=M-1\nM=-1\n");
            bw.write("(END" + labelCounter + ")\n");
            labelCounter++;
        } else {
            throw new IllegalArgumentException("Invalid arithmetic command: " + command);
        }
    }

    public void writePushPop(Parser.commandType command, String segment, int index) throws IOException {
        if (command == Parser.commandType.C_PUSH) {
            bw.write("// push " + segment + " " + index + "\n");
            if (segment.equals("constant")) {
                bw.write("@" + index + "\nD=A\n");
                bw.write("@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            } else if (segment.equals("local") || segment.equals("argument") || segment.equals("this") || segment.equals("that")) {
                String segmentBase = segment.equals("local") ? "LCL" : segment.equals("argument") ? "ARG" : segment.equals("this") ? "THIS" : "THAT";
                bw.write("@" + segmentBase + "\nD=M\n@" + index + "\nA=D+A\nD=M\n");
                bw.write("@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            } else if (segment.equals("pointer")) {
                if (index < 0 || index > 1) throw new IllegalArgumentException("Invalid pointer index: " + index);
                String segmentBase = (index == 0) ? "THIS" : "THAT";
                bw.write("@" + segmentBase + "\nD=M\n");
                bw.write("@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            } else if (segment.equals("static")) {
                if (index < 0 || index > 239) throw new IllegalArgumentException("Invalid static index: " + index);
                String staticVar = "Foo." + index;
                bw.write("@" + staticVar + "\nD=M\n");
                bw.write("@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            } else if (segment.equals("temp")) {
                if (index < 0 || index > 7) throw new IllegalArgumentException("Invalid temp index: " + index);
                int tempAddress = 5 + index;
                bw.write("@" + tempAddress + "\nD=M\n");
                bw.write("@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            }
        } else if (command == Parser.commandType.C_POP) {
            bw.write("// pop " + segment + " " + index + "\n");
            if (segment.equals("local") || segment.equals("argument") || segment.equals("this") || segment.equals("that")) {
                String segmentBase = segment.equals("local") ? "LCL" : segment.equals("argument") ? "ARG" : segment.equals("this") ? "THIS" : "THAT";
                bw.write("@" + segmentBase + "\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n");
                bw.write("@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
            } else if (segment.equals("pointer")) {
                if (index < 0 || index > 1) throw new IllegalArgumentException("Invalid pointer index: " + index);
                String segmentBase = (index == 0) ? "THIS" : "THAT";
                bw.write("@SP\nAM=M-1\nD=M\n");
                bw.write("@" + segmentBase + "\nM=D\n");
            } else if (segment.equals("static")) {
                if (index < 0 || index > 239) throw new IllegalArgumentException("Invalid static index: " + index);
                String staticVar = "Foo." + index;
                bw.write("@SP\nAM=M-1\nD=M\n");
                bw.write("@" + staticVar + "\nM=D\n");
            } else if (segment.equals("temp")) {
                if (index < 0 || index > 7) throw new IllegalArgumentException("Invalid temp index: " + index);
                int tempAddress = 5 + index;
                bw.write("@SP\nAM=M-1\nD=M\n");
                bw.write("@" + tempAddress + "\nM=D\n");
            }
        }
    }

    public void close() throws IOException {
        bw.close();
    }
}
