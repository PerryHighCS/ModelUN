/**
 * A manager class to manage and run a competition between many countries
 *
 * @author bdahl
 */
package run.mycode.untiednations.competition.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import run.mycode.untiednations.delegates.Correspondence;
import run.mycode.untiednations.delegates.Delegate;

public class Competition {

    private final Delegate[] delegates;
    private final List<double[]> wealthHistory;
    private final List<boolean[][]> battleHistory;
    private final List<List<GameEvent>> eventHistory;
    
    private int lastRound;

    /**
     * Enroll a set of countries into a competition
     *
     * @param membershipRoll the set of delegates to add to the ModelUN
     * Competition
     */
    public Competition(Collection<Delegate> membershipRoll) {
        this.delegates = membershipRoll.toArray(new Delegate[membershipRoll.size()]);
        this.wealthHistory = new ArrayList<>();
        this.battleHistory = new ArrayList<>();
        this.eventHistory = new ArrayList<>();
        
        this.lastRound = 0;
        
        // Perform a roll call to get the country names and report the 
        // delegates' indices
        for (int i = 0; i < delegates.length; i++) {
            delegates[i].setIndex(i);
        }

        // Tally up the initial wealth of each country
        double initialWealth = 0;
        double[] wealth = new double[membershipRoll.size()];
        for (int i = 0; i < delegates.length; i++) {
            wealth[i] = initialWealth;
        }
        wealthHistory.add(wealth);
    }
    
    /**
     * Perform a given number of rounds of competition
     * 
     * @param numRounds the number of rounds to perform
     */
    public void advanceCompetition(int numRounds) {
        for (int i = 0; i < numRounds; i++) {            
            lastRound++;                        
            doRound();
        }
    }
    
    /**
     * Perform a given number of rounds of competition
     * 
     * @param round the round number to advance competition to
     */
    public void advanceCompetitionTo(int round) {
        for (int i = lastRound; i <= round; i++) {            
            lastRound++;                        
            doRound();
        }
    }

    /**
     * Run one lastRound of the competition
     */
    public void doRound() {
        // Get the current wealth of all nations
        double[] wealth = wealthHistory.get(wealthHistory.size() - 1);
        
        // Create an empty list to hold the correspondence
        List<Correspondence> msgs = new ArrayList<>();

        for (Delegate d : delegates) {
            // Report the current wealth of each country to each other country.
            // Clone to prevent modification by the member countries.
            d.reportCurrentWealth(wealth.clone());
            List<Correspondence> newMsgs = d.getMessages();
            
            // Receive any communication from the delegate
            if (newMsgs != null && newMsgs.size() > 0) {
                msgs.addAll(newMsgs);
            }
        }
        
        // Poll for battle plans
        boolean[][] battleRecord = warPoll();
        
        // Report battles and distribute wealth based on the battles
        double[] newWealth = aftermath(wealth, battleRecord);
        
        // Create a register of events for this year
        List<GameEvent> events = recordEvents(battleRecord);
        
        // Deliver any correspondence
        msgs.forEach((msg) -> {
            delegates[msg.getTo()].deliverMessage(msg);
            events.add(new GameEvent(delegates[msg.getFrom()],
                                     delegates[msg.getTo()],
                                     GameEvent.Action.MESSAGED));
        });
        
        // Save the record of battles and global wealth
        battleHistory.add(battleRecord);
        wealthHistory.add(newWealth);
        eventHistory.add(events);
    }
    
    /**
     * Get the events for a particular lastRound
     * 
     * @param round the round number (0 == beginning)
     * 
     * @return the list of events for the round. If round is &lt; 0 or
     *              hasn't been run yet, null is returned.
     */
    public List<GameEvent> getEvents(int round) {
        if (round >= 0 && round < eventHistory.size()) {
            return eventHistory.get(round);
        }
        else {
            return null;
        }        
    }
    
    /**
     * Get the resulting wealth for a particular round
     * 
     * @param round the round number (0 == beginning)
     * @return the list of events for the round. If round is &lt; 0 or
     *              hasn't been run yet, null is returned.
     */
    public double[] getWealth(int round) {
        if (round >= 0 && round < wealthHistory.size()) {
            return wealthHistory.get(round);
        }
        else {
            return null;
        } 
    }
    
    private boolean[][] warPoll() {
        boolean[][] battleRecord = new boolean[delegates.length][delegates.length];
        
        // Record each country's hostility towards all other countries
        for (int i = 0; i < delegates.length; i++) {
            for (int j = 0; j < delegates.length; j++) {
                if (i != j) {
                    battleRecord[i][j] = delegates[i].goToWar(j);
                }
            }
        }
        
        return battleRecord;
    }
    
    private double[] aftermath(double[] oldWealth, boolean[][] battleRecord) {
        double[] newWealth = oldWealth.clone();
        
        // Report each country's attacks and tally the results
        for (int i = 0; i < delegates.length; i++) {
            for (int j = 0; j < i; j++) {
                // Determine who attacked who
                boolean iAttacksj = battleRecord[i][j];
                boolean jAttacksi = battleRecord[j][i];
                
                // Report the battles
                delegates[j].doBattle(i, iAttacksj);
                delegates[i].doBattle(j, jAttacksi);
                
                // If both countries attack eachother, they each squandered 1 resource
                if (iAttacksj && jAttacksi) {
                    newWealth[i] += 1;
                    newWealth[j] += 1;
                }
                // If i attacked j, then i loses 1 resource but gains j's 2 resources
                else if (iAttacksj) {
                    newWealth[i] += 3;
                }
                // If j attacked i, then j loses 1 resource but gains i's 2 resources
                else if (jAttacksi) {
                    newWealth[j] += 3;
                }
                // If noone attacks, they both get 2 resources
                else {
                    newWealth[i] += 2;
                    newWealth[j] += 2;
                }
            }
        }
        
        return newWealth;
    }
    
    private List<GameEvent> recordEvents(boolean[][] battles) {
        List<GameEvent> events = new ArrayList<>();
        
        for (int i = 0; i < delegates.length; i++) {
            for (int j = 0; j < i; j++) {
                if (battles[i][j]) {
                    events.add(new GameEvent(delegates[i], delegates[j],
                                             GameEvent.Action.ATTACK));
                }
                else {
                    events.add(new GameEvent(delegates[i], delegates[j],
                                             GameEvent.Action.IGNORE));
                }
            }
        }
        
        return events;
    }
}
