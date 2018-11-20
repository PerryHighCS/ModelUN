/**
 * GhandiLand believes in peace.
 * 
 * @author bdahl
 */

package run.mycode.untiednations.delegates.example;

import java.util.List;
import run.mycode.untiednations.delegates.Correspondence;
import run.mycode.untiednations.delegates.Delegate;

public class GandiLand implements Delegate {
    private int index;
    
    @Override
    public String getCountryName() {
        return "GandiLand";
    }
    
    @Override
    public void setIndex(int index) {
        this.index = index;
    }
    
    @Override
    public boolean goToWar(int otherCountry) {
        // Mohandas Gandhi believes in peace
        return false;
    }

    @Override
    public void doBattle(int otherCountry, boolean warDeclared) {
        return; // Gandi doesn't record other's actions
    }

    @Override
    public void reportCurrentWealth(double[] resourceValue) {
        return; // Gandi doesn't care about money
    }

    @Override
    public List<Correspondence> getMessages() {
        return null; // Gandi doesn't communicate
    }

    @Override
    public void deliverMessage(Correspondence msg) {
        return; // Gandi doesn't communicate
    }
}
