import java.io.BufferedReader;
import java.io.IOException;

public class Parser {

    private final BufferedReader br;
    private String currentCommand;
    public enum CommandType {
        C_ARITHMETIC,
        C_PUSH,
        C_POP,
        C_LABEL,
        C_GOTO,
        C_IF,
        C_FUNCTION,
        C_RETURN,
        C_CALL
    }

    public Parser(BufferedReader reader) {
        this.br = reader;
    }

    public boolean hasMoreCommands() {
        try {
            return br.ready();
        } catch (IOException e) {
            throw new RuntimeException("Failed to check for more commands: " + e.getMessage(), e);
        }
    }

    public void advance() {
        try {
            currentCommand = br.readLine();
            if (currentCommand != null) {
                int commentIndex = currentCommand.indexOf("//");
                if (commentIndex >= 0) {
                    currentCommand = currentCommand.substring(0, commentIndex);
                }
                currentCommand = currentCommand.trim();
                if (currentCommand.isEmpty()) {
                    advance();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the next command: " + e.getMessage(), e);
        }
    }

    public CommandType commandType() {
        try {
            String[] parts = currentCommand.split(" ");
            String command = parts[0];

            return switch (command) {
                case "push" -> CommandType.C_PUSH;
                case "pop" -> CommandType.C_POP;
                case "label" -> CommandType.C_LABEL;
                case "goto" -> CommandType.C_GOTO;
                case "if-goto" -> CommandType.C_IF;
                case "function" -> CommandType.C_FUNCTION;
                case "return" -> CommandType.C_RETURN;
                case "call" -> CommandType.C_CALL;
                default -> CommandType.C_ARITHMETIC;
            };
        } catch (NullPointerException e) {
            throw new RuntimeException("Failed to parse the current command: " + e.getMessage(), e);
        }

    }

    public String arg1() {
        CommandType type = commandType();
        if (type == CommandType.C_ARITHMETIC) {
            return currentCommand.split(" ")[0];
        } else if (type != CommandType.C_RETURN){
            return currentCommand.split(" ")[1];
        }
        return null;
    }

    public int arg2() {
        CommandType type = commandType();
        if (type == CommandType.C_PUSH || type == CommandType.C_POP || type == CommandType.C_FUNCTION || type == CommandType.C_CALL) {
            return Integer.parseInt(currentCommand.split(" ")[2]);
        }
        throw new UnsupportedOperationException("arg2 is not supported for this command type");
    }
}