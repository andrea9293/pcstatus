package pcstatus;

import javafx.application.Platform;
import org.json.JSONException;

import java.util.Observable;

public class SingletonBatteryStatus extends Observable{
    private static SingletonBatteryStatus ourInstance = new SingletonBatteryStatus();

    public static SingletonBatteryStatus getInstance() {
        return ourInstance;
    }

    private String[] battery;
    private String[] cpu;
    private String[] disks;
    private String jsonStr;
    private String[] computerInfo;
    private String[] miscellaneous;


    private SingletonBatteryStatus() {}

    void setJsonStr(String jsonStr) throws JSONException {
        this.jsonStr = jsonStr;
        new jsonParser (jsonStr);
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
        //System.out.println("modificato il valore in\n " + s);
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

    void addingObserver(ServerBatteryMain serverBatteryMain){
        addObserver(serverBatteryMain);
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
}
