package run.mycode.untiednations.competition.model;

import run.mycode.untiednations.delegates.Delegate;

public class GameEvent {
    enum Type {
        ATTACK, IGNORE
    }
    
    public GameEvent(Delegate source, Delegate target, Type type) {
        
    }
    
    @Override
    public String toString() {
        return "Event";
    }
    
}
