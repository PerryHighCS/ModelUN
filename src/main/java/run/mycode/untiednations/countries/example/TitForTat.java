/**
 * If the other country attacked us last time, we attack them back!
 * 
 * @author bdahl
 */
package run.mycode.untiednations.countries.example;

import java.util.ArrayList;
import java.util.List;
import run.mycode.untiednations.countries.Delegate;

public class TitForTat implements Delegate {
    private List<String> enemies;
    
    public TitForTat() {
        enemies = new ArrayList<>();
    }
    
    @Override
    public String getCountryName() {
        return "TitForTat";
    }

    @Override
    public boolean goToWar(String otherCountry, double otherWealth) {
        // If the other country attacked us last time, we attack them back!
        return (enemies.contains(otherCountry)); 
    }

    @Override
    public void doBattle(String otherCountry, boolean warDeclared) {
        if (!warDeclared) {
            // If they didn't declare war, remove them from our enemies list
            enemies.remove(otherCountry);
        } 
        
        if (warDeclared && !enemies.contains(otherCountry)) {
            // If they declared war and weren't already on our enemies list,
            // add them.
            enemies.add(otherCountry);
        }
    }

    @Override
    public void reportCurrentWealth(String[] countries, double[] resourceValue) {
        return; // TitForTat doesn't care about your money, only your actions
    }
}
