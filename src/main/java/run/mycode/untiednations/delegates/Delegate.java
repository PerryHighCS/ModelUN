/**
 * The diplomatic interface for a country in the ModelUN.
 *  
 * @author bdahl
 */

package run.mycode.untiednations.delegates;

import java.util.List;

public interface Delegate {

    /**
     * Get the name of this country
     *   (all instances should return the same name)
     * 
     * @return The name of this country 
     */
    public String getCountryName();
    
    /**
     * Report this Delegate's index in all ModelUN lists
     * @param index this Delegate's ID, and index in the resourceValue list
     */
    public void setIndex(int index);
    
    /**
     * State the current value of this country's resources
     * 
     * @param resourceValue The current total value of each country's resources
     */
    public void reportCurrentWealth(double[] resourceValue);
    
    /**
     * State whether this country will go to war with a given other country in 
     * this round
     * 
     * @param otherCountry The name of the country to challenge
     * 
     * @return true if we will go to war with this country, false otherwise
     */
    public boolean goToWar(int otherCountry);
    
    /**
     * Learn whether the other country declared war against this country in this
     * round.
     * 
     * @param otherCountry The name of the country offering a challenge
     * @param warDeclared true if the other country declared war in this round
     */
    public void doBattle(int otherCountry, boolean warDeclared);
    
    /**
     * Collect the diplomatic messages this delegate would like to send
     * Note: messages are collected immediately after the wealth report is made
     * and will be delivered after battles complete.
     * @return the Messages to be sent during the round
     */
    public List<Correspondence> getMessages();
    
    /**
     * Give the delegate a message from another delegate.
     * 
     * @param msg 
     */
    public void deliverMessage(Correspondence msg);
}
