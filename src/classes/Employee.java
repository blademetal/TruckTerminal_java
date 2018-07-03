package classes;

import javafx.beans.property.SimpleStringProperty;

/**
 *  This class will help to instantiate an employee, if the user wants to add one to the database,
 * or the program wants to fill up an employee table.
 */
public class Employee {

    /**
     * The employee's ID
     */
    private final SimpleStringProperty empID;

    /**
     * The employee's username
     */
    private final SimpleStringProperty empUser;

    /**
     * The employee's password
     */
    private final SimpleStringProperty empPass;

    /**
     * The employee's surename
     */
    private final SimpleStringProperty empLastN;

    /**
     * The employee's first name
     */
    private final SimpleStringProperty empFirstN;

    /**
     * The employee's birth date
     */
    private final SimpleStringProperty empBirth;

    /**
     * The employee's phone number
     */
    private final SimpleStringProperty empPhone;

    /**
     * The employee's living place (City)
     */
    private final SimpleStringProperty empPlace;

    /**
     * The employee's address (Street, and other infos)
     */
    private final SimpleStringProperty empAddress;

    /**
     * The employee's ZIP Code
     */
    private final SimpleStringProperty empZIP;

    /**
     * The employee's start date at this company
     */
    private final SimpleStringProperty empStart;

    /**
     * The date the employee was dismissed from the company
     */
    private final SimpleStringProperty empEnd;


    /**
     * The constructor of this class, which creates an employee instance.
     * @param empID The employee's ID code
     * @param empUser The employee's username
     * @param empPass The employee's password
     * @param empLastN The employee's surename
     * @param empFirstN The employee's first name
     * @param empBirth The employee's birth date
     * @param empPhone The employee's phone number
     * @param empPlace The employee's home city
     * @param empAddress The employee's home address
     * @param empZIP The employee's home address ZIP Code
     * @param empStart The date when the employee started his job at this company
     * @param empEnd The date when the employee ended his job at this company
     */
    public Employee(String empID,
                    String empUser,
                    String empPass,
                    String empLastN,
                    String empFirstN,
                    String empBirth,
                    String empPhone,
                    String empPlace,
                    String empAddress,
                    String empZIP,
                    String empStart,
                    String empEnd) {
        this.empID = new SimpleStringProperty(empID);
        this.empUser = new SimpleStringProperty(empUser);
        this.empPass = new SimpleStringProperty(empPass);
        this.empLastN = new SimpleStringProperty(empLastN);
        this.empFirstN = new SimpleStringProperty(empFirstN);
        this.empBirth = new SimpleStringProperty(empBirth);
        this.empPhone = new SimpleStringProperty(empPhone);
        this.empPlace = new SimpleStringProperty(empPlace);
        this.empAddress = new SimpleStringProperty(empAddress);
        this.empZIP = new SimpleStringProperty(empZIP);
        this.empStart = new SimpleStringProperty(empStart);
        this.empEnd = new SimpleStringProperty(empEnd);
    }

    public String getEmpID() {
        return empID.get();
    }

    public SimpleStringProperty empIDProperty() {
        return empID;
    }

    public String getEmpUser() {
        return empUser.get();
    }

    public SimpleStringProperty empUserProperty() {
        return empUser;
    }

    public String getEmpPass() {
        return empPass.get();
    }

    public SimpleStringProperty empPassProperty() {
        return empPass;
    }

    public String getEmpLastN() {
        return empLastN.get();
    }

    public SimpleStringProperty empLastNProperty() {
        return empLastN;
    }

    public String getEmpFirstN() {
        return empFirstN.get();
    }

    public SimpleStringProperty empFirstNProperty() {
        return empFirstN;
    }

    public String getEmpBirth() {
        return empBirth.get();
    }

    public SimpleStringProperty empBirthProperty() {
        return empBirth;
    }

    public String getEmpPhone() {
        return empPhone.get();
    }

    public SimpleStringProperty empPhoneProperty() {
        return empPhone;
    }

    public String getEmpPlace() {
        return empPlace.get();
    }

    public SimpleStringProperty empPlaceProperty() {
        return empPlace;
    }

    public String getEmpAddress() {
        return empAddress.get();
    }

    public SimpleStringProperty empAddressProperty() {
        return empAddress;
    }

    public String getEmpZIP() {
        return empZIP.get();
    }

    public SimpleStringProperty empZIPProperty() {
        return empZIP;
    }

    public String getEmpStart() {
        return empStart.get();
    }

    public SimpleStringProperty empStartProperty() {
        return empStart;
    }

    public String getEmpEnd() {
        return empEnd.get();
    }

    public SimpleStringProperty empEndProperty() {
        return empEnd;
    }
}