package run.mycode.compiler;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

public class CompileDiagnosticListener implements DiagnosticListener<JavaFileObject> {

    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        /*
        System.out.println("Line Number->" + diagnostic.getLineNumber());
        System.out.println("code->" + diagnostic.getCode());
        System.out.println("Message->" + diagnostic.getMessage(Locale.ENGLISH));
        System.out.println("Source->" + diagnostic.getSource());
        System.out.println(" ");
         */
    }
}
