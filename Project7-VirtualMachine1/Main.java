
public class Main {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: java VMTranslator fileName.vm");
            return;
        }

        String inputFileName = args[0];
        if (!inputFileName.endsWith(".vm")) {
            System.out.println("Input file must have a .vm extension");
            return;
        }

        String outputFileName = inputFileName.replace(".vm", ".asm");

        try {
            Parser parser = new Parser(inputFileName);
            CodeWriter codeWriter = new CodeWriter(outputFileName);

            while (parser.hasMoreLines()) {
                parser.advance();
                Parser.commandType commandType = parser.commandType();

                if(commandType == Parser.commandType.C_ARITHMETIC){
                    codeWriter.writeArithmetic(parser.arg1());
                } else if (commandType == Parser.commandType.C_PUSH || commandType == Parser.commandType.C_POP) {
                    codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
                }
            }
            codeWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}