package pcstatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class jsonParser {
    private String[] batteryInfo;
    private String[] cpuInfo;
    private String[] networkSpeed;
    private String disks;
    private String miscellaneous;

    public jsonParser(String jsonStr) throws JSONException {
        JSONObject jsonObj;
        String[] strings;
        jsonObj = new JSONObject(jsonStr); //assegnazione della stringa ad un oggetto JSONObject
        JSONArray jsonArray = jsonObj.getJSONArray("batteryInfo");
        strings = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            strings[i] = jsonArray.getString(i);
        }
        SingletonBatteryStatus.getInstance().setBattery(strings);

        jsonArray = jsonObj.getJSONArray("cpuInfo");
        strings = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            strings[i] = jsonArray.getString(i);
        }
        SingletonBatteryStatus.getInstance().setCpu(strings);

        strings = jsonObj.getString("disks").split("\n");
        SingletonBatteryStatus.getInstance().setDisks(strings);

        strings = jsonObj.getString("computerInfo").split("\n");
        SingletonBatteryStatus.getInstance().setComputerInfo(strings);

        strings = jsonObj.getString("miscellaneous").split("\n");
        SingletonBatteryStatus.getInstance().setMiscellaneous(strings);

        SingletonBatteryStatus.getInstance().notifyMyObservers();
    }

    public void setDisks(String disks) {
        this.disks = disks;
    }

    public String getDisks() {
        return disks;
    }

    public void setBatteryInfo(String[] batteryInfo) {
        this.batteryInfo = batteryInfo;
    }

    public String[] getNetworkSpeed() {
        return networkSpeed;
    }

    public void setCpuInfo(String[] cpuInfo) {
        this.cpuInfo = cpuInfo;
    }

    public void setNetworkSpeed(String[] networkSpeed) {
        this.networkSpeed = networkSpeed;
    }

    public String[] getCpuInfo() {
        return cpuInfo;
    }

    public String[] getBatteryInfo() {
        return batteryInfo;
    }
}


