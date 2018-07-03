package database;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.swing.*;
import java.security.SecureRandom;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *  This class is responsible for creating the database connection and sets up all the used tables.
 * Another functionality is that it holds methods which return new and random IDs, and methods
 * that can return dates.
 */
public class DatabaseHandler {

    /**
     *  The variable hold a DtabaseHandler object.
     */
    private static DatabaseHandler handler = null;

    /**
     * This String variable holds the URL of the database.
     */
    private static final String DB_URL = "jdbc:sqlite:C:\\databases\\testjava.db";

    /**
     * This instantiated Connection object will help the program to create a connection with the database.
     */
    private static Connection conn = null;

    /**
     * This instantiated Statement object will help creating SQL statements.
     */
    private static Statement stmt = null;

    /**
     * The variable holds an instance of the SecureRandom object, and it will be used for the creation of a secure ID.
     */
    private SecureRandom rnd = new SecureRandom();


    /**
     * This methos uses all the predefined methods and creates all the data tables.
     * @throws SQLException Handles the SQLException.
     */
    public DatabaseHandler() throws SQLException {
        createConnection();
        setupTransCompanyTable();
        setupEmployeeDataTable();
        setupTruckDataTable();
        setupExpenditureDataTable();
        setupShiftDataTable();
    }


    /**
     * This method helps to get an instance of the DatabaseHandler class.
     * @return A DatabaseHandler object.
     * @throws SQLException Handles the SQLException.
     */
    public static DatabaseHandler getInstance() throws SQLException {
        if(handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }


    /**
     * This method creates the initial connection with the database.
     * @throws SQLException Handles the SQLException.
     */
    public static void createConnection() throws SQLException {
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Sikeresen csatlakozott az adatbázishoz.");
        } catch(SQLException e) {
            System.out.println("Az adatbázishoz való csatlakozás nem jött létre, az ok: " + e.getMessage());
        }
    }


