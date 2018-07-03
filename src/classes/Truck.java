package classes;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;


/**
 *  The main purpose of this class is to hold all data which is directly related to a truck. As the class will instantiated,
 *  the created object can be saved into the database, can help to calculate financial informations and can provide help to
 *  display the truck's data in tables.
 */
public class Truck {

    /**
     * The truck has an ID.
     */
    private final SimpleStringProperty truckID;

    /**
     * The name od the company which owns the truck.
     */
    private final SimpleStringProperty transCompName;

    /**
     * The plate number of the truck. It help to identify it.
     */
    private final SimpleStringProperty plateNumber;

    /**
     * The brand of the truck.
     */
    private final SimpleStringProperty truckBrand;

    /**
     * The date and time when the truck arrived and moved into the terminal.
     */
    private final SimpleStringProperty truckStartDate;

    /**
     * The date and time when the truck moved out of the terminal.
     */
    private final SimpleStringProperty truckEndDate;

    /**
     * The amount of money in hungarian HUF what was paid for the parking.
     */
    private final SimpleDoubleProperty paid;


    /**
     * The constructor which helps to instantiate the Truck class.
     * @param truckID The ID of truck.
     * @param transCompName The name of the company which owns the truck.
     * @param plateNumber The truck's plate number.
     * @param truckBrand The truck's brand (like MAN, Mercedes, Volvo, etc.).
     * @param truckStartDate The date and time when the truck moved into the terminal.
     * @param truckEndDate The date and time when the truck left the terminal.
     * @param paid The amount of money, in hungarian HUF, what the trucks driver have to pay for parking in the terminal.
     */
    public Truck(String truckID,
                        String transCompName,
                        String plateNumber,
                        String truckBrand,
                        String truckStartDate,
                        String truckEndDate,
                        double paid)
    {
        this.truckID = new SimpleStringProperty(truckID);
        this.transCompName = new SimpleStringProperty(transCompName);
        this.plateNumber = new SimpleStringProperty(plateNumber);
        this.truckBrand = new SimpleStringProperty(truckBrand);
        this.truckStartDate = new SimpleStringProperty(truckStartDate);
        this.truckEndDate = new SimpleStringProperty(truckEndDate);
        this.paid = new SimpleDoubleProperty(paid);
    }

    public String getTruckID() {
        return truckID.get();
    }

    public String getTransCompName() {
        return transCompName.get();
    }

    public String getPlateNumber() {
        return plateNumber.get();
    }

    public String getTruckBrand() {
        return truckBrand.get();
    }

    public String getTruckStartDate() {
        return truckStartDate.get();
    }

    public String getTruckEndDate() {
        return truckEndDate.get();
    }

    public double getPaid() {
        return paid.get();
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber.set(plateNumber);
    }

    public void setTruckBrand(String truckBrand) {
        this.truckBrand.set(truckBrand);
    }
}
