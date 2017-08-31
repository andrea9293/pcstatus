package pcstatus.springServer;


public class Greeting {

    private final long id;
    private final String content;
    private final String[] batteryInfo;
    private final String[] cpuInfo;
    private final String[] networkSpeed;
    private final String disks;
    private final String computerInfo;
    private final String miscellaneous;

    public Greeting(long id, String content, String[] batteryInfo, String[] cpuInfo, String[] networkSpeed, String disks, String computerInfo, String miscellaneuos) {
        this.id = id;
        this.content = content;
        this.batteryInfo = batteryInfo;
        this.cpuInfo = cpuInfo;
        this.networkSpeed=networkSpeed;
        this.disks = disks;
        this.computerInfo = computerInfo;
        this.miscellaneous = miscellaneuos;
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

    public String[] getNetworkSpeed() {
        return networkSpeed;
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
}