    /**
     * This method is creates the Trans_Company table in the database, and sets up it's parameters.
     */
    private void setupTransCompanyTable() {
        String TABLE_NAME = "Trans_Company";
        try {
            stmt = conn.createStatement();

            java.sql.DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            System.out.println(tables.next());
            if(tables.next()) {
                System.out.println("Az " + TABLE_NAME + " nevű adattábla létezik. A művelet folytatható.");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                                + "id TEXT PRIMARY KEY, \n"
                                + "cegnev TEXT, \n"
                                + "orszag TEXT, \n"
                                + "telefon TEXT, \n"
                                + "varos TEXT, \n"
                                + "cim TEXT, \n"
                                + "iranyitoszam TEXT, \n"
                                + "datum TEXT\n"
                                + ");");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "... adatbázis telepítés");
        }
    }


    /**
     * This method is creates the Employee_Data table in the database, and sets up it's parameters.
     */
    private void setupEmployeeDataTable() {
        String TABLE_NAME = "Employee_Data";
        try {
            stmt = conn.createStatement();

            java.sql.DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            System.out.println(tables.next());
            if(tables.next()) {
                System.out.println("Az " + TABLE_NAME + " nevű adattábla létezik. A művelet folytatható.");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "id TEXT PRIMARY KEY, \n"
                        + "felhasznalonev TEXT, \n"
                        + "jelszo TEXT, \n"
                        + "vezeteknev TEXT, \n"
                        + "keresztnev TEXT, \n"
                        + "szuletesi_datum TEXT, \n"
                        + "telefon TEXT, \n"
                        + "lakhely TEXT, \n"
                        + "cim TEXT, \n"
                        + "iranyitoszam TEXT,\n"
                        + "kezdett TEXT,\n"
                        + "befejezte TEXT\n"
                        + ");");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "... adatbázis telepítés");
        }
    }


    /**
     * This method is creates the Truck_Data table in the database, and sets up it's parameters.
     */
    private void setupTruckDataTable() {
        String TABLE_NAME = "Truck_Data";
        try {
            stmt = conn.createStatement();

            java.sql.DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            System.out.println(tables.next());
            if(tables.next()) {
                System.out.println("Az " + TABLE_NAME + " nevű adattábla létezik. A művelet folytatható.");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "id TEXT PRIMARY KEY, \n"
                        + "cegnev TEXT, \n"
                        + "rendszam TEXT, \n"
                        + "marka TEXT, \n"
                        + "beerkezes_datum TEXT, \n"
                        + "kilepes_datum TEXT, \n"
                        + "fizetett REAL \n"
                        + ");");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "... adatbázis telepítés");
        }
    }


    /**
     * This method is creates the Expenditure_Data table in the database, and sets up it's parameters.
     */
    private void setupExpenditureDataTable() {
        String TABLE_NAME = "Expenditure_Data";
        try {
            stmt = conn.createStatement();

            java.sql.DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            System.out.println(tables.next());
            if(tables.next()) {
                System.out.println("Az " + TABLE_NAME + " nevű adattábla létezik. A művelet folytatható.");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "id TEXT PRIMARY KEY, \n"
                        + "tipus TEXT, \n"
                        + "egysegar REAL, \n"
                        + "darabszam INTEGER, \n"
                        + "osszeg REAL, \n"
                        + "datum TEXT \n"
                        + ");");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "... adatbázis telepítés");
        }
    }

    /**
     * This method is creates the Shift_Data table in the database, and sets up it's parameters.
     */
    private void setupShiftDataTable() {
        String TABLE_NAME = "Shift_Data";
        try {
            stmt = conn.createStatement();

            java.sql.DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            System.out.println(tables.next());
            if(tables.next()) {
                System.out.println("Az " + TABLE_NAME + " nevű adattábla létezik. A művelet folytatható.");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, \n"
                        + "be_kamionok_szama INTEGER, \n"
                        + "ki_kamionok_szama INTEGER, \n"
                        + "bevetel REAL, \n"
                        + "kiadas REAL, \n"
                        + "kezdet TEXT, \n"
                        + "veg TEXT \n"
                        + ");");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "... adatbázis telepítés");
        }
    }


    /**
     * This method executes a SQL query.
     * @param query A valid SQL query in String format.
     * @return The result as a ResultSet object if it found something, otherwise null.
     */
    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        } catch(SQLException e) {
            System.out.println("A lekérdezés nem megvalósítható, az ok: " + e.getLocalizedMessage());
            return null;
        }
        return result;
    }


    /**
     * This method executes the SQL command.
     * @param act This is the command what will be executed. It has to be a valid SQL command.
     * @return True if everything went alright, false if some trouble occurred.
     */
    public boolean execAction(String act) {
        try {
            stmt = conn.createStatement();
            stmt.execute(act);
            return true;
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "Hiba: " + e.getMessage(),
                    "Hiba történet!",JOptionPane.ERROR_MESSAGE);
            System.out.println("Az adatbázismódosítást nem tudtuk végrahajtani, az ok: " + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * This method will create a new ID, what is 8 character long and contains uppercase letters and numbers.
     * @return A new ID which is a String.
     */
    public String createID(){
        int ID_LEN = 8;
        StringBuilder sb = new StringBuilder(ID_LEN);
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for(int i = 0; i < ID_LEN; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    /**
     * This method will create a new date and time String.
     * @return A String object with the date and time.
     */
    public String getCreationDate(){
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    /**
     * This method creates the Shift's starting date.
     * @return Returns the date of starting shift in form of a String object.
     */
    public String getShiftStartDate() {
        return new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
    }

    /**
     * This method creates the next days date.
     * @return A String object that references to the next days date.
     */
    public String getShiftTomorrowDate() {
        DateTime tomorrow = new DateTime().plusDays(1);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd");
        return fmt.print(tomorrow);
    }


}
