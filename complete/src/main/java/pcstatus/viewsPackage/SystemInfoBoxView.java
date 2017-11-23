package pcstatus.viewsPackage;

import javafx.scene.control.Label;
import pcstatus.dataPackage.SingletonBatteryStatus;

import java.util.Observable;
import java.util.Observer;

public class SystemInfoBoxView implements Observer {
    private Label systemInfoText;

    public SystemInfoBoxView(Label systemInfoText) {
        this.systemInfoText = systemInfoText;
        SingletonBatteryStatus.getInstance().addingObserver(SystemInfoBoxView.this);
    }

    @Override
    public void update(Observable o, Object arg) {
        systemInfoText.setText(String.join("\n", SingletonBatteryStatus.getInstance().getComputerInfo()));
    }
}
