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
    private final String[] countryNames;
    private final List<double[]> wealthHistory;
    private final List<boolean[][]> battleHistory;
    
    private int round;

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
        this.countryNames = new String[membershipRoll.size()];
        this.round = 0;
        
        // Perform a roll call to get the country names
        for (int i = 0; i < delegates.length; i++) {
            countryNames[i] = delegates[i].getCountryName();
        }

        // Tally up the initial wealth of each country
        double initialWealth = 0;
        double[] wealth = new double[membershipRoll.size()];
        for (int i = 0; i < delegates.length; i++) {
            wealth[i] = initialWealth;
        }
        wealthHistory.add(wealth);
    }
    
    public void advanceCompetition(int numRounds) {
        for (int i = 0; i < numRounds; i++) {
            round++;
            
            System.out.println("ROUND " + round + ":");
            
            doRound();
            
            boolean[][] battles = battleHistory.get(battleHistory.size() - 1);
            double[] wealth = wealthHistory.get(wealthHistory.size() - 1);
            
            for (int j = 0; j < delegates.length; j++) {
                System.out.print("  " + countryNames[j] + " attacks:");
                int count = 0;
                
                for (int k = 0; k < delegates.length; k++) {
                    if (battles[j][k]) {
                        count++;
                        System.out.print(" " + countryNames[k]);
                    }
                }
                if (count == 0) {
                    System.out.print(" no one");
                }
                System.out.println(" -> Resulting wealth: " + wealth[j]);
            }
        }
    }

    /**
     * Run one round of the competition
     */
    public void doRound() {
        double[] wealth = wealthHistory.get(wealthHistory.size() - 1);

        boolean[][] battleRecord = new boolean[delegates.length][delegates.length];
        
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
        
        // Record each country's hostility towards all other countries
        for (int i = 0; i < delegates.length; i++) {
            for (int j = 0; j < countryNames.length; j++) {
                if (i != j) {
                    battleRecord[i][j] = delegates[i].goToWar(j);
                }
            }
        }
        
        double[] newWealth = wealth.clone();
        
        // Report each country's attacks and tally the results
        for (int i = 0; i < countryNames.length; i++) {
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
        
        // Deliver any correspondence
        for (Correspondence msg : msgs) {
            delegates[msg.getTo()].deliverMessage(msg);
        }
        
        // Save the record of battles and global wealth
        battleHistory.add(battleRecord);
        wealthHistory.add(newWealth);
    }
}
