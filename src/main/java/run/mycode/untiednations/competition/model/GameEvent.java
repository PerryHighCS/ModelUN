package run.mycode.untiednations.competition.model;

import run.mycode.untiednations.delegates.Delegate;

public class GameEvent {
    public static enum Action {
        ATTACK("%s attacked %s"),
        ATTACK_CONT("%s maintains its attack on %s"),
        ATTACK_CEASE("%s declares a cease fire against %s"),
        PEACE("%s and %s declare peace"),
        PEACE_CONT(""),
        WAR("%s and %s go to war"),
        WAR_CONT("%s and %s continue their war"),
        MESSAGED("%s sent a secret message to %s"),
        CIVILWAR("%s erupts in civil war!"),
        CIVILWAR_CONT("%s continues its civil war");
        
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
