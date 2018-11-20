/**
 * Attack, don't attack... leave the choice to a higher power.
 * 
 * @author bdahl
 */

package run.mycode.untiednations.delegates.example;

import java.util.List;
import run.mycode.untiednations.delegates.Correspondence;
import run.mycode.untiednations.delegates.Delegate;

public class Providence implements Delegate{
    private int index;
    
    @Override
    public String getCountryName() {
        return "Providence";
    }
    
    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean goToWar(int otherCountry) {
        // Attack, don't attack... leave the choice to a higher power.
        return Math.random() >= 0.5;
    }
    
    @Override
    public void doBattle(int otherCountry, boolean warDeclared) {
        return; // Providence doesn't record other's actions
    }

    @Override
    public void reportCurrentWealth(double[] resourceValue) {
        return; // Providence fights not for money but for glory
    }
    
    @Override
    public List<Correspondence> getMessages() {
        return null; // Providence doesn't communicate
    }

    @Override
    public void deliverMessage(Correspondence msg) {
        return; // Providence doesn't communicate
    }
}
