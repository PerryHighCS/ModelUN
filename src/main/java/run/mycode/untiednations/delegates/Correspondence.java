/**
 * A secret message from one diplomat to another.  The format of the message is
 * unspecified.
 * 
 * @author bdahl
 */
package run.mycode.untiednations.delegates;

public class Correspondence {
    private final int from;
    private final int to;
    private final String msg;
    
    /**
     * Create a message to be passed from one delegate to another
     * @param from
     * @param to
     * @param message 
     */
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
    
    /**
     * Get the message contained in this correspondence.
     * @return A String message. The format of the message is unspecified.
     */
    public String getMessage() {
        return msg;
    }
}
