package classes;


/**
 *  This class is responsible for creating a TransCompany object which is the abbreviation of Transportation Company.
 * Az it's name suggest, this class instantiate an object that holds datas about companies that main business profile
 * is transportation, a focus on transportation via trucks. It is used when user wants to add a new Truck, he/she has
 * to choose a transportation company.
 */
public class TransCompany {

    /**
     * The ID of the transportation company.
     */
    private String TransCompID;

    /**
     * The name of the transportation company.
     */
    private String TransCompName;

    /**
     * The country where the transporttation company's headquarter is.
     */
    private String TransCompNation;

    /**
     * The city where the headquarter is.
     */
    private String TransCompCity;

    /**
     * The address within the city.
     */
    private String TransCompAddress;

    /**
     * The official phone number of the company.
     */
    private String TransCompPhone;

    /**
     * The Postal ZIP Code of the company's headquarter.
     */
    private String TransCompZIP;

    /**
     * The date when the transportation company was registered in the system.
     */
    private String TransCompDate;


    /**
     * The constructor method will instantiate the a TransCompany object which will help the user to add
     * a new truck to the database.
     * @param transCompID The ID of the transportation company
     * @param transCompName The name of the company
     * @param transCompNation The country where the company's headquarter lies
     * @param transCompCity The city where the company's headquarter is
     * @param transCompAddress The address of the headquarter
     * @param transCompPhone The phone number of zhe transportation company
     * @param transCompZIP The Postal Code of the headquarter
     * @param transCompDate The date when the company was added to the system
     */
    public TransCompany(String transCompID,
                        String transCompName,
                        String transCompNation,
                        String transCompCity,
                        String transCompAddress,
                        String transCompPhone,
                        String transCompZIP,
                        String transCompDate) {
        TransCompID = transCompID;
        TransCompName = transCompName;
        TransCompNation = transCompNation;
        TransCompCity = transCompCity;
        TransCompAddress = transCompAddress;
        TransCompPhone = transCompPhone;
        TransCompZIP = transCompZIP;
        TransCompDate = transCompDate;
    }

    public String getTransCompID() {
        return TransCompID;
    }

    public String getTransCompName() {
        return TransCompName;
    }

    public void setTransCompName(String transCompName) {
        TransCompName = transCompName;
    }

    public String getTransCompNation() {
        return TransCompNation;
    }

    public void setTransCompNation(String transCompNation) {
        TransCompNation = transCompNation;
    }

    public String getTransCompCity() {
        return TransCompCity;
    }

    public void setTransCompCity(String transCompCity) {
        TransCompCity = transCompCity;
    }

    public String getTransCompAddress() {
        return TransCompAddress;
    }

    public void setTransCompAddress(String transCompAddress) {
        TransCompAddress = transCompAddress;
    }

    public String getTransCompPhone() {
        return TransCompPhone;
    }

    public void setTransCompPhone(String transCompPhone) {
        TransCompPhone = transCompPhone;
    }

    public String getTransCompZIP() {
        return TransCompZIP;
    }

    public void setTransCompZIP(String transCompZIP) {
        TransCompZIP = transCompZIP;
    }

    public String getTransCompDate() {
        return TransCompDate;
    }

    public void setTransCompDate(String transCompDate) {
        TransCompDate = transCompDate;
    }
}
