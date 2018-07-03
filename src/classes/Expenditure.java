package classes;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *  This class will help to instantiate an expenditure instance, if the user wants to add one to the database,
 * or the program wants to fill up an expenditure table. Also useful if the program wants to calculate the expanses
 * and cashflow.
 */
public class Expenditure {

    /**
     * The expenditure's ID.
     */
    private final SimpleStringProperty expID;

    /**
     * The expenditure's type. What service or object the expense covered.
     */
    private final SimpleStringProperty expType;

    /**
     * The expenditure's price per piece.
     */
    private final SimpleDoubleProperty expPricePerPiece;

    /**
     * The amount how much such object or service was bought.
     */
    private final SimpleIntegerProperty expAmount;

    /**
     * This is the multiplication of the expPrivePerPiece and ExpAmount variables.
     * The variable denotes how much money was spent on the item.
     */
    private final SimpleDoubleProperty expPaid;

    /**
     * When the expenditure occurred.
     */
    private final SimpleStringProperty expDate;


    /**
     * Constructor of the Expenditure class. Creates an instance of the aforementioned class.
     * @param expID The expenditure's ID
     * @param expType The expenditure's type
     * @param expPricePerPiece The price per unit of the expenditure
     * @param expAmount The amount the user bought
     * @param expPaid The amount of money the user had to pay for this particular expenditure
     * @param expDate When the paying occurred
     */
    public Expenditure(String expID,
                       String expType,
                       double expPricePerPiece,
                       int expAmount,
                       double expPaid,
                       String expDate)
    {
        this.expID = new SimpleStringProperty(expID);
        this.expType = new SimpleStringProperty(expType);
        this.expPricePerPiece = new SimpleDoubleProperty(expPricePerPiece);
        this.expAmount = new SimpleIntegerProperty(expAmount);
        this.expPaid = new SimpleDoubleProperty(expPaid);
        this.expDate = new SimpleStringProperty(expDate);
    }


    public String getExpID() {
        return expID.get();
    }

    public SimpleStringProperty expIDProperty() {
        return expID;
    }

    public String getExpType() {
        return expType.get();
    }

    public SimpleStringProperty expTypeProperty() {
        return expType;
    }

    public double getExpPricePerPiece() {
        return expPricePerPiece.get();
    }

    public SimpleDoubleProperty expPricePerPieceProperty() {
        return expPricePerPiece;
    }

    public int getExpAmount() {
        return expAmount.get();
    }

    public SimpleIntegerProperty expAmountProperty() {
        return expAmount;
    }

    public double getExpPaid() {
        return expPaid.get();
    }

    public SimpleDoubleProperty expPaidProperty() {
        return expPaid;
    }

    public String getExpDate() {
        return expDate.get();
    }

    public SimpleStringProperty expDateProperty() {
        return expDate;
    }
}
