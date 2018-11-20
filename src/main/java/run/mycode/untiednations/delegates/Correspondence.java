/**
 * A secret message from one diplomat to another
 * 
 * @author bdahl
 */
package run.mycode.untiednations.delegates;

public class Correspondence {
    private final int from;
    private final int to;
    private final String msg;
    
    public Correspondence(int from, int to, String message) {
        this.from = from;
        this.to = to;
        this.msg = message;
    }
    
    public int getFrom() {
        return from;
    }
    
    public int getTo() {
        return to;
    }
    
    public String getMessage() {
        return msg;
    }
}
