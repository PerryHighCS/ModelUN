/**
 * A manager class to manage and run a competition between many countries
 *
 * @author bdahl
 */
package run.mycode.untiednations.competition.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import run.mycode.untiednations.delegates.Delegate;

public class Competition {

    private final Delegate[] delegates;
    private final String[] countryNames;
    private final List<double[]> wealthHistory;
    private final List<boolean[][]> battleHistory;

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

        // Perform a roll call to get the country names
        for (int i = 0; i < delegates.length; i++) {
            countryNames[i] = delegates[i].getCountryName();
        }

        // Tally up the initial wealth of each country
        double initialWealth = delegates.length * 2;
        double[] wealth = new double[membershipRoll.size()];
        for (int i = 0; i < delegates.length; i++) {
            wealth[i] = initialWealth;
        }
        wealthHistory.add(wealth);
    }

    /**
     * Run one round of the competition
     */
    public void doRound() {
        double[] wealth = wealthHistory.get(wealthHistory.size() - 1);

        boolean[][] battleRecord = new boolean[delegates.length][delegates.length];

        // Report the current wealth of each country to each other country.
        // Clone to prevent modification by the member countries.
        for (Delegate c : delegates) {
            c.reportCurrentWealth(countryNames.clone(), wealth.clone());
        }
    }

}
