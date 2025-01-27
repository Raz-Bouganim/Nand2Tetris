import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
    private BufferedReader br;
    private String currentCommand;
    public enum commandType {
        C_ARITHMETIC,
        C_PUSH,
        C_POP
    }

    public Parser(String fileName) throws IOException {
        br = new BufferedReader(new FileReader(fileName));
        currentCommand = "";
    }

    public boolean hasMoreLines() throws IOException {
        return br.ready();
    }

    public void advance() throws IOException {
        currentCommand = br.readLine().trim();
        while(currentCommand.isEmpty() || currentCommand.startsWith("//")) {
            currentCommand = br.readLine();
            if(currentCommand == null) break;
            currentCommand = currentCommand.trim();
        }
    }

    public commandType commandType(){
        if(currentCommand.startsWith("push")) return commandType.C_PUSH;
        if(currentCommand.startsWith("pop")) return commandType.C_POP;
        return commandType.C_ARITHMETIC;
    }

    public String arg1() {
        if (commandType() == commandType.C_ARITHMETIC) {
            return currentCommand;
        }
        return currentCommand.split(" ")[1];
    }

    public int arg2(){
        return Integer.parseInt(currentCommand.split(" ")[2]);
    }
}
