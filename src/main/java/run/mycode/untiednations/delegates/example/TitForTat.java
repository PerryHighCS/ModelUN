/**
 * If the other country attacked us last time, we attack them back!
 * 
 * @author bdahl
 */
package run.mycode.untiednations.delegates.example;

import java.util.ArrayList;
import java.util.List;
import run.mycode.untiednations.delegates.Correspondence;
import run.mycode.untiednations.delegates.Delegate;

public class TitForTat implements Delegate {
    private int index;
    private List<Integer> enemies;
    
    public TitForTat() {
        enemies = new ArrayList<>();
    }
    
    @Override
    public void setIndex(int index) {
        this.index = index;
    }
    
    @Override
    public String getCountryName() {
        return "TitForTat";
    }

    @Override
    public boolean goToWar(int otherCountry) {
        // If the other country attacked us last time, we attack them back!
        return (enemies.contains(otherCountry)); 
    }

    @Override
    public void doBattle(int otherCountry, boolean warDeclared) {
        if (!warDeclared) {
            // If they didn't declare war, remove them from our enemies list
            enemies.remove(Integer.valueOf(otherCountry));  // use Integer.valueOf to remove element rather than element at index
        } 
        
        if (warDeclared && !enemies.contains(otherCountry)) {
            // If they declared war and weren't already on our enemies list,
            // add them.
            enemies.add(otherCountry);
        }
    }

    @Override
    public void reportCurrentWealth(double[] resourceValue) {
        return; // TitForTat doesn't care about your money, only your actions
    }
    
    @Override
    public List<Correspondence> getMessages() {
        return null; // TitForTat doesn't communicate
    }

    @Override
    public void deliverMessage(Correspondence msg) {
        return; // TitForTat doesn't communicate
    }
}
