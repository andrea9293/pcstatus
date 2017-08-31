package pcstatus.dataPackage;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

import java.util.ArrayList;
import java.util.List;

public interface Kernel32 extends StdCallLibrary {

    Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("Kernel32", Kernel32.class);

    /**
     * see http://msdn2.microsoft.com/en-us/library/aa373232.aspx
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
            sb.append("     " + "AC status: " + getACLineStatusString() + "\n");
            sb.append("     " + "Battery percentage: " + getBatteryLifePercent() + "\n");
            sb.append("     " + "Remaining battery: " + getHours(getBatteryLifeTime()) + "\n");
            return sb.toString();
        }

        private String getHours(String seconds){
            if (seconds.equals("Unknown")){
                return seconds;
            }else {
                Integer totalSecs = Integer.parseInt(seconds);
                Integer hours = totalSecs / 3600;
                Integer minutes = (totalSecs % 3600) / 60;
                //Integer secondi = totalSecs % 60;

                return String.format("%02d h %02d min", hours, minutes);
            }
        }
    }



    /**
     * Fill the structure.
     */
    int GetSystemPowerStatus(SYSTEM_POWER_STATUS result);
}
