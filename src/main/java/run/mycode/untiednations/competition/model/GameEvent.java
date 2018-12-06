package run.mycode.untiednations.competition.model;

import run.mycode.untiednations.delegates.Delegate;

public class GameEvent {
    public static enum Action {
        ATTACK("%s attacked %s."), IGNORE(/*"%s ignored %s."*/""),
        MESSAGED("%s sent a secret message to %s.");
        
        public String text;
        
        Action(String text) {
            this.text = text;
        }
    }
    
    private final Delegate target;
    private final Delegate source;
    private final Action act;
    
    public GameEvent(Delegate source, Delegate target, Action act) {
        this.source = source;
        this.target = target;
        this.act = act;
    }
    
    @Override
    public String toString() {        
        if (target == null) {
            return String.format(act.text, source.getCountryName());
        }
        else {
            return String.format(act.text, source.getCountryName(),
                                 target.getCountryName());
        }
    }    
    
    public Delegate getSource() {
        return source;
    }
    
    public Delegate getTarget() {
        return target;
    }
    
    public Action getAction() {
        return act;
    }
}
