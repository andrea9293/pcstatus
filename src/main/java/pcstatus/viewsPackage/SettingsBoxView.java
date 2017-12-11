/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
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
     * shows the local IP address
     */
    private Label ipAddressInformation;

    /**
     * shows bluetooth name
     */
    private Label bluetoothInformation;

    /**
     * shows port for server
     */
    private Label serverPortInformation;

    /**
     * the constructor initialize the parameters and register itself like <code>Observer</code>
     * @param ipAddressInformation label that will show the local IP address
     * @param bluetoothInformation label that will show bluetooth name
     * @param serverPortInformation label that will show port for server
     * @param openLibs label that will show information about open source libraries used
     */
    public SettingsBoxView(Label ipAddressInformation, Label bluetoothInformation, Label serverPortInformation, Label openLibs) {
        this.ipAddressInformation = ipAddressInformation;
        this.bluetoothInformation = bluetoothInformation;
        this.serverPortInformation = serverPortInformation;
        openLibs.setText("This program is licensed under the GNU AGPLv3 or later.\n" +
                "PC-status uses the following open source libraries:\n\n" +
                "Bluecove\n" +
                "OSHI-core\n" +
                "Sigar-lib\n" +
                "SpringFramework\n" +
                "Android-Json\n");
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
    }
}
