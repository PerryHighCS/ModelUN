package run.mycode.compiler;

import java.util.ArrayList;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

public class CompileErrorReporter implements DiagnosticListener<JavaFileObject> {
    private List<ReportItem> report;
    private boolean containsError;
    
    public CompileErrorReporter() {
        report = new ArrayList<>();        
    }
    
    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        ReportItem ri = new ReportItem(diagnostic);
        
        if (ri.isError()) {
            containsError = true;
        }
        report.add(ri);
    }
    
    public List<ReportItem> getReport() {
        return report;
    }
    
    public boolean hasError() {
        return containsError;
    }
    
}
