package run.mycode.compiler;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 *
 * @author dahlem.brian
 */
public class ReportItem {
    
    private final long line;
    private final long column;
    private final String filename;
    private final String message;
    private final Diagnostic.Kind type;
    private final boolean compileError;

    public ReportItem(Diagnostic<? extends JavaFileObject> diagnostic) {
        this.message = diagnostic.getMessage(null);
        this.line = diagnostic.getLineNumber();
        this.column = diagnostic.getColumnNumber();
        this.filename = diagnostic.getSource().getName();
        this.type = diagnostic.getKind();
//        System.out.println(diagnostic.getKind().toString());
//        System.out.println(message);
//        System.out.println("At line " + line + ", position " + column + " in " + filename);
        this.compileError = true;
    }
    
    public ReportItem(String filename, boolean error, String message) {
        this.filename = filename;
        this.line = -1;
        this.column = -1;
        this.message = message;
        
        if (error) {
            type = Diagnostic.Kind.ERROR;
        }
        else {
            type = Diagnostic.Kind.OTHER;
        }
        this.compileError = false;
    }

    /**
     * Determine if this report is classified as an error
     * @return true if this is an error.
     */
    public boolean isError() {
        return type == Diagnostic.Kind.ERROR;
    }

    /**
     * Determine if this report came from the Java Compiler
     * @return true if the compiler generated the message
     */
    public boolean isCompileMessage() {
        return compileError;
    }
    /**
     * Get the line number the error occurred on
     * @return the line number the error occurred on
     */
    public long getLine() {
        return line;
    }

    /**
     * Get the column number in the line the error occurred on
     * @return the column number in the line the error occurred on
     */
    public long getColumn() {
        return column;
    }

    /**
     * Get the name of the file containing the error
     * @return the name of the file containing the error
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Get the error message
     * @return the error message
     */
    public String getMessage() {
        return message;
    }
    
}
