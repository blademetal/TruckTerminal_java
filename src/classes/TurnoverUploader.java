package classes;


/**
 *  The class helps to create a TurnoverUploader object which contains the main traffic and financial information for
 *  a selected time period. From the objects data the program can generate an XLS format financial report.
 */
public class TurnoverUploader {


    /**
     * The number of truck moved in the terminal in a selected time period.
     */
    private int truckInTurn;

    /**
     * The number of truck moved out from the terminal in a selected time period.
     */
    private int truckOutTurn;

    /**
     * The difference between the incoming and out coming trucks in the selected time period.
     */
    private int truckDiffTurn;

    /**
     * The generated revenue in a selected time period in hungarian HUF.
     */
    private double revTurn;

    /**
     * The generated expediture in a selected time period in hungarian HUF.
     */
    private double expTurn;

    /**
     * The cashflow in a selected period in hungarian HUF which is calculated subtracting the expTurn from the revTurn.
     */
    private double diffRevExpTurn;


    /**
     * The constructor which helps to create a TurnoverUploader object.
     * @param truckInTurn The number of truck moved in the terminal in a selected time period
     * @param truckOutTurn The number of truck moved out from the terminal in a selected time period.
     * @param truckDiffTurn The difference between the incoming and out coming trucks in the selected time period.
     * @param revTurn The generated revenue in a selected time period in hungarian HUF.
     * @param expTurn The generated expediture in a selected time period in hungarian HUF.
     * @param diffRevExpTurn The cashflow in a selected period in hungarian HUF which is calculated subtracting the expTurn from the revTurn.
     */
    public TurnoverUploader(int truckInTurn,
                            int truckOutTurn,
                            int truckDiffTurn,
                            double revTurn,
                            double expTurn,
                            double diffRevExpTurn
    ) {
        this.truckInTurn = truckInTurn;
        this.truckOutTurn = truckOutTurn;
        this.truckDiffTurn = truckDiffTurn;
        this.revTurn = revTurn;
        this.expTurn = expTurn;
        this.diffRevExpTurn = diffRevExpTurn;
    }


    public int getTruckInTurn() {
        return truckInTurn;
    }

    public int getTruckOutTurn() {
        return truckOutTurn;
    }

    public int getTruckDiffTurn() {
        return truckDiffTurn;
    }

    public double getRevTurn() {
        return revTurn;
    }

    public double getExpTurn() {
        return expTurn;
    }

    public double getDiffRevExpTurn() {
        return diffRevExpTurn;
    }
}
