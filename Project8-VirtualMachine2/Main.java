import java.io.*;

public class Main {
    private static CodeWriter Writer;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: java Main <inputFile.vm | inputDirectory>");
            return;
        }

        File inputFile = new File(args[0]);

        if (inputFile.isDirectory()) {
            File[] files = inputFile.listFiles((dir, name) -> name.endsWith(".vm"));
            if (files == null || files.length == 0) {
                System.err.println("No .vm files found in the directory: " + inputFile);
                return;
            }
            File outputFile = new File(inputFile, inputFile.getName() + ".asm");
            Writer = new CodeWriter(outputFile);

            boolean hasSysFile = false;
            for (File file : files) {
                if (file.getName().equals("Sys.vm")) {
                    hasSysFile = true;
                    break;
                }
            }

            if (hasSysFile) {
                Writer.writeInit();
            }

            for (File file : files) {
                translator(file);
            }

        } else {

            if (!inputFile.getName().endsWith(".vm")) {
                System.err.println("Input file must have a .vm extension: " + inputFile);
                return;
            }

            File outputFile = new File(inputFile.getParentFile(),
                    inputFile.getName().split(".vm")[0] + ".asm");
            Writer = new CodeWriter(outputFile);
            translator(inputFile);
        }
        Writer.close();
    }


    private static void translator(File file) {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Parser parser = new Parser(reader);
            Writer.setFileName(new File(file.getName().split(".vm")[0] + ".asm"));

            while (parser.hasMoreCommands()) {
                parser.advance();
                switch (parser.commandType()) {
                    case C_POP:
                    case C_PUSH:
                        Writer.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                        break;
                    case C_ARITHMETIC:
                        Writer.writeArithmetic(parser.arg1());
                        break;
                    case C_LABEL:
                        Writer.writeLabel(parser.arg1());
                        break;
                    case C_GOTO:
                        Writer.writeGoto(parser.arg1());
                        break;
                    case C_IF:
                        Writer.writeIf(parser.arg1());
                        break;
                    case C_FUNCTION:
                        Writer.writeFunction(parser.arg1(), parser.arg2());
                        break;
                    case C_RETURN:
                        Writer.writeReturn();
                        break;
                    case C_CALL:
                        Writer.writeCall(parser.arg1(), parser.arg2());
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error processing file: " + file.getName());
            e.printStackTrace();
        }
    }
}