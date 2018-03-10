/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus.viewsPackage;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import pcstatus.Controller;
import pcstatus.dataPackage.SingletonStaticGeneralStats;

import java.util.Observable;
import java.util.Observer;

/**
 * This class manages the view dedicated for information about the program implementing <code>Observer</code>
 *
 * @author Andrea Bravaccino
 * @see java.util.Observer
 */
public class SettingsBoxView implements Observer {
    /**
     * shows bluetooth name
     */
    private Label bluetoothInformation;
    private ImageView qrImageView;
    private VBox settingVBox;

    /**
     * the constructor initialize the parameters and register itself like <code>Observer</code>
     *
     * @param qrImageView          image with qr
     * @param bluetoothInformation label that will show bluetooth name
     * @param openLibs             label that will show information about open source libraries used
     * @param settingVBox          VBox with qrImageView
     */
    public SettingsBoxView(ImageView qrImageView, Label bluetoothInformation, Label openLibs, VBox settingVBox) {
        this.qrImageView = qrImageView;
        this.settingVBox = settingVBox;
        this.bluetoothInformation = bluetoothInformation;
        openLibs.setText("This program is licensed under the GNU AGPLv3 or later.\n\n" +
                "PC-status uses the following open source libraries:\n" +
                "Bluecove, " +
                "Sigar-lib, " +
                "SpringFramework, " +
                "Android-Json, " +
                "Zxing");
        SingletonStaticGeneralStats.getInstance().addingObserver(SettingsBoxView.this);
    }

    /**
     * method updating view with new data
     *
     * @param o   not used
     * @param arg not used
     * @see Observer#update(Observable, Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        if (SingletonStaticGeneralStats.getInstance().isServerCreated()) {
            String url = SingletonStaticGeneralStats.getInstance().getIpAddress() + ":" + SingletonStaticGeneralStats.getInstance().getPort();
            qrImageView.setImage(Controller.createQR(url));
            qrImageView.setVisible(true);

            if (settingVBox.getChildren().get(1) instanceof Label) {
                settingVBox.getChildren().remove(1);
                settingVBox.getChildren().add(qrImageView);
            }

            bluetoothInformation.setText(SingletonStaticGeneralStats.getInstance().getBluetoothName());
        } else {
            if (settingVBox.getChildren().get(1) instanceof ImageView) {
                settingVBox.getChildren().remove(qrImageView);
                settingVBox.getChildren().add(new Label("Server not created"));
            }
        }
    }
}
