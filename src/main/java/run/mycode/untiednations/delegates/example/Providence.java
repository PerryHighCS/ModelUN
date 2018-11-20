/**
 * Attack, don't attack... leave the choice to a higher power.
 * 
 * @author bdahl
 */

package run.mycode.untiednations.delegates.example;

import run.mycode.untiednations.delegates.Delegate;

public class Providence implements Delegate{

    @Override
    public String getCountryName() {
        return "Providence";
    }

    @Override
    public boolean goToWar(String otherCountry) {
        // Attack, don't attack... leave the choice to a higher power.
        return Math.random() >= 0.5;
    }
    
    @Override
    public void doBattle(String otherCountry, boolean warDeclared) {
        return; // Providence doesn't record other's actions
    }

    @Override
    public void reportCurrentWealth(String[] countries, double[] resourceValue) {
        return; // Providence fights not for money but for glory
    }
}
