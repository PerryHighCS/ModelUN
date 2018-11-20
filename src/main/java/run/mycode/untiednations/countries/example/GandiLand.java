/**
 * GhandiLand believes in peace.
 * 
 * @author bdahl
 */

package run.mycode.untiednations.countries.example;

import run.mycode.untiednations.countries.Delegate;

public class GandiLand implements Delegate {

    @Override
    public String getCountryName() {
        return "GandiLand";
    }
    
    @Override
    public boolean goToWar(String otherCountry, double otherWealth) {
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
