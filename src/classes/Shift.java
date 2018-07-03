package classes;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *  This class is responsible for creating a Shift object which is 24 hours long, and takes place, form 08:00 AM day 1 until
 * 08:00 AM day 2, in time. The class will bi instantiated when the program initialize a new Shift every morning az 08:00 AM
 * or when a table is filled with shift data. It also helps to calculate financial datas.
 */
public class Shift {

    /**
     * The Shift has an ID.
     */
    private final SimpleIntegerProperty shiftID;

    /**
     * The number of truck went in during the Shift.
     */
    private final SimpleIntegerProperty shiftNumIn;

    /**
     * The number of truck went out during the Shift.
     */
    private final SimpleIntegerProperty shiftNumOut;

    /**
     * The revenue what was generated during the Shift.
     */
    private final SimpleDoubleProperty shiftRev;

    /**
     * The expenditure what was spent during the Shift.
     */
    private final SimpleDoubleProperty shiftExp;

    /**
     * The date and time when the Shift started.
     */
    private final SimpleStringProperty shiftStart;

    /**
     * The date end time when the Shift ended.
     */
    private final SimpleStringProperty shiftEnd;


    /**
     * Construnctor for the Shift class. It will instantiate a Shift object which can provide
     * the many informations about the Shift.
     * @param shiftID The ID of the Shift
     * @param shiftNumIn The number of Truck went in during the Shift
     * @param shiftNumOut The number of Truck went out during the Shift
     * @param shiftRev The revenue generated during the Shift
     * @param shiftExp The spending in the Shift
     * @param shiftStart The date and time when the Shift started
     * @param shiftEnd The date and time when the Shift ended
     */
    public Shift(int shiftID,
                  int shiftNumIn,
                  int shiftNumOut,
                  double shiftRev,
                  double shiftExp,
                  String shiftStart,
                  String shiftEnd)
    {
        this.shiftID = new SimpleIntegerProperty(shiftID);
        this.shiftNumIn = new SimpleIntegerProperty(shiftNumIn);
        this.shiftNumOut = new SimpleIntegerProperty(shiftNumOut);
        this.shiftRev = new SimpleDoubleProperty(shiftRev);
        this.shiftExp = new SimpleDoubleProperty(shiftExp);
        this.shiftStart = new SimpleStringProperty(shiftStart);
        this.shiftEnd = new SimpleStringProperty(shiftEnd);
    }

    public int getShiftID() {
        return shiftID.get();
    }

    public SimpleIntegerProperty shiftIDProperty() {
        return shiftID;
    }

    public int getShiftNumIn() {
        return shiftNumIn.get();
    }

    public SimpleIntegerProperty shiftNumInProperty() {
        return shiftNumIn;
    }

    public int getShiftNumOut() {
        return shiftNumOut.get();
    }

    public SimpleIntegerProperty shiftNumOutProperty() {
        return shiftNumOut;
    }

    public double getShiftRev() {
        return shiftRev.get();
    }

    public SimpleDoubleProperty shiftRevProperty() {
        return shiftRev;
    }

    public double getShiftExp() {
        return shiftExp.get();
    }

    public SimpleDoubleProperty shiftExpProperty() {
        return shiftExp;
    }

    public String getShiftStart() {
        return shiftStart.get();
    }

    public SimpleStringProperty shiftStartProperty() {
        return shiftStart;
    }

    public String getShiftEnd() {
        return shiftEnd.get();
    }

    public SimpleStringProperty shiftEndProperty() {
        return shiftEnd;
    }
}
