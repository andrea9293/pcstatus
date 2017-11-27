package pcstatus.viewsPackage;

import javafx.scene.control.Label;
import pcstatus.dataPackage.SingletonStaticGeneralStats;

import java.util.Observable;
import java.util.Observer;

/**
 * This class manages the view dedicated for information about the program implementing <code>Observer</code>
 * @see java.util.Observer
 * @author Andrea Bravaccino
 */
public class SettingsBoxView implements Observer{

    /**
     * show the local IP address
     */
    private Label ipAddressInformation;

    /**
     * show bluetooth name
     */
    private Label bluetoothInformation;

    /**
     * show port for server
     */
    private Label serverPortInformation;

    /**
     * the constructor initialize the parameters and register itself like <code>Observer</code>
     * @param ipAddressInformation label that will show the local IP address
     * @param bluetoothInformation label that will show bluetooth name
     * @param serverPortInformation label that will show port for server
     */
    public SettingsBoxView(Label ipAddressInformation, Label bluetoothInformation, Label serverPortInformation) {
        this.ipAddressInformation = ipAddressInformation;
        this.bluetoothInformation = bluetoothInformation;
        this.serverPortInformation = serverPortInformation;
        SingletonStaticGeneralStats.getInstance().addingObserver(SettingsBoxView.this);
    }

    /**
     * method updating view with new data
     * @see Observer#update(Observable, Object)
     * @param o not used
     * @param arg not used
     */
    @Override
    public void update(Observable o, Object arg) {
        if(SingletonStaticGeneralStats.getInstance().isServerCreated()){
            serverPortInformation.setText(String.valueOf(SingletonStaticGeneralStats.getInstance().getPort()));
            ipAddressInformation.setText(SingletonStaticGeneralStats.getInstance().getIpAddress());
            bluetoothInformation.setText(SingletonStaticGeneralStats.getInstance().getBluetoothName());
        }
        //bluetoothInformation.setText();
    }

}
