import java.io.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HackAssembly <Prog.asm>");
            return;
        }

        String inputFile = args[0];
        String outputFile = inputFile.replace(".asm", ".hack");

        try {
            Parser parser = new Parser(inputFile);
            SymbolTable symbolTable = new SymbolTable();
            Code code = new Code();

            int romAddress = 0;
            while (parser.hasMoreLines()) {
                parser.advance();
                if (parser.instructionType() == Parser.InstructionType.L_INSTRUCTION) {
                    symbolTable.addEntry(parser.symbol(), romAddress);
                } else {
                    romAddress++;
                }
            }

            parser = new Parser(inputFile);
            try (BufferedWriter writer = Files.newBufferedWriter(Path.of(outputFile))) {
                int ramAddress = 16;
                while (parser.hasMoreLines()) {
                    parser.advance();
                    if (parser.instructionType() == Parser.InstructionType.A_INSTRUCTION) {
                        String symbol = parser.symbol();
                        int address;
                        if (Character.isDigit(symbol.charAt(0))) {
                            address = Integer.parseInt(symbol);
                        } else {
                            if (!symbolTable.contains(symbol)) {
                                symbolTable.addEntry(symbol, ramAddress++);
                            }
                            address = symbolTable.getAddress(symbol);
                        }

                        writer.write(String.format("%16s%n", Integer.toBinaryString(address)).replace(' ', '0'));
                    } else if (parser.instructionType() == Parser.InstructionType.C_INSTRUCTION) {
                        String binary = "111" +
                                code.comp(parser.comp()) +
                                code.dest(parser.dest()) +
                                code.jump(parser.jump());
                        writer.write(binary + "\n");
                    }
                }
            }
            System.out.println("Translation complete: " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
