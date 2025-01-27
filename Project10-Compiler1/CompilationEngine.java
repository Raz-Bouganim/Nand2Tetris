import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {
    private FileWriter writer;
    private JackTokenizer tokenizer;
    private boolean isFirstRoutine;

    public CompilationEngine(JackTokenizer tokenizer, File outputFile) {
        try {
            writer = new FileWriter(outputFile);
            this.tokenizer = tokenizer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        isFirstRoutine = true;
    }

    public void compileClass() {
        try {
            tokenizer.advance();
            writer.write("<class>\n");
            writer.write("<keyword> class </keyword>\n");
            tokenizer.advance();
            writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenizer.advance();
            writer.write("<symbol> { </symbol>\n");
            compileClassVarDec();
            compileSubRoutine();
            writer.write("<symbol> } </symbol>\n");
            writer.write("</class>\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileClassVarDec() {
        tokenizer.advance();
        try {
            while (tokenizer.keyWord().equals("static") || tokenizer.keyWord().equals("field")) {
                writer.write("<classVarDec>\n");
                writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>\n");
                tokenizer.advance();
                if (tokenizer.tokenType().equals("IDENTIFIER")) {
                    writer.write("<identifier> " + tokenizer.identifier() + "</identifier>\n");
                } else {
                    writer.write("<keyword> " + tokenizer.keyWord() + "</keyword>\n");
                }
                tokenizer.advance();
                writer.write("<identifier> " + tokenizer.identifier() + "</identifier>\n");
                tokenizer.advance();
                while (tokenizer.symbol() == ',') {
                    writer.write("<symbol> , </symbol>\n");
                    tokenizer.advance();
                    writer.write("<identifier> " + tokenizer.identifier() + "</identifier>\n");
                    tokenizer.advance();
                }
                writer.write("<symbol> ; </symbol>\n");
                tokenizer.advance();
                writer.write("</classVarDec>\n");
            }
            if (tokenizer.keyWord().equals("function") || tokenizer.keyWord().equals("method") || tokenizer.keyWord().equals("constructor")) {
                tokenizer.decrementPointer();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileSubRoutine() {
        boolean hasSubRoutines = false;
        tokenizer.advance();
        try {
            if (tokenizer.symbol() == '}' && tokenizer.tokenType().equals("SYMBOL")) {
                return;
            }
            if (isFirstRoutine && (tokenizer.keyWord().equals("function") || tokenizer.keyWord().equals("method") || tokenizer.keyWord().equals("constructor"))) {
                isFirstRoutine = false;
                writer.write("<subroutineDec>\n");
                hasSubRoutines = true;
            }
            if (tokenizer.keyWord().equals("function") || tokenizer.keyWord().equals("method") || tokenizer.keyWord().equals("constructor")) {
                hasSubRoutines = true;
                writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>\n");
                tokenizer.advance();
            }
            if (tokenizer.tokenType().equals("IDENTIFIER")) {
                writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
                tokenizer.advance();
            } else if (tokenizer.tokenType().equals("KEYWORD")) {
                writer.write("<keyword> " + tokenizer.keyWord() + "</keyword>\n");
                tokenizer.advance();
            }
            if (tokenizer.tokenType().equals("IDENTIFIER")) {
                writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
                tokenizer.advance();
            }
            if (tokenizer.symbol() == '(') {
                writer.write("<symbol> ( </symbol>\n");
                writer.write("<parameterList>\n");
                compileParameterList();
                writer.write("</parameterList>\n");
                writer.write("<symbol> ) </symbol>\n");
            }
            tokenizer.advance();
            if (tokenizer.symbol() == '{') {
                writer.write("<subroutineBody>\n");
                writer.write("<symbol> { </symbol>\n");
                tokenizer.advance();
            }
            while (tokenizer.keyWord().equals("var") && tokenizer.tokenType().equals("KEYWORD")) {
                writer.write("<varDec>\n ");
                tokenizer.decrementPointer();
                compileVarDec();
                writer.write(" </varDec>\n");
            }
            writer.write("<statements>\n");
            compileStatements();
            writer.write("</statements>\n");
            writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
            if (hasSubRoutines) {
                writer.write("</subroutineBody>\n");
                writer.write("</subroutineDec>\n");
                isFirstRoutine = true;
            }
            compileSubRoutine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileParameterList() {
        tokenizer.advance();
        try {
            while (!(tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ')')) {
                if (tokenizer.tokenType().equals("IDENTIFIER")) {
                    writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
                    tokenizer.advance();
                } else if (tokenizer.tokenType().equals("KEYWORD")) {
                    writer.write("<keyword> " + tokenizer.keyWord() + "</keyword>\n");
                    tokenizer.advance();
                } else if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ',') {
                    writer.write("<symbol> , </symbol>\n");
                    tokenizer.advance();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileVarDec() {
        tokenizer.advance();
        try {
            if (tokenizer.keyWord().equals("var") && tokenizer.tokenType().equals("KEYWORD")) {
                writer.write("<keyword> var </keyword>\n");
                tokenizer.advance();
            }
            if (tokenizer.tokenType().equals("IDENTIFIER")) {
                writer.write("<identifier> " + tokenizer.identifier() + "</identifier>\n");
                tokenizer.advance();
            } else if (tokenizer.tokenType().equals("KEYWORD")) {
                writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>\n");
                tokenizer.advance();
            }
            if (tokenizer.tokenType().equals("IDENTIFIER")) {
                writer.write("<identifier> " + tokenizer.identifier() + "</identifier>\n");
                tokenizer.advance();
            }
            while (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ',') {
                writer.write("<symbol> , </symbol>\n");
                tokenizer.advance();
                writer.write("<identifier> " + tokenizer.identifier() + "</identifier>\n");
                tokenizer.advance();
            }
            if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ';') {
                writer.write("<symbol> ; </symbol>\n");
                tokenizer.advance();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileStatements() {
        try {
            if (tokenizer.symbol() == '}' && tokenizer.tokenType().equals("SYMBOL")) {
                return;
            } else if (tokenizer.keyWord().equals("do") && tokenizer.tokenType().equals("KEYWORD")) {
                writer.write("<doStatement>\n ");
                compileDo();
                writer.write(" </doStatement>\n");
            } else if (tokenizer.keyWord().equals("let") && tokenizer.tokenType().equals("KEYWORD")) {
                writer.write("<letStatement>\n ");
                compileLet();
                writer.write(" </letStatement>\n");
            } else if (tokenizer.keyWord().equals("if") && tokenizer.tokenType().equals("KEYWORD")) {
                writer.write("<ifStatement>\n ");
                compileIf();
                writer.write(" </ifStatement>\n");
            } else if (tokenizer.keyWord().equals("while") && tokenizer.tokenType().equals("KEYWORD")) {
                writer.write("<whileStatement>\n ");
                compileWhile();
                writer.write(" </whileStatement>\n");
            } else if (tokenizer.keyWord().equals("return") && tokenizer.tokenType().equals("KEYWORD")) {
                writer.write("<returnStatement>\n ");
                compileReturn();
                writer.write(" </returnStatement>\n");
            }
            tokenizer.advance();
            compileStatements();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileDo() {
        try {
            if (tokenizer.keyWord().equals("do")) {
                writer.write("<keyword> do </keyword>\n");
            }
            compileCall();
            tokenizer.advance();
            writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compileCall() {
        tokenizer.advance();
        try {
            writer.write("<identifier> " + tokenizer.identifier() + "</identifier>\n");
            tokenizer.advance();
            if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == '.') {
                writer.write("<symbol> " + tokenizer.symbol() + "</symbol>\n");
                tokenizer.advance();
                writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
                tokenizer.advance();
                writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                writer.write("<expressionList>\n");
                compileExpressionList();
                writer.write("</expressionList>\n");
                tokenizer.advance();
                writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
            } else if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == '(') {
                writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                writer.write("<expressionList>\n");
                compileExpressionList();
                writer.write("</expressionList>\n");
                tokenizer.advance();
                writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileLet() {
        try {
            writer.write("<keyword>" + tokenizer.keyWord() + "</keyword>\n");
            tokenizer.advance();
            writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenizer.advance();
            if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == '[') {
                writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                compileExpression();
                tokenizer.advance();
                if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ']') {
                    writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                }
                tokenizer.advance();
            }
            writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
            compileExpression();
            writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
            tokenizer.advance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileWhile() {
        try {
            writer.write("<keyword>" + tokenizer.keyWord() + "</keyword>\n");
            tokenizer.advance();
            writer.write("<symbol>" + tokenizer.symbol() + "</symbol>\n");
            compileExpression();
            tokenizer.advance();
            writer.write("<symbol>" + tokenizer.symbol() + "</symbol>\n");
            tokenizer.advance();
            writer.write("<symbol>" + tokenizer.symbol() + "</symbol>\n");
            writer.write("<statements>\n");
            compileStatements();
            writer.write("</statements>\n");
            writer.write("<symbol>" + tokenizer.symbol() + "</symbol>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileReturn() {
        try {
            writer.write("<keyword> return </keyword>\n");
            tokenizer.advance();
            if (!(tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ';')) {
                tokenizer.decrementPointer();
                compileExpression();
            }
            if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ';') {
                writer.write("<symbol> ; </symbol>\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileIf() {
        try {
            writer.write("<keyword> if </keyword>\n");
            tokenizer.advance();
            writer.write("<symbol> ( </symbol>\n");
            compileExpression();
            writer.write("<symbol> ) </symbol>\n");
            tokenizer.advance();
            writer.write("<symbol> { </symbol>\n");
            tokenizer.advance();
            writer.write("<statements>\n");
            compileStatements();
            writer.write("</statements>\n");
            writer.write("<symbol> } </symbol>\n");
            tokenizer.advance();
            if (tokenizer.tokenType().equals("KEYWORD") && tokenizer.keyWord().equals("else")) {
                writer.write("<keyword> else </keyword>\n");
                tokenizer.advance();
                writer.write("<symbol> { </symbol>\n");
                tokenizer.advance();
                writer.write("<statements>\n");
                compileStatements();
                writer.write("</statements>\n");
                writer.write("<symbol> } </symbol>\n");
            } else {
                tokenizer.decrementPointer();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileExpression() {
        try {
            writer.write("<expression>\n");
            compileTerm();
            while (true) {
                tokenizer.advance();
                if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.isOperation()) {
                    if (tokenizer.symbol() == '<') {
                        writer.write("<symbol> &lt; </symbol>\n");
                    } else if (tokenizer.symbol() == '>') {
                        writer.write("<symbol> &gt; </symbol>\n");
                    } else if (tokenizer.symbol() == '&') {
                        writer.write("<symbol> &amp; </symbol>\n");
                    } else {
                        writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                    }
                    compileTerm();
                } else {
                    tokenizer.decrementPointer();
                    break;
                }
            }
            writer.write("</expression>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileTerm() {
        try {
            writer.write("<term>\n");
            tokenizer.advance();
            if (tokenizer.tokenType().equals("IDENTIFIER")) {
                String prevIdentifier = tokenizer.identifier();
                tokenizer.advance();
                if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == '[') {
                    writer.write("<identifier> " + prevIdentifier + " </identifier>\n");
                    writer.write("<symbol> [ </symbol>\n");
                    compileExpression();
                    tokenizer.advance();
                    writer.write("<symbol> ] </symbol>\n");
                } else if (tokenizer.tokenType().equals("SYMBOL") && (tokenizer.symbol() == '(' || tokenizer.symbol() == '.')) {
                    tokenizer.decrementPointer();
                    tokenizer.decrementPointer();
                    compileCall();
                } else {
                    writer.write("<identifier> " + prevIdentifier + " </identifier>\n");
                    tokenizer.decrementPointer();
                }
            } else {
                if (tokenizer.tokenType().equals("INT_CONST")) {
                    writer.write("<integerConstant> " + tokenizer.intVal() + " </integerConstant>\n");
                } else if (tokenizer.tokenType().equals("STRING_CONST")) {
                    writer.write("<stringConstant> " + tokenizer.stringVal() + " </stringConstant>\n");
                } else if (tokenizer.tokenType().equals("KEYWORD") && (tokenizer.keyWord().equals("this") || tokenizer.keyWord().equals("null")
                        || tokenizer.keyWord().equals("false") || tokenizer.keyWord().equals("true"))) {
                    writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>\n");
                } else if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == '(') {
                    writer.write("<symbol>" + tokenizer.symbol() + "</symbol>\n");
                    compileExpression();
                    tokenizer.advance();
                    writer.write("<symbol> " + tokenizer.symbol() + "</symbol>\n");
                } else if (tokenizer.tokenType().equals("SYMBOL") && (tokenizer.symbol() == '-' || tokenizer.symbol() == '~')) {
                    writer.write("<symbol> " + tokenizer.symbol() + "</symbol>\n");
                    compileTerm();
                }
            }
            writer.write("</term>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileExpressionList() {
        tokenizer.advance();
        if (tokenizer.symbol() == ')' && tokenizer.tokenType().equals("SYMBOL")) {
            tokenizer.decrementPointer();
        } else {
            tokenizer.decrementPointer();
            compileExpression();
        }
        while (true) {
            tokenizer.advance();
            if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ',') {
                try {
                    writer.write("<symbol> , </symbol>\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                compileExpression();
            } else {
                tokenizer.decrementPointer();
                break;
            }
        }
    }
}