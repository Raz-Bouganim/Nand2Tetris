import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class JackTokenizer {
    private Scanner scanner;
    private static ArrayList<String> keywords;
    private static String symbols;
    private static String operations;
    private ArrayList<String> tokens;
    private String code;
    private String tokenType;
    private String keyword;
    private char symbol;
    private String identifier;
    private String stringVal;
    private int intVal;
    private static ArrayList<String> libraries;
    private int pointer;
    private boolean firstToken;

    public enum TokenType {
        KEYWORD,
        SYMBOL,
        IDENTIFIER,
        INT_CONST,
        STRING_CONST
    }

    public enum KeyWord {
        CLASS("class"),
        CONSTRUCTOR("constructor"),
        FUNCTION("function"),
        METHOD("method"),
        FIELD("field"),
        STATIC("static"),
        VAR("var"),
        INT("int"),
        CHAR("char"),
        BOOLEAN("boolean"),
        VOID("void"),
        TRUE("true"),
        FALSE("false"),
        NULL("null"),
        THIS("this"),
        DO("do"),
        IF("if"),
        ELSE("else"),
        WHILE("while"),
        RETURN("return"),
        LET("let");

        private final String keywordString;

        KeyWord(String keywordString) {
            this.keywordString = keywordString;
        }

        public static KeyWord fromString(String str) {
            for (KeyWord k : KeyWord.values()) {
                if (k.keywordString.equals(str)) {
                    return k;
                }
            }
            throw new IllegalArgumentException("No enum constant for keyword: " + str);
        }

        public String getKeywordString() {
            return keywordString;
        }
    }

    public JackTokenizer(File file) {
        try {
            scanner = new Scanner(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        code = "";
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            while (line.equals("") || hasComments(line)) {
                if (hasComments(line)) {
                    line = removeComments(line);
                }
                if (line.trim().equals("")) {
                    if (scanner.hasNextLine()) {
                        line = scanner.nextLine();
                    } else {
                        break;
                    }
                }
            }
            code += line.trim();
        }
        tokens = new ArrayList<>();
        while (code.length() > 0) {
            while (code.charAt(0) == ' ') {
                code = code.substring(1);
            }
            for (int i = 0; i < keywords.size(); i++) {
                if (code.startsWith(keywords.get(i))) {
                    String kw = keywords.get(i);
                    tokens.add(kw);
                    code = code.substring(kw.length());
                }
            }
            if (symbols.contains(code.substring(0, 1))) {
                char sym = code.charAt(0);
                tokens.add(Character.toString(sym));
                code = code.substring(1);
            } else if (Character.isDigit(code.charAt(0))) {
                String value = code.substring(0, 1);
                code = code.substring(1);
                while (Character.isDigit(code.charAt(0))) {
                    value += code.substring(0, 1);
                    code = code.substring(1);
                }
                tokens.add(value);
            } else if (code.startsWith("\"")) {
                code = code.substring(1);
                String str = "\"";
                while (code.charAt(0) != '\"') {
                    str += code.charAt(0);
                    code = code.substring(1);
                }
                str = str + "\"";
                tokens.add(str);
                code = code.substring(1);
            } else if (Character.isLetter(code.charAt(0)) || (code.startsWith("_"))) {
                String id = code.substring(0, 1);
                code = code.substring(1);
                while ((Character.isLetter(code.charAt(0))) || (code.startsWith("_"))) {
                    id += code.substring(0, 1);
                    code = code.substring(1);
                }
                tokens.add(id);
            }
            firstToken = true;
            pointer = 0;
        }
    }

    static {
        keywords = new ArrayList<>();
        keywords.add("class");
        keywords.add("constructor");
        keywords.add("function");
        keywords.add("method");
        keywords.add("field");
        keywords.add("static");
        keywords.add("var");
        keywords.add("int");
        keywords.add("char");
        keywords.add("boolean");
        keywords.add("void");
        keywords.add("true");
        keywords.add("false");
        keywords.add("null");
        keywords.add("this");
        keywords.add("do");
        keywords.add("if");
        keywords.add("else");
        keywords.add("while");
        keywords.add("return");
        keywords.add("let");

        operations = "+-*/&|<>=";
        symbols = "{}()[].,;+-*/&|<>=-~";

        libraries = new ArrayList<>();
        libraries.add("Array");
        libraries.add("Math");
        libraries.add("String");
        libraries.add("Array");
        libraries.add("Output");
        libraries.add("Screen");
        libraries.add("Keyboard");
        libraries.add("Memory");
        libraries.add("Sys");
        libraries.add("Square");
        libraries.add("SquareGame");
    }

    public boolean hasMoreTokens() {
        return pointer < (tokens.size() - 1);
    }

    public void advance() {
        if (hasMoreTokens()) {
            if (!firstToken) {
                pointer++;
            } else {
                firstToken = false;
            }

            String currentToken = tokens.get(pointer);

            if (keywords.contains(currentToken)) {
                tokenType = "KEYWORD";
                keyword = currentToken;
            } else if (symbols.contains(currentToken)) {
                symbol = currentToken.charAt(0);
                tokenType = "SYMBOL";
            } else if (Character.isDigit(currentToken.charAt(0))) {
                intVal = Integer.parseInt(currentToken);
                tokenType = "INT_CONST";
            } else if (currentToken.startsWith("\"")) {
                tokenType = "STRING_CONST";
                stringVal = currentToken.substring(1, currentToken.length() - 1);
            } else if (Character.isLetter(currentToken.charAt(0)) || (currentToken.charAt(0) == '_')) {
                tokenType = "IDENTIFIER";
                identifier = currentToken;
            }
        }
    }

    public void decrementPointer() {
        if (pointer > 0) {
            pointer--;
        }
    }

    private boolean hasComments(String line) {
        return line.contains("//") || line.contains("/*") || line.startsWith(" *");
    }

    private String removeComments(String line) {
        if (!hasComments(line)) {
            return line;
        }
        int offset;
        if (line.startsWith(" *")) {
            offset = line.indexOf("*");
        } else if (line.contains("/*")) {
            offset = line.indexOf("/*");
        } else {
            offset = line.indexOf("//");
        }
        return line.substring(0, offset).trim();
    }

    public String tokenType() {
        return tokenType;
    }

    public TokenType getTokenTypeEnum() {
        switch (tokenType) {
            case "KEYWORD":
                return TokenType.KEYWORD;
            case "SYMBOL":
                return TokenType.SYMBOL;
            case "IDENTIFIER":
                return TokenType.IDENTIFIER;
            case "INT_CONST":
                return TokenType.INT_CONST;
            case "STRING_CONST":
                return TokenType.STRING_CONST;
            default:
                throw new RuntimeException("Unknown token type: " + tokenType);
        }
    }

    public KeyWord getKeyWordEnum() {
        if (!"KEYWORD".equals(tokenType)) {
            throw new RuntimeException("Current token is not a KEYWORD; cannot convert to KeyWordEnum.");
        }
        return KeyWord.fromString(keyword);
    }

    public String keyWord() {
        return keyword;
    }

    public char symbol() {
        return symbol;
    }

    public String identifier() {
        return identifier;
    }

    public int intVal() {
        return intVal;
    }

    public String stringVal() {
        return stringVal;
    }

    public boolean isOperation() {
        for (int i = 0; i < operations.length(); i++) {
            if (operations.charAt(i) == symbol) {
                return true;
            }
        }
        return false;
    }
}