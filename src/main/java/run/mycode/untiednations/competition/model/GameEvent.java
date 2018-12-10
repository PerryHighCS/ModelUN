package run.mycode.untiednations.competition.model;

import run.mycode.untiednations.delegates.Delegate;

public class GameEvent {
    public static enum Action {
        START("The Untied Nations is established.", Attack.NONE),
        FINISH("The Untied Nations is disbanded.", Attack.NONE),
        ATTACK("%s attacked %s", Attack.ONEWAY),
        ATTACK_CONT("%s maintains its attack on %s", Attack.ONEWAY),
        ATTACK_CEASE("%s declares a ceasefire in its conflict with %s", Attack.NONE),
        PEACE("%s and %s declare peace", Attack.NONE),
        PEACE_CONT("", Attack.NONE),
        WAR("%s and %s go to war", Attack.BOTH),
        WAR_CONT("%s and %s continue their war", Attack.BOTH),
        MESSAGED("%s sent a secret message to %s", Attack.NONE),
        CIVILWAR("%s erupts in civil war!", Attack.SELF),
        CIVILWAR_CONT("%s continues its civil war", Attack.SELF),
        CIVILWAR_CEASE("Civil war in %s ends", Attack.NONE);
        
        public String text;
        public Attack attackDir;
        
        Action(String text, Attack dir) {
            this.text = text;
            this.attackDir = dir;
        }
    }
    
    public static enum Attack {
        NONE, ONEWAY, BOTH, SELF
    }
    
    private final Delegate target;
    private final Delegate source;
    private final Action act;
    
    public static GameEvent eventFor(Delegate a, Delegate b, 
                                     boolean aVb, boolean bVa,
                                     boolean ly_aVb, boolean ly_bVa,
                                     boolean addTwoSided) {       
        
        // If country S attacked
        if (aVb) {
            // If country A has attacked itself
            if (a == b) {
                // and there was a civil war last year
                if (ly_aVb) {
                    // Return a continuing civil war event
                    return new GameEvent(a, null, GameEvent.Action.CIVILWAR_CONT);
                }
                else {
                    // otherwise return a new civil war event
                    return new GameEvent(a, null, GameEvent.Action.CIVILWAR);
                }
            }
            // If both countries attacked eachother, and two sided attacks should be added
            else if (bVa && addTwoSided) {
                // If they both attacked last year
                if (ly_aVb && ly_bVa) {
                    // Report a continuing war
                    return new GameEvent(a, b, GameEvent.Action.WAR_CONT);
                }
                else {
                    // Otherwise report a war breaking out
                    return new GameEvent(a, b, GameEvent.Action.WAR);
                }
            }
            // If country B did not attack
            else if (!bVa) {
                // If country A attacked last year
                if (ly_aVb) {
                    // Report the continuing attack
                    return new GameEvent(a, b, GameEvent.Action.ATTACK_CONT);
                }
                else {
                    // Otherwise report a new attack
                    return new GameEvent(a, b, GameEvent.Action.ATTACK);
                }
            }
        }
        // If country A did not attack
        else {
            // If the country had a civil war last year 
            if (a == b && ly_aVb) {
                // Report an end to the civil war
                return new GameEvent(a, b, GameEvent.Action.CIVILWAR_CEASE);
            }
            
            // If country A attacked B last year
            if (ly_aVb) {
                // If there was a war last year, but not this year
                if (ly_bVa && !bVa) {
                    // The countries declared peace
                    return new GameEvent(a, b, GameEvent.Action.PEACE);
                }
                // Otherwise, if the battle was one-sided last year
                else {
                    // Declare a cease fire
                    return new GameEvent(a, b, GameEvent.Action.ATTACK_CEASE);
                }
            }
        }
        
        return null;
    }
    
    public GameEvent(Delegate source, Delegate target, Action act) {
        this.source = source;
        this.target = target;
        this.act = act;
    }
    
    @Override
    public String toString() {
        if (source == null) {
            return act.text;
        }
        else if (target == null) {
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
