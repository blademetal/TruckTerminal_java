package classes;

import database.DatabaseHandler;
import ui.addTruck.AddTruckController;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  This class holds all the important database loader and utility methods.
 * It's also in charge of calculating prices of each parking and to decide if the truck was in the terminal
 * in a given period, or wasn't.
 */
public class Utilities {

    /**
     * Creating a Properties object.
     */
    private Properties priceProps = new Properties();

    /**
     * This ArrayList will contain the Truck objects which went already out from the terminal.
     */
    private ArrayList<Truck> alreadyOutList = new ArrayList<>();

    /**
     * This ArrayList will contain the Truck objects which are now in the terminal.
     */
    private ArrayList<Truck> yetInList = new ArrayList<>();

    /**
     * This ArrayList will contain the Expenditure objects.
     */
    private ArrayList<Expenditure> expList = new ArrayList<>();

    /**
     * This ArrayList will contain the Shift objects.
     */
    private ArrayList<Shift> shiftList = new ArrayList<>();

    /**
     * This ArrayList will contain the indexes of Shift objects, so they will be more accessible.
     */
    private ArrayList<Integer> indexList = new ArrayList<>();

    /**
     * This variable will hold the size of shiftList ArrayList globally.
     */
    public int shiftListLength = 0;


    /**
     * This variable will hold the value of the revenues in the latest shift in hungarian HUF.
     */
    public double revInLatestShiftSum = 0;

    /**
     * This variable will hold the value of the expenditures in the latest shift in hungarian HUF.
     */
    public double expInLatestShiftSum = 0;


    /**
     * Holds the type of paying for the parking (there are 3 of them).
     */
    private String typeOfPay;

    /**
     * If the paying type requires it will hold a not-null value, which will denote how much hours will
     * the company charge in one round
     */
    private String NumOfHour;

    /**
     * If the paying type requires it will hold a not-null value, which will denote the hourly charge for parking.
     */
    private String PriceOfHour;

    /**
     * If the paying type requires it will hold a not-null value, which will denote the daily charge for parking.
     */
    private String PriceOfDay;


    /**
     * A global DatabaseHandler object, which will be used in the run() method.
     */
    private DatabaseHandler databaseHandler = null;


