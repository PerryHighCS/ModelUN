package run.mycode.untiednations.competition.ui;

import java.util.List;
import run.mycode.compiler.ReportItem;

/**
 *
 * @author dahlem.brian
 */
public class DelegateErrorReport {
    private final String name;
    private final List<ReportItem> errors;
    
    public DelegateErrorReport(String delegateName, List<ReportItem> report) {
        this.name = delegateName;
        this.errors = report;
    }

    public String getDelegateName() {
        return name;
    }

    public List<ReportItem> getReport() {
        return errors;
    }
}
