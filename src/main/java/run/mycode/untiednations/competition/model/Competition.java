/**
 * A manager class to manage and run a competition between many countries
 *
 * @author bdahl
 */
package run.mycode.untiednations.competition.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import run.mycode.untiednations.delegates.Correspondence;
import run.mycode.untiednations.delegates.Delegate;

public class Competition {

    private final Delegate[] delegates;
    private final List<Map<String, Double>> wealthHistory;
    private final List<List<GameEvent>> eventHistory;
    private final List<boolean[][]> battleHistory;
    private final int MAX_ROUND;
    
    private int lastRound;

    /**
     * Enroll a set of countries into a competition
     *
     * @param membershipRoll the set of delegates to add to the ModelUN
     *                      Competition
     * @param numRounds the number of rounds to run in the competition
     */
    public Competition(Collection<Delegate> membershipRoll, int numRounds) {
        this.MAX_ROUND = numRounds;
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
        double initialWealth = 5;
        Map<String, Double> wealth = new HashMap<>();
        for (Delegate delegate : delegates) {
            wealth.put(delegate.getCountryName(), initialWealth);
        }
        wealthHistory.add(wealth);
        
        // No history for starting year
        battleHistory.add(null);     
        eventHistory.add(new ArrayList<>());
    }
    
    /**
     * Perform a given number of rounds of competition
     * 
     * @param numRounds the number of rounds to perform
     */
    public void advanceCompetition(int numRounds) {
        if (lastRound + numRounds > this.MAX_ROUND) {
            numRounds = this.MAX_ROUND - lastRound;
        }
        for (int i = 0; i < numRounds; i++) {                       
            doRound();            
            lastRound++; 
        }
    }
    
    /**
     * Perform a given number of rounds of competition
     * 
     * @param round the round number to advance competition to
     */
    public void advanceCompetitionTo(int round) {
        if (round > this.MAX_ROUND) {
            round = this.MAX_ROUND;
        }
        for (int i = lastRound+1; i <= round; i++) {                        
            doRound();            
            lastRound++;
        }
    }

    /**
     * Run the next round of the competition
     */
    private void doRound() {
        // Get the current wealth of all nations
        Map<String, Double> wealth = wealthHistory.get(wealthHistory.size() - 1);
        
        // Create an empty list to hold the correspondence
        List<Correspondence> msgs = new ArrayList<>();

        double[] wealthArr = new double[delegates.length];
        
        for (int i = 0; i < delegates.length; i++) {
            wealthArr[i] = wealth.get(delegates[i].getCountryName());
        }
        
        for (Delegate d : delegates) {
            // Report the current wealth of each country to each other country.
            // Clone to prevent modification by the member countries.
            d.reportCurrentWealth(wealthArr.clone());
            List<Correspondence> newMsgs = d.getMessages();
            
            // Receive any communication from the delegate
            if (newMsgs != null && newMsgs.size() > 0) {
                msgs.addAll(newMsgs);
            }
        }
        
        // Poll for battle plans
        boolean[][] battleRecord = warPoll();
        
        // Report battles and distribute wealth based on the battles
        wealthHistory.add(aftermath(wealth, battleRecord));
        
        // Create a register of events for this year
        List<GameEvent> events = recordEvents(battleRecord);
        
        // Deliver any correspondence
        msgs.forEach((msg) -> {
            delegates[msg.getTo()].deliverMessage(msg);
            events.add(new GameEvent(delegates[msg.getFrom()],
                                     delegates[msg.getTo()],
                                     GameEvent.Action.MESSAGED));
        });
        
        // Save the record of battles
        battleHistory.add(battleRecord);
        eventHistory.add(events);
    }
    
    /**
     * Get the events for a particular round
     * 
     * @param round the round number (0 == beginning)
     * 
     * @return the list of events for the round. If round is &lt; 0 or
     *              hasn't been run yet, null is returned.
     */
    public List<GameEvent> getEvents(int round) {
        if (round > 0 && round < eventHistory.size()) {
            return eventHistory.get(round);
        }
        else if (round == 0) {
            // Show the establishment event
            List<GameEvent> est = new ArrayList<>();
            est.add(new GameEvent(null, null, GameEvent.Action.START));
            return est;
        }
        else if (round >= this.MAX_ROUND) {
            // Show the establishment event
            List<GameEvent> fin = new ArrayList<>();
            fin.add(new GameEvent(null, null, GameEvent.Action.FINISH));
            return fin;
        }
        else {
            return null;
        }        
    }
        
