/**
 * The diplomatic interface for a country.
 * 
 * A country in the Untied Nations General Assembly needs to be able perform
 * three operations. The country must be able to declare its name, declare war,
 * and receive other countries declarations of war.
 * 
 * Each meeting of the General Assembly starts with a roll call. The delegates
 * will each declare the name of the country they represent. After the roll 
 * call, the current wealth of each country will be tallied and reported to the
 * delegates. Next, a silent, binding poll will be taken in which each country
 * will record their wish to go to war with each other country. Once the poll is
 * taken, wars will be announced. The meeting will then end.
 * 
 * After all wars are fought, the resource distribution will be tallied and
 * another meeting will be called.
 * 
 * @author bdahl
 */

package run.mycode.untiednations.countries;

public interface Delegate {

    /**
     * Get the name of this country
     *   (all instances should return the same name)
     * 
     * @return The name of this country 
     */
    public String getCountryName();
    
    /**
     * State the current value of this country's resources
     * 
     * @param countries The names of each of the countries
     * @param resourceValue The current total value of each country's resources
     */
    public void reportCurrentWealth(String[] countries, double[]resourceValue);
    
    /**
     * State whether this country will go to war with a given other country in 
     * this round
     * 
     * @param otherCountry The name of the country to challenge
     * @param otherWealth The other country's current value
     * 
     * @return true if we will go to war with this country, false otherwise
     */
    public boolean goToWar(String otherCountry, double otherWealth);
    
    
    /**
     * Learn whether the other country declared war against this country in this
     * round.
     * 
     * @param otherCountry The name of the country offering a challenge
     * @param warDeclared true if the other country declared war in this round
     */
    public void doBattle(String otherCountry, boolean warDeclared);
}
