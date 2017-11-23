package pcstatus.dataPackage;

import javafx.application.Platform;
import org.json.JSONException;
import pcstatus.ServerBatteryMain;

import java.util.Observable;
import java.util.Observer;

public class SingletonBatteryStatus extends Observable{
    private static SingletonBatteryStatus ourInstance = new SingletonBatteryStatus();
    private boolean firtShow;
    private boolean isServerCreated;
    private String[] battery;
    private String[] cpu;
    private String[] disks;
    private String jsonStr;
    private String[] computerInfo;
    private String[] miscellaneous;
    private Float[] percPerThread;
    private String[] avaibleFileSystem;
    private Integer batteryPerc;
    private int port;
    private String bluetoothName;
    private String ipAddress;
    private boolean bluetoothServerCreated = true;

    public static SingletonBatteryStatus getInstance() {
        return ourInstance;
    }
    private SingletonBatteryStatus() {}

    public void setJsonStr(String jsonStr) throws JSONException {
        this.jsonStr = jsonStr;
        new jsonParser(jsonStr);
    }

    public boolean isServerCreated() {
        return isServerCreated;
    }

    public void setServerCreated(boolean serverCreated) {
        isServerCreated = serverCreated;
    }

    public String[] getDisks() {
        return disks;
    }

    public String[] getCpu() {
        return cpu;
    }

    public void setCpu(String[] cpu) {
        this.cpu = cpu;
    }

    public void setDisks(String[] disks) {
        this.disks = disks;
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public void setBattery(String[] s){
        battery = s;
    }

    public String[] getBattery() {
        return battery;
    }

    public void setComputerInfo(String[] strings) {
        this.computerInfo = strings;
    }

    public String[] getComputerInfo() {
        return computerInfo;
    }

   /* public void addingObserver(ServerBatteryMain serverBatteryMain){
        addObserver(serverBatteryMain);
    }*/

    public void addingObserver(Observer observer){
        addObserver(observer);
    }

    public void notifyMyObservers() {

        Platform.runLater(() -> {
            setChanged();
            notifyObservers();
        });
    }

    public void setMiscellaneous(String[] strings) {
        miscellaneous = strings;
    }

    public String[] getMiscellaneous() {
        return miscellaneous;
    }

    public void setPercPerThread(String percPerThread) {
        String[] tmpStr = percPerThread.split("\n");
        Float[] tmpFlo = new Float[tmpStr.length];
        for (int i = 0; i < tmpStr.length; i++) {
            tmpFlo[i] = Float.valueOf(tmpStr[i]);
        }
        this.percPerThread=tmpFlo;
    }

    public Float[] getPercPerThread() {
        return percPerThread;
    }

    public void setAvaibleFileSystem(String[] avaibleFileSystem) {
        this.avaibleFileSystem = avaibleFileSystem;
    }

    public String[] getAvaibleFileSystem() {
        return avaibleFileSystem;
    }

    public void setBatteryPerc(String batteryPerc) {
        this.batteryPerc = Integer.parseInt(batteryPerc);
    }

    public Integer getBatteryPerc() {
        return batteryPerc;
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
}