    /**
     * Get the resulting wealth for a particular country in a given round
     * 
     * @param name the name of the country
     * @param round the round number (0 == beginning)
     * @return the resources for the country at the end of the round.
     *         If round is &lt; 0 or hasn't been run yet, null is returned.
     */
    public Double getWealth(String name, int round) {
        if (round < this.MAX_ROUND) {
            return wealthHistory.get(round).get(name);
        }
        else {
            return wealthHistory.get(this.MAX_ROUND).get(name);
        }
    }
    
    private boolean[][] warPoll() {
        boolean[][] battleRecord = new boolean[delegates.length][delegates.length];
        
        // Record each country's hostility towards all other countries
        for (int i = 0; i < delegates.length; i++) {
            for (int j = 0; j < delegates.length; j++) {
                battleRecord[i][j] = delegates[i].goToWar(j);
            }
        }
        
        return battleRecord;
    }
    
    private Map<String, Double> aftermath(Map<String, Double> oldWealth, boolean[][] battleRecord) {
        Map<String, Double> newWealth = new HashMap<>(oldWealth);
        
        // Report each country's attacks and tally the results
        for (int i = 0; i < delegates.length; i++) {
            for (int j = 0; j < i; j++) {
                // Determine who attacked who
                boolean iAttacksj = battleRecord[i][j];
                boolean jAttacksi = battleRecord[j][i];
                
                // Report the battles
                delegates[j].doBattle(i, iAttacksj);
                delegates[i].doBattle(j, jAttacksi);
                
                String iName = delegates[i].getCountryName();
                String jName = delegates[j].getCountryName();
                double iWealth = oldWealth.get(iName);
                double jWealth = oldWealth.get(jName);
                
                // If both countries attack eachother, they each squandered 1 resource
                if (iAttacksj && jAttacksi && j != i) {
                    newWealth.put(iName, iWealth + 1);
                    newWealth.put(jName, jWealth + 1);
                }              
                // If i attacked j, then i loses 1 resource but gains j's 2 resources
                else if (iAttacksj) {
                    newWealth.put(iName, iWealth + 3);
                }
                // If j attacked i, then j loses 1 resource but gains i's 2 resources
                else if (jAttacksi) {
                    newWealth.put(jName, jWealth + 3);
                }
                // If noone attacks, they both get 2 resources
                else {
                    newWealth.put(iName, iWealth + 2);
                    newWealth.put(jName, jWealth + 2);
                }
            }
        }
        
        // Check for civil war
        for (int i = 0; i < delegates.length; i++) {
            boolean iAttacksi = battleRecord[i][i];
            
            // If there is a civil war, all resources are lost
            if (iAttacksi) {
                delegates[i].doBattle(i, iAttacksi);    // report the battle
                newWealth.put(delegates[i].getCountryName(), 0d);
            }
            
        }
        
        return newWealth;
    }
    
    private List<GameEvent> recordEvents(boolean[][] battles) {
        List<GameEvent> events = new ArrayList<>();
        
        boolean[][] lastYear = battleHistory.get(lastRound); // Get the battles from the previous year
        
        for (int i = 0; i < delegates.length; i++) {
            for (int j = 0; j < delegates.length; j++) {
                GameEvent event;
                
                if (lastRound > 0) {
                    event = GameEvent.eventFor(delegates[i], delegates[j],
                                               battles[i][j], battles[j][i],
                                               lastYear[i][j], lastYear[j][i],
                                               i < j);
                }
                else {
                    event = GameEvent.eventFor(delegates[i], delegates[j],
                                               battles[i][j], battles[j][i],
                                               false, false, i < j);
                }
                
                if (event != null) {
                    events.add(event);
                }
            }
        }
        
        return events;
    }
}
