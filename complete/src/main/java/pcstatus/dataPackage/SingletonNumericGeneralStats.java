package pcstatus.dataPackage;

public class SingletonNumericGeneralStats {
    private static SingletonNumericGeneralStats ourInstance = new SingletonNumericGeneralStats();

    public static SingletonNumericGeneralStats getInstance() {
        return ourInstance;
    }

    private SingletonNumericGeneralStats() {
    }

    private String cpuLoad;
    private String percPerThread;
    private Float percRam;
    private String[] avaibleFileSystem;
    private String systemInformation;
    private String[] cpuInfo;
    //private String ramPerProcess = null;

    public void setCpuLoad(String cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public String getCpuLoad() {
        return cpuLoad;
    }

    public void setFreeRam(long available, long tot) {
        percRam = (((float)available / (float) tot) * 100);
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

    public void setPercPerThread(String percPerThread) {
        this.percPerThread = percPerThread;
    }

    public String getPercPerThread() {
        return percPerThread;
    }

    public void setSystemInformation(String systemInformation) {
        this.systemInformation = systemInformation;
    }

    public String getSystemInformation() {
        return systemInformation;
    }

    public void setCpuInfo(String[] cpuInfo) {
        this.cpuInfo = cpuInfo;
    }

    public String[] getCpuInfo() {
        return cpuInfo;
    }

    /*public void setRamPerProcess(String ramPerProcess) {
        this.ramPerProcess = ramPerProcess;
    }

    public String getRamPerProcess() {
        return ramPerProcess;
    }*/
}
