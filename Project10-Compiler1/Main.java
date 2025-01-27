import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        File inputDir = new File(args[0]);
        ArrayList<File> jackFiles = new ArrayList<>();
        
        if (inputDir.isFile() && args[0].endsWith(".jack")) {
            jackFiles.add(inputDir);
        } else if (inputDir.isDirectory()) {
            jackFiles = getJackFiles(inputDir);
        }

        for (File jackFile : jackFiles) {
            String baseName = jackFile.toString().substring(0, jackFile.toString().length() - 5);
            String xmlOutput = baseName + ".xml";
            String tokenOutput = baseName + "T.xml";
            
            JackTokenizer tokenizer = new JackTokenizer(jackFile);
            generateTokenFile(tokenizer, new File(tokenOutput));
            
            // Second pass - generate parsed XML file (.xml)
            tokenizer = new JackTokenizer(jackFile); // Reinitialize tokenizer
            CompilationEngine engine = new CompilationEngine(tokenizer, new File(xmlOutput));
            engine.compileClass();
        }
    }

    private static ArrayList<File> getJackFiles(File dir) {
        ArrayList<File> jackFiles = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jack")) {
                jackFiles.add(file);
            }
        }
        return jackFiles;
    }

    private static void generateTokenFile(JackTokenizer tokenizer, File outputFile) {
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("<tokens>\n");
            while (tokenizer.hasMoreTokens()) {
                tokenizer.advance();
                String tokenValue = "";
                switch (tokenizer.tokenType()) {
                    case "KEYWORD":
                        tokenValue = tokenizer.keyWord();
                        writer.write("<keyword> " + tokenValue + " </keyword>\n");
                        break;
                    case "SYMBOL":
                        tokenValue = String.valueOf(tokenizer.symbol());
                        if (tokenValue.equals("<")) {
                            tokenValue = "&lt;";
                        } else if (tokenValue.equals(">")) {
                            tokenValue = "&gt;";
                        } else if (tokenValue.equals("&")) {
                            tokenValue = "&amp;";
                        } else if (tokenValue.equals("\"")) {
                            tokenValue = "&quot;";
                        }
                        writer.write("<symbol> " + tokenValue + " </symbol>\n");
                        break;
                    case "IDENTIFIER":
                        tokenValue = tokenizer.identifier();
                        writer.write("<identifier> " + tokenValue + " </identifier>\n");
                        break;
                    case "INT_CONST":
                        tokenValue = String.valueOf(tokenizer.intVal());
                        writer.write("<integerConstant> " + tokenValue + " </integerConstant>\n");
                        break;
                    case "STRING_CONST":
                        tokenValue = tokenizer.stringVal();
                        writer.write("<stringConstant> " + tokenValue + " </stringConstant>\n");
                        break;
                }
            }
            writer.write("</tokens>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}