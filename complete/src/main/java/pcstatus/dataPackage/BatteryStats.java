/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus.dataPackage;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

import java.util.ArrayList;
import java.util.List;

/**
 * class for power Management. This class retrieves information about battery and set them in model
 */

public class BatteryStats{

    /**
     * Intstance of singleton
     */
    private static BatteryStats ourInstance = new BatteryStats();

    /**
     * empty constructor
     */
    private BatteryStats(){}

    /**
     * function to return the instance
     * @return current instance of <code>CPUStats</code>
     */
    public static BatteryStats getInstance(){
        return ourInstance;
    }

    /**
     * retrieve informations about battery
     * @return a string with information about battery (AC status, battery percentage, remaining battery) separated by a "\n"
     */
    public String[] getBatteryStats(){
        Kernel32.SYSTEM_POWER_STATUS batteryStatus = new Kernel32.SYSTEM_POWER_STATUS();
        Kernel32.INSTANCE.GetSystemPowerStatus(batteryStatus);
        setBatteryInModel(batteryStatus.toString().split("\n"));
        batteryStatus.toString().split("\n");
        return batteryStatus.toString().split("\n");
    }

    /**
     * this function set information about battery in model
     * @param battery an array of strings containing information about battery
     */
    private void setBatteryInModel(String[] battery){
        SingletonDynamicGeneralStats.getInstance().setBatteryPerc(battery[1].replaceAll("[^0-9]", ""));
        SingletonDynamicGeneralStats.getInstance().setBattery(battery);
    }

    /**
     * @see <a href="https://stackoverflow.com/a/3434962/1613867" target="_blank">Get Windows Charging Status in Java - Stackoverflow</a>
     */
    public interface Kernel32 extends StdCallLibrary {

        Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("Kernel32", Kernel32.class);

        /**
         * @see <a href="http://msdn2.microsoft.com/en-us/library/aa373232.aspx" target="_blank">http://msdn2.microsoft.com/en-us/library/aa373232.aspx</a>
         */
        class SYSTEM_POWER_STATUS extends Structure {
            public byte ACLineStatus;
            public byte BatteryFlag;
            public byte BatteryLifePercent;
            public byte Reserved1;
            public int BatteryLifeTime;
            public int BatteryFullLifeTime;

            @Override
            protected List<String> getFieldOrder() {
                ArrayList<String> fields = new ArrayList<String>();
                fields.add("ACLineStatus");
                fields.add("BatteryFlag");
                fields.add("BatteryLifePercent");
                fields.add("Reserved1");
                fields.add("BatteryLifeTime");
                fields.add("BatteryFullLifeTime");
                return fields;
            }

            /**
             * The AC power status
             */
            public String getACLineStatusString() {
                switch (ACLineStatus) {
                    case (0): return "Offline";
                    case (1): return "Online";
                    default: return "Unknown";
                }
            }

            /**
             * The battery charge status
             */
            public String getBatteryFlagString() {
                switch (BatteryFlag) {
                    case (1): return "High, more than 66 percent";
                    case (2): return "Low, less than 33 percent";
                    case (4): return "Critical, less than five percent";
                    case (8): return "Charging";
                    case ((byte) 128): return "No system battery";
                    default: return "Unknown";
                }
            }

            /**
             * The percentage of full battery charge remaining
             */
            public String getBatteryLifePercent() {
                return (BatteryLifePercent == (byte) 255) ? "Unknown" : BatteryLifePercent + "%";
            }

            /**
             * The number of seconds of battery life remaining
             */
            public String getBatteryLifeTime() {
                return (BatteryLifeTime == -1) ? "Unknown" : BatteryLifeTime + "";
            }

            /**
             * The number of seconds of battery life when at full charge
             */
            public String getBatteryFullLifeTime() {
                return (BatteryFullLifeTime == -1) ? "Unknown" : BatteryFullLifeTime + "seconds";
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("AC status: ").append(getACLineStatusString()).append("\n");
                sb.append("Battery percentage: ").append(getBatteryLifePercent()).append("\n");
                sb.append("Remaining battery: ").append(getHours(getBatteryLifeTime())).append("\n");
                return sb.toString();
            }

            private String getHours(String seconds){
                if (seconds.equals("Unknown")){
                    return seconds;
                }else {
                    Integer totalSecs = Integer.parseInt(seconds);
                    Integer hours = totalSecs / 3600;
                    Integer minutes = (totalSecs % 3600) / 60;
                    //Integer seconds = totalSecs % 60;

                    return String.format("%02d h %02d min", hours, minutes);
                }
            }
        }



        /**
         * Fill the structure.
         */
        int GetSystemPowerStatus(SYSTEM_POWER_STATUS result);
    }

}