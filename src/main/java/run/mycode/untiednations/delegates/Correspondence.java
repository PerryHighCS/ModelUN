/**
 * A secret message from one diplomat to another
 * 
 * @author bdahl
 */
package run.mycode.untiednations.delegates;

public class Correspondence {
    private final String from;
    private final String to;
    private final String msg;
    
    public Correspondence(String from, String to, String message) {
        this.from = from;
        this.to = to;
        this.msg = message;
    }
    
    public String getFrom() {
        return from;
    }
    
    public String getTo() {
        return to;
    }
    
    public String getMessage() {
        return msg;
    }
}
