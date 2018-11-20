/**
 * GenghisStan always declares war.
 * 
 * @author bdahl
 */
package run.mycode.untiednations.delegates.example;

import java.util.List;
import run.mycode.untiednations.delegates.Correspondence;
import run.mycode.untiednations.delegates.Delegate;

public class GenghisStan implements Delegate {
    
    @Override
    public String getCountryName() {
        return "GenghisStan";
    }

    @Override
    public boolean goToWar(String otherCountry) {
        // Genghis Kahn always declares war.
        return true;
    }
    
    @Override
    public void doBattle(String otherCountry, boolean warDeclared) {
        return; // Genghis doesn't record other's actions
    }

    @Override
    public void reportCurrentWealth(String[] countries, double[] resourceValue) {
        return; // Genghis doesn't care about money, only land
    }
    @Override
    public List<Correspondence> getMessages() {
        return null; // Genghis doesn't communicate
    }

    @Override
    public void deliverMessage(Correspondence msg) {
        return; // Genghis doesn't communicate
    }
}
