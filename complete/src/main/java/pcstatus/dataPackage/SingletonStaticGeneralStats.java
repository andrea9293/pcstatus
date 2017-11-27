package pcstatus.dataPackage;

import javafx.application.Platform;
import org.json.JSONException;

import java.util.Observable;
import java.util.Observer;

/**
 * This singleton class is a model containing all static information (model of PC, OS, bluetooth name of PC, ...) and
 * some useful variables for the status of the program extending <code>Observables</code>
 *
 * @see java.util.Observable
 * @author Andrea Bravaccino
 */
public class SingletonStaticGeneralStats extends Observable{
    /**
     * Intstance of singleton
     */
    private static SingletonStaticGeneralStats ourInstance = new SingletonStaticGeneralStats();

    /**
     * getter for current instance of <code>SingletonStaticGeneralStats</code>
     * @return current instance of <code>SingletonStaticGeneralStats</code>
     */
    public static SingletonStaticGeneralStats getInstance() {
        return ourInstance;
    }

    /**
     * empty constructor
     */
    private SingletonStaticGeneralStats() {}

    /**
     * this boolean variable indicates if the program is showing data for the first time
     */
    private boolean firtShow;
    /**
     * this boolean variable indicates if the program has created the server
     */
    private boolean isServerCreated;
    /**
     * string containing the json downloaded from server
     */
    private String jsonStr;
    /**
     * array of strings containing computer information
     */
    private String[] computerInfo;
    /**
     * array of string containing various information about computer performance
     */
    private String[] miscellaneous;
    /**
     * int indicating server port
     */
    private int port;
    /**
     * bluetooth name of computer
     */
    private String bluetoothName;
    /**
     * local IP address
     */
    private String ipAddress;
    /**
     * this boolean variable indicates if the program has created the bluetooth server
     */
    private boolean bluetoothServerCreated = false;
    /**
     * * array of string containing various information about computer
     */
    private String systemInformation;


    public void setJsonStr(String jsonStr) throws JSONException {
        this.jsonStr = jsonStr;
        new JsonParser(jsonStr);
    }

    void setCpuInfo(String[] cpuInfo) {
        this.cpuInfo = cpuInfo;
    }

    public String[] getCpuInfo() {
        return cpuInfo;
    }
    private String[] cpuInfo;

    void setSystemInformation(String systemInformation) {
        this.systemInformation = systemInformation;
    }

    public String getSystemInformation() {
        return systemInformation;
    }

    public boolean isServerCreated() {
        return isServerCreated;
    }

    public void setServerCreated(boolean serverCreated) {
        isServerCreated = serverCreated;
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public void setComputerInfo(String[] strings) {
        this.computerInfo = strings;
    }

    public String[] getComputerInfo() {
        return computerInfo;
    }

    public void setMiscellaneous(String[] strings) {
        miscellaneous = strings;
    }

    public String[] getMiscellaneous() {
        return miscellaneous;
    }

    public void setFirstShow(boolean firstShow){
        this.firtShow = firstShow;
    }

    public boolean isFirtShow() {
        return firtShow;
    }

    public void setPort (int port){
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public String getBluetoothName() {
        return bluetoothName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setBluetoothServerCreated(boolean bluetoothServerCreated) {
        this.bluetoothServerCreated = bluetoothServerCreated;
    }

    public boolean isBluetoothServerCreated() {
        return bluetoothServerCreated;
    }

    /**
     * add an observer to list
     * @see Observer
     * @param observer view to add
     */
    public void addingObserver(Observer observer){
        addObserver(observer);
    }

    /**
     * send notify to all observers
     * @see Observable#notifyObservers()
     */
    public void notifyMyObservers() {

        Platform.runLater(() -> {
            setChanged();
            notifyObservers();
        });
    }
}
