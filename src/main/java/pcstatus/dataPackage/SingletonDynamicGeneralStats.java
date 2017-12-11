/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus.dataPackage;

/**
 * This singleton class is the model for dynamic data. Contain only setter and getter
 *
 * @author Andrea Bravaccino
 */
public class SingletonDynamicGeneralStats {
    /**
     * Intstance of singleton
     */
    private static SingletonDynamicGeneralStats ourInstance = new SingletonDynamicGeneralStats();

    /**
     * getter for current instance of <code>SingletonDynamicGeneralStats</code>
     * @return current instance of <code>SingletonDynamicGeneralStats</code>
     */
    public static SingletonDynamicGeneralStats getInstance() {
        return ourInstance;
    }

    /**
     * empty constructor
     */
    private SingletonDynamicGeneralStats() { }

    /**
     * Float for CPU load
     */
    private Float cpuLoad;
    /**
     * Float array containing load percentage for each CPU threads
     */
    private Float[] percPerThread;
    /**
     * Float to indicate free RAM
     */
    private Float freeRam;
    /**
     * a string containing information about all filesystems founded with total and free size, label, and type of device
     */
    private Float[] avaibleFileSystem;
    /**
     * Integer to indicate percentage of battery
     */
    private Integer batteryPerc;
    /**
     * Array of strings with information about disks
     */
    private String[] disks;
    /**
     * Array of strings with information about battery
     */
    private String[] battery;
    //private String ramPerProcess = null;

    /**
     * setter for CPU load
     * @param cpuLoad is percentage of cpu load
     */
    void setCpuLoad(Float cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    /**
     * getter for CPU load
     * @return is percentage of cpu load
     */
    public Float getCpuLoad() {
        return cpuLoad;
    }

    /**
     * setter for free RAM
     * @param freeRam free RAM size
     */
    void setFreeRam(Float freeRam) {
        this.freeRam = freeRam;
    }

    /**
     * getter for free RAM
     * @return free RAM size
     */
    public String getFreeRam() {
        if(freeRam != null)
            return freeRam.toString();
        else
            return "";
    }


    /**
     * setter for battery percentage
     * @param batteryPerc is percentage of battery
     */
    void setBatteryPerc(String batteryPerc) {
        this.batteryPerc = Integer.parseInt(batteryPerc);
    }

    /**
     * setter for battery percentage
     * @return is percentage of battery
     */
    public Integer getBatteryPerc() {
        return batteryPerc;
    }

    /**
     * getter for information about available space in filesystems
     * @return array of Float containing available space in filesystems
     */
    public Float[] getAvaibleFileSystem() {
        return avaibleFileSystem;
    }

    /**
     * setter for information about available space in filesystems
     * @param avaibleFileSystem array of Float containing available space in filesystems
     */
    void setAvaibleFileSystem(Float[] avaibleFileSystem) {
        this.avaibleFileSystem = avaibleFileSystem;
    }

    /**
     * setter for information about load percentage for each thread of CPU
     * @param percPerThread an array of Float containing load percentage for each thread of CPU
     */
    void setPercPerThread(Float[] percPerThread) {
        this.percPerThread = percPerThread;
    }

    /**
     * getter for information about load percentage for each thread of CPU
     * @return an array of Float containing load percentage for each thread of CPU
     */
    public Float[] getPercPerThread() {
        return percPerThread;
    }

    /**
     * getter for variable containing information about all disks
     * @return Array of strings with information about disks
     */
    public String[] getDisks() {
        return disks;
    }

    /**
     * setter for variable containing information about all disks
     * @param disks Array of strings with information about disks
     */
    public void setDisks(String[] disks) {
        this.disks = disks;
    }

    /**
     * setter for information about battery
     * @param battery an array of strings containing information about battery
     */
    void setBattery(String[] battery){
        this.battery = battery;
    }

    /**
     * getter for information about battery
     * @return an array of strings containing information about battery
     */
    public String[] getBattery() {
        return battery;
    }

    /*public void setRamPerProcess(String ramPerProcess) {
        this.ramPerProcess = ramPerProcess;
    }

    public String getRamPerProcess() {
        return ramPerProcess;
    }*/
}
