package run.mycode.untiednations.delegates;

import java.util.List;
import run.mycode.untiednations.delegates.Correspondence;
import run.mycode.untiednations.delegates.Delegate;

/**
 * An exception safe wrapper around the Delegate Class
 * 
 * @author dahlem.brian
 */
public final class DelegateWrapper implements Delegate {
    Delegate delegate;
    
    public DelegateWrapper(Delegate d) {
        this.delegate = d;
    }
    
    @Override
    public String getCountryName() {
        try {
            return delegate.getCountryName();
        }
        catch (Exception e) {
            System.out.println(delegate.getClass().getName() + " failed in getCountryName");
            e.printStackTrace();
            return delegate.getClass().getSimpleName();
        }
    }

    @Override
    public void setIndex(int index) {
        try {
            delegate.setIndex(index);
        }
        catch (Exception e) {
            System.out.println(delegate.getClass().getName() + " failed in setIndex");
            e.printStackTrace();
        }
    }

    @Override
    public void reportCurrentWealth(double[] resourceValue) {
        try {
            delegate.reportCurrentWealth(resourceValue);
        }
        catch (Exception e) {
            System.out.println(delegate.getClass().getName() + " failed in reportCurrentWealth");
            e.printStackTrace();
        }
    }

    @Override
    public boolean goToWar(int otherCountry) {
        try {
            return delegate.goToWar(otherCountry);
        }
        catch (Exception e) {
            System.out.println(delegate.getClass().getName() + " failed in goToWar");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void doBattle(int otherCountry, boolean warDeclared) {
        try {
            delegate.doBattle(otherCountry, warDeclared);
        }
        catch (Exception e) {
            System.out.println(delegate.getClass().getName() + " failed in doBattle");
            e.printStackTrace();
        }
    }

    @Override
    public List<Correspondence> getMessages() {
        try {
            return delegate.getMessages();
        }
        catch (Exception e) {
            System.out.println(delegate.getClass().getName() + " failed in getMessages");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverMessage(Correspondence msg) {
        try {
            delegate.deliverMessage(msg);
        }
        catch (Exception e) {
            System.out.println(delegate.getClass().getName() + " failed in deliverMessage");
            e.printStackTrace();
        }
    }
    
}
