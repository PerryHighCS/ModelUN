/**
 * GhandiLand believes in peace.
 * 
 * @author bdahl
 */

package run.mycode.untiednations.delegates.example;

import run.mycode.untiednations.delegates.Delegate;

public class GandiLand implements Delegate {

    @Override
    public String getCountryName() {
        return "GandiLand";
    }
    
    @Override
    public boolean goToWar(String otherCountry) {
        // Mohandas Gandhi believes in peace
        return false;
    }

    @Override
    public void doBattle(String otherCountry, boolean warDeclared) {
        return; // Gandi doesn't record other's actions
    }

    @Override
    public void reportCurrentWealth(String[] countries, double[] resourceValue) {
        return; // Gandi doesn't care about money
    }
}
