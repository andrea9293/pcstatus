package pcstatus.dataPackage;

public class SingletonNumericGeneralStats {
    private static SingletonNumericGeneralStats ourInstance = new SingletonNumericGeneralStats();

    public static SingletonNumericGeneralStats getInstance() {
        return ourInstance;
    }

    private SingletonNumericGeneralStats() {
    }

    private String cpuLoad;
    private Float percRam;
    private String[] avaibleFileSystem;

    public void setCpuLoad(String cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public String getCpuLoad() {
        return cpuLoad;
    }

    public void setFreeRam(long available, long tot) {
        percRam = (((float)available / (float) tot) * 100);
        System.out.println("stampo freeram" + percRam + " " + (float) available + " " + tot);
    }

    public String getFreeRam() {
        return GeneralStats.round(percRam,2);
    }

    public String[] getAvaibleFileSystem() {
        return avaibleFileSystem;
    }

    public void setAvaibleFileSystem(String[] avaibleFileSystem) {
        this.avaibleFileSystem = avaibleFileSystem;
    }
}