    /**
     * The constructor method of the Utilities class.
     */
    public Utilities(){

        try {
            databaseHandler = DatabaseHandler.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            priceProps.load(new FileInputStream("TeMeReSettings.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        typeOfPay = priceProps.getProperty("Type");
        NumOfHour = priceProps.getProperty("NumOfHour");
        PriceOfHour = priceProps.getProperty("PriceOfHour");
        PriceOfDay = priceProps.getProperty("PriceOfDay");

        loadTruckData();
        loadShiftData();
        loadExpData();

        revInLatestShiftSum = getRevInLatestShift();
        expInLatestShiftSum = getExpInLatestShift();
        System.out.println(revInLatestShiftSum);


        shiftListLength = shiftList.size();
        shouldCreateShift();

    }


    /**
     * If the mehod is called, it refreshes all the important ArrayLists, and also gets the latest created Shift object.
     */
    public void refreshDatabasePrices() {

        loadTruckData();
        loadShiftData();
        loadExpData();

        getRevInLatestShift();
        revInLatestShiftSum = getRevInLatestShift();
        expInLatestShiftSum = getExpInLatestShift();
    }


    /**
     * The method loads all Truck data from the database, and will sort out according to it's state regarding, are the
     * truck yet in or already moved out. In this classifier part, it will fill up the yetInList and alreadyOutList ArrayLists.
     */
    private void loadTruckData() {
        alreadyOutList.clear();
        yetInList.clear();
        DatabaseHandler handler = null;
        try {
            handler = DatabaseHandler.getInstance();
            String query = "SELECT * FROM Truck_Data";
            ResultSet re = handler.execQuery(query);
            while (re.next()) {
                String truckID = re.getString("id");
                String transCompName = re.getString("cegnev");
                String plateNumber = re.getString("rendszam");
                String truckBrand = re.getString("marka");
                String truckStartDate = re.getString("beerkezes_datum");
                String truckEndDate = re.getString("kilepes_datum");
                double paid = re.getDouble("fizetett");


                if (Objects.equals(truckEndDate,null)
                        || Objects.equals(truckEndDate, "null")
                        || Objects.equals(truckEndDate, "")) {
                    yetInList.add(new Truck(truckID,
                            transCompName,
                            plateNumber,
                            truckBrand,
                            truckStartDate,
                            databaseHandler.getCreationDate(),
                            0
                    ));
                } else if (!Objects.equals(truckEndDate,null)
                            && !Objects.equals(truckEndDate, "null")
                            && !Objects.equals(truckEndDate, "")
                            && paid >= 0.0) {
                        alreadyOutList.add(new Truck(truckID,
                                transCompName,
                                plateNumber,
                                truckBrand,
                                truckStartDate,
                                truckEndDate,
                                paid
                        ));
                    }
                }
            } catch (SQLException ex) {
            Logger.getLogger(AddTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * This method will calculate the owing prices.
     * @param start The date and time when the truck moved in.
     * @param end The date and time when the truck went out.
     * @return The owing price.
     * @throws ParseException It is required because of the getDateDiff method.
     */
    public double getPrice(String start, String end) throws ParseException {
        int dateDiff = getDateDiff(start, end) + 1;
        System.out.println("Az órában mért különbség :" + dateDiff);
        if(Objects.equals(typeOfPay, "perHour")) {
            int diffFloor = ceilDiv(dateDiff, Integer.parseInt(NumOfHour));
            return diffFloor * Integer.parseInt(PriceOfHour);
        } else if (Objects.equals(typeOfPay, "perDay")) {
            int diffFloor = ceilDiv(dateDiff, 24);
            return diffFloor * Integer.parseInt(PriceOfDay);
        } else if (Objects.equals(typeOfPay, "Hibrid")) {
            int diffDay = Math.floorDiv(dateDiff, 24);
            int remainHours = Math.floorMod(dateDiff, 24);
            int diffHour = ceilDiv(remainHours, Integer.parseInt(NumOfHour));
            double endPrice = diffDay*Integer.parseInt(PriceOfDay) + diffHour*Integer.parseInt(PriceOfHour);
            return endPrice;
        }
        return 0;
    }


    /**
     * Calculates the difference between two date in hours.
     * @param start Starting date.
     * @param end Ending date.
     * @return The difference in hours.
     * @throws ParseException It is required because of the convertDate method.
     */
    private int getDateDiff(String start, String end) throws ParseException {
        Date startDate = convertDate(start);
        Date endDate = convertDate(end);
        return (int) getDateDiff(startDate, endDate, TimeUnit.HOURS);
    }


    /**
     * From String input, this method creates and return Date output.
     * @param date A date in String format with a yyyy/MM/dd HH:mm:ss pattern.
     * @return The same date as the input but as a Date object.
     * @throws ParseException It is needed because of the date parsing.
     */
    private Date convertDate(String date) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        return format.parse(date);
    }

    /**
     * The method calculate difference between two date in milliseconds.
     * @param date1 The starting date.
     * @param date2 The ending date.
     * @param timeUnit The used timeUnit by the calculation.
     * @return Return a long number, which is the difference in miliseconds.
     */
    public long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }


    /**
     * Calculates the dividend and ceil rounded value of two numbers.
     * @param x The first integer number.
     * @param y The second integer number.
     * @return Returns a ceil rounded integer number.
     */
    private int ceilDiv(int x, int y){
        return -Math.floorDiv(-x,y);
    }


    /**
     * Loads all shift data from the database, and appends it to the shiftList ArrayList.
     */
    private void loadShiftData() {
        shiftList.clear();
        indexList.clear();
        DatabaseHandler handler = null;
        try {
            handler = DatabaseHandler.getInstance();
            String query = "SELECT * FROM Shift_Data";
            ResultSet re = handler.execQuery(query);
            while (re.next()) {
                int shiftID = re.getInt("id");
                int shiftNumIn = re.getInt("be_kamionok_szama");
                int shiftNumOut = re.getInt("ki_kamionok_szama");
                double shiftRev = re.getDouble("bevetel");
                double shiftExp = re.getDouble("kiadas");
                String shiftStart = re.getString("kezdet");
                String shiftEnd = re.getString("veg");

                System.out.println(shiftID);
                System.out.println(shiftStart);

                shiftList.add(new Shift(shiftID,
                        shiftNumIn,
                        shiftNumOut,
                        shiftRev,
                        shiftExp,
                        shiftStart,
                        shiftEnd
                ));

                indexList.add(shiftID);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * Loads all expenditure data from the database, and appends it to the expList ArrayList.
     */
    private void loadExpData() {
        expList.clear();
        DatabaseHandler handler = null;
        try {
            handler = DatabaseHandler.getInstance();
            String query = "SELECT * FROM Expenditure_Data";
            ResultSet re = handler.execQuery(query);
            while (re.next()) {
                String expID = re.getString("id");
                String expType = re.getString("tipus");
                double expPricePerPiece = re.getDouble("egysegar");
                int expAmount = re.getInt("darabszam");
                double expPaid = re.getDouble("osszeg");
                String expDate = re.getString("datum");

                expList.add(new Expenditure(expID,
                        expType,
                        expPricePerPiece,
                        expAmount,
                        expPaid,
                        expDate
                ));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * The method check if there are existing data in the Shift_Data table in the database.
     * @return A boolean which returns true if there is data, and false if isn't.
     */
    public Boolean isShiftDataAvailable() {
        DatabaseHandler handler = null;
        try {
            handler = DatabaseHandler.getInstance();
            String query = "SELECT * FROM Shift_Data";
            ResultSet re = handler.execQuery(query);
            if (re.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }


    /**
     * Gets the revenues from the last shift.
     * @return A Double type number, the sum of revenues.
     */
    private double getRevInLatestShift() {
        double num = 0;
        for (Truck truck: alreadyOutList
             ) {
            if (wasInLastShiftTruck(truck))
                num += truck.getPaid();
        }
        return num;
    }

    /**
     * Gets the latest Shift what it founds in the database.
     * @return The latest Shift or a null, if there is nothing.
     */
    private Shift getLatestShift(){
        ArrayList<Integer> shiftIDs = new ArrayList<>();
        for (Shift shift : shiftList
             ) {
            shiftIDs.add(shift.getShiftID());
        }

        shiftIDs.sort(Collections.reverseOrder());

        int lsID = shiftIDs.get(0);
        Shift ls = null;

        for (Shift shift : shiftList
             ) {
            if(shift.getShiftID() == lsID) {
                ls = shift;
            }
        }

        return ls;
    }


    /**
     * Check if the conditions demand to create a new Shift. It compares the current time in milliseconds
     * with the latest Shifts starting time.
     * @return A boolean which if false then no new Shift is needed, if true, then a new Shift have to be created.
     */
    public Boolean shouldCreateShift() {
        Shift latest;
        if(shiftList.size()>0) {
            latest = getLatestShift();
        } else {
            return false;
        }

        Calendar latestCal = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();

        long latestCalInMillis;
        long nowinMillis = calNow.getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);

        try {
            latestCal.setTime(sdf.parse(latest.getShiftStart()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        latestCalInMillis = latestCal.getTimeInMillis();

        if((nowinMillis - latestCalInMillis) > 86400000) {
            return true;
        }
        return false;
    }


    /**
     * Checks if a given truck left the terminal in the latest shift.
     * @param truck An inspected Truck object.
     * @return True if the truck left in the latest Shift, else return false.
     */
    private Boolean wasInLastShiftTruck(Truck truck) {
        Shift latest;
        if(shiftList.size()>0) {
            latest = getLatestShift();
        } else {
            return false;
        }

        Calendar latestCal = Calendar.getInstance();
        Calendar truckCal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);

        try {
            latestCal.setTime(sdf.parse(latest.getShiftStart()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            truckCal.setTime(sdf.parse(truck.getTruckEndDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long latestCalInMillis = latestCal.getTimeInMillis();
        long truckCalInMillis = truckCal.getTimeInMillis();

        return (truckCalInMillis>latestCalInMillis);
    }


    /**
     * Checks if a given truck left the terminal in the examined period.
     * @param startDate The period's starting date as a String object.
     * @param endDate The periods ending date as a String object.
     * @param truck The examined Truck object.
     * @param inOrOut It is 0 - Truck moving in date and 1 - Truck moving out date.
     * @return True if the Truck's attribute was partly in in that period, else false.
     */
    public Boolean wasInExaminedTimePeriodTruck(String startDate, String endDate, Truck truck, int inOrOut) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        Calendar truckInCal = Calendar.getInstance();
        Calendar truckOutCal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);

        try {
            startCal.setTime(sdf.parse(startDate));
            endCal.setTime(sdf.parse(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            truckInCal.setTime(sdf.parse(truck.getTruckStartDate()));
            truckOutCal.setTime(sdf.parse(truck.getTruckStartDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long startCalInMillis = startCal.getTimeInMillis();
        long endCalInMillis = endCal.getTimeInMillis();
        long truckInCalInMillis = truckInCal.getTimeInMillis();
        long truckOutCalInMillis = truckOutCal.getTimeInMillis();


        // 0 - Truck moving in date
        // 1 - Truck moving out date
        if (inOrOut == 0) {
            return ((truckInCalInMillis > startCalInMillis && truckInCalInMillis < endCalInMillis));
        } else
            return inOrOut == 1 && (truckOutCalInMillis > startCalInMillis && truckOutCalInMillis < endCalInMillis);
    }


    /**
     * Checks if a given truck left the terminal in the examined period.
     * @param startDate The period's starting date as a long.
     * @param endDate The periods ending date as a long.
     * @param truck The examined Truck object.
     * @param inOrOut It is 0 - Truck moving in date and 1 - Truck moving out date.
     * @return True if the Truck's attribute was partly in in that period, else false.
     */
    public Boolean wasInExaminedTimePeriodTruck(long startDate, long endDate, Truck truck, int inOrOut) {
        Calendar truckInCal = Calendar.getInstance();
        Calendar truckOutCal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);

        try {
            truckInCal.setTime(sdf.parse(truck.getTruckStartDate()));
            truckOutCal.setTime(sdf.parse(truck.getTruckStartDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long truckInCalInMillis = truckInCal.getTimeInMillis();
        long truckOutCalInMillis = truckOutCal.getTimeInMillis();

        // 0 - Truck moving in date
        // 1 - Truck moving out date
        if (inOrOut == 0) {
            return ((truckInCalInMillis > startDate && truckInCalInMillis < endDate));
        } else
            return inOrOut == 1 && (truckOutCalInMillis > startDate && truckOutCalInMillis < endDate);
    }


    /**
     * Checks if examined expenditure occurred in the latest shift.
     * @param exp An examined Expenditure object.
     * @return True if the Expenditure object occurred in the latest Shift, else false.
     */
    private Boolean wasInLastShiftExp(Expenditure exp) {
        Shift latest;
        if(shiftList.size()>0) {
            latest = getLatestShift();
        } else {
            return false;
        }

        Calendar latestCal = Calendar.getInstance();
        Calendar expCal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);

        try {
            latestCal.setTime(sdf.parse(latest.getShiftStart()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            expCal.setTime(sdf.parse(exp.getExpDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long latestCalInMillis = latestCal.getTimeInMillis();
        long expCalInMillis = expCal.getTimeInMillis();

        return (expCalInMillis>latestCalInMillis);
    }


    /**
     * Checks the examined expenditure occurrence date.
     * @param startDate The starting date as a String object.
     * @param endDate The ending date as a String object.
     * @param exp The examined Expenditure object.
     * @return True if the expenditure occurred in the given period, else false.
     */
    public Boolean wasInExaminedTimePeriodExp(String startDate, String endDate, Expenditure exp) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        Calendar expCal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);

        try {
            startCal.setTime(sdf.parse(startDate));
            endCal.setTime(sdf.parse(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            expCal.setTime(sdf.parse(exp.getExpDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long startCalInMillis = startCal.getTimeInMillis();
        long endCalInMillis = endCal.getTimeInMillis();
        long expCalInMillis = expCal.getTimeInMillis();

        return (expCalInMillis > startCalInMillis && expCalInMillis < endCalInMillis);
    }


    /**
     * Checks the examined expenditure occurrence date.
     * @param startDate The starting date as a long.
     * @param endDate The ending date as a long.
     * @param exp The examined Expenditure object.
     * @return True if the expenditure occurred in the given period, else false.
     */
    public Boolean wasInExaminedTimePeriodExp(long startDate, long endDate, Expenditure exp) {
        Calendar expCal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);

        try {
            expCal.setTime(sdf.parse(exp.getExpDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long expCalInMillis = expCal.getTimeInMillis();

        return (expCalInMillis > startDate && expCalInMillis < endDate);
    }


    /**
     * Get the sum of spent money in the latest shift.
     * @return The sum of spent money in the latest shift as a double.
     */
    private double getExpInLatestShift() {
        double num = 0;
        for (Expenditure expenditure: expList
                ) {
            if (wasInLastShiftExp(expenditure)) {
                num += expenditure.getExpPaid();
            }
        }
        return num;
    }
}
