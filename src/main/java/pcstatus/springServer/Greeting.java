package pcstatus.springServer;


public class Greeting {

    private final long id;
    private final String content;
    private final String[] batteryInfo;
    private final String[] cpuInfo;
    private final String[] networkSpeed;
    private final String disks;

    public Greeting(long id, String content, String[] batteryInfo, String[] cpuInfo, String[] networkSpeed, String disks) {
        this.id = id;
        this.content = content;
        this.batteryInfo = batteryInfo;
        this.cpuInfo = cpuInfo;
        this.networkSpeed=networkSpeed;
        this.disks = disks;
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
