import java.io.*;

public class Parser {
    private BufferedReader reader;
    private String currentCommand;
    public enum InstructionType {
        A_INSTRUCTION,
        C_INSTRUCTION,
        L_INSTRUCTION
    }


    public Parser(String fileName) throws IOException {
        reader = new BufferedReader(new FileReader(fileName));
    }

    public boolean hasMoreLines() throws IOException {
        return reader.ready();
    }

    public void advance() throws IOException {
        do {
            currentCommand = reader.readLine();
            if (currentCommand == null) return; // End of file
            currentCommand = currentCommand.strip();
        } while (currentCommand.isEmpty() || currentCommand.startsWith("//"));
    }

    public InstructionType instructionType() {
        if (currentCommand.startsWith("@")) {
            return InstructionType.A_INSTRUCTION;
        } else if (currentCommand.startsWith("(") && currentCommand.endsWith(")")) {
            return InstructionType.L_INSTRUCTION;
        } else {
            return InstructionType.C_INSTRUCTION;
        }
    }

    public String symbol() {
        if (instructionType() == InstructionType.A_INSTRUCTION) {
            return currentCommand.substring(1);
        }
        if (instructionType() == InstructionType.L_INSTRUCTION) {
            return currentCommand.substring(1, currentCommand.length() - 1);
        }
        return null;
    }

    public String dest() {
        if(currentCommand.contains(("="))){
            return currentCommand.split("=")[0];
        }
        return "";
    }

    public String comp() {
        String[] parts = currentCommand.split("[=;]");
        if (currentCommand.contains("=")) {
            return parts[1]; // After '='
        } else if (currentCommand.contains(";")) {
            return parts[0]; // Before ';'
        }
        return currentCommand; // Entire command if no '=' or ';'
    }


    public String jump() {
        if(currentCommand.contains(";")){
            return currentCommand.split(";")[1];
        }
        return "";
    }
}
