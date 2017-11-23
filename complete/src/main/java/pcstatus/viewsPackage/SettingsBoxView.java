package pcstatus.viewsPackage;

import javafx.scene.control.Label;
import pcstatus.dataPackage.SingletonBatteryStatus;

import java.util.Observable;
import java.util.Observer;

public class SettingsBoxView implements Observer{

    private Label ipAddressInformation;
    private Label bluetoothInformation;
    private Label serverPortInformation;

    public SettingsBoxView(Label ipAddressInformation, Label bluetoothInformation, Label serverPortInformation) {
        this.ipAddressInformation = ipAddressInformation;
        this.bluetoothInformation = bluetoothInformation;
        this.serverPortInformation = serverPortInformation;
        SingletonBatteryStatus.getInstance().addingObserver(SettingsBoxView.this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(SingletonBatteryStatus.getInstance().isServerCreated()){
            serverPortInformation.setText(String.valueOf(SingletonBatteryStatus.getInstance().getPort()));
            ipAddressInformation.setText(SingletonBatteryStatus.getInstance().getIpAddress());
            bluetoothInformation.setText(SingletonBatteryStatus.getInstance().getBluetoothName());
        }
        //bluetoothInformation.setText();
    }

}
