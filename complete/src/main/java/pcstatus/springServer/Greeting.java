package pcstatus.springServer;


/**
 *  Spring uses the Jackson JSON library to automatically marshal instances of type <code>Greeting</code>into JSON.
 * @see <a href="https://spring.io/guides/gs/rest-service/" target="_blank">Building a RESTful Web Service - Spring</a>
 * @author Andrea Bravaccino
 */
public class Greeting {

    private final long id;
    private final String content;
    private final String[] batteryInfo;
    private final String[] cpuInfo;
    private final String disks;
    private final String computerInfo;
    private final String miscellaneous;
    private final Float[] numericAvaibleFileSystem;
    private final String numericCpuLoad;
    private final String numericFreeRam;
    private final String numericPercPerThread;
    private final String numericBatteryPerc;
   // private final String numericRamPerProcess;

    Greeting(long id, String content, String[] batteryInfo, String[] cpuInfo, String disks, String computerInfo, String miscellaneuos, Float[] numericAvaibleFileSystem, String numericCpuLoad, String numericFreeRam, String numericPercPerThread, String numericBatteryPerc) {
        this.id = id;
        this.content = content;
        this.batteryInfo = batteryInfo;
        this.cpuInfo = cpuInfo;
        this.disks = disks;
        this.computerInfo = computerInfo;
        this.miscellaneous = miscellaneuos;
        this.numericAvaibleFileSystem = numericAvaibleFileSystem;
        this.numericCpuLoad = numericCpuLoad;
        this.numericFreeRam = numericFreeRam;
        this.numericPercPerThread = numericPercPerThread;
        this.numericBatteryPerc = numericBatteryPerc;
        //this.numericRamPerProcess = numericRamPerProcess;
    }

   /* public String getNumericRamPerProcess() {
        return numericRamPerProcess;
    }*/

    public String getNumericCpuLoad() {
        return numericCpuLoad;
    }

    public String getNumericPercPerThread() {
        return numericPercPerThread;
    }

    public String getNumericFreeRam() {
        return numericFreeRam;
    }

    public Float[] getNumericAvaibleFileSystem() {
        return numericAvaibleFileSystem;
    }

    public String getComputerInfo() {
        return computerInfo;
    }

    public String getMiscellaneous() {
        return miscellaneous;
    }

    public String getDisks() {
        return disks;
    }

    public long getId() {
        return id;
    }

    public String[] getCpuInfo() {
        return cpuInfo;
    }

    public String[] getBatteryInfo() {
        return batteryInfo;
    }

    public String getContent() {
        return content;
    }

    public String getNumericBatteryPerc() {
        return numericBatteryPerc;
    }
}
