package classes;

import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;

import java.util.Calendar;

/**
 *  This class purpose is to validate dates, which are aquired from datepickers.
 * The validation checks if the dates are equals or the starting date was later than the ending date.
 * If it finds some problem with the input dates logic, it will warn the user.
 */
public class DatePickerUtil {

    /**
     * A DatePicker instance that will used in the walidation process
     */
    private DatePicker picker1;

    /**
     * A DatePicker instance that will used in the walidation process
     */
    private DatePicker picker2;

    public DatePickerUtil() {
    }

    /**
     * The method takes two DatePicker instances and checks if they are equals or logically not consistent.
     * If tyey are not, the method will alert the user about the case.
     * @param picker1 A DatePicker instance which should be the starting date
     * @param picker2 A DatePicker Instance which should be the ending date
     * @return A boolean which is false if the starting date is equal or was later than the ending date, true if the dates are consistent
     */
    public static boolean validatePeriodDates(DatePicker picker1, DatePicker picker2) {
        if(picker1.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Szükséges egy kezdő dátum kiválasztása!\n\nPróbáld újra!");
            alert.showAndWait();

            setPickerValuesToNull(picker1, picker2);

            return false;
        }


        Calendar periodStart = Calendar.getInstance();
        periodStart.set(Calendar.DAY_OF_MONTH, picker1.getValue().getDayOfMonth());
        periodStart.set(Calendar.MONTH, picker1.getValue().getMonthValue());
        periodStart.set(Calendar.YEAR, picker1.getValue().getYear());
        long periodStartToMili = periodStart.getTimeInMillis();


        if(picker2.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Szükséges egy végső dátum kiválasztása!\n\nPróbáld újra!");
            alert.showAndWait();

            setPickerValuesToNull(picker1, picker2);

            return false;
        }


        Calendar periodEnd = Calendar.getInstance();
        periodEnd.set(Calendar.DAY_OF_MONTH, picker2.getValue().getDayOfMonth());
        periodEnd.set(Calendar.MONTH, picker2.getValue().getMonthValue());
        periodEnd.set(Calendar.YEAR, picker2.getValue().getYear());
        long periodEndToMili = periodEnd.getTimeInMillis();



        if(periodEndToMili <= periodStartToMili) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Nem lehet a végső dátum kisebb, sem egyenlő a kezdő dátummal!\n\nPróbáld újra!");
            alert.showAndWait();

            setPickerValuesToNull(picker1, picker2);

            return false;
        }

        return true;
    }

    /**
     * If the validatePeriodDates method has a false return, than this method will clear the DatePicker values
     * @param picker1 A DatePicker instance which should be the starting date
     * @param picker2 A DatePicker Instance which should be the ending date
     */
    public static void setPickerValuesToNull(DatePicker picker1, DatePicker picker2) {
        picker1.setValue(null);
        picker2.setValue(null);
    }


}
