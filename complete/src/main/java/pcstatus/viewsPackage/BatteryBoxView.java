package pcstatus.viewsPackage;

import javafx.scene.control.Label;
import pcstatus.charts.StackedAreaChartClass;
import pcstatus.dataPackage.SingletonBatteryStatus;

import java.util.Observable;
import java.util.Observer;

public class BatteryBoxView implements Observer {

    private Label batteryText;
    private StackedAreaChartClass stackedAreaChartClass;

    public BatteryBoxView(Label batteryText, StackedAreaChartClass stackedAreaChartClass) {
        this.batteryText = batteryText;
        this.stackedAreaChartClass = stackedAreaChartClass;
        SingletonBatteryStatus.getInstance().addingObserver(BatteryBoxView.this);
    }

    @Override
    public void update(Observable o, Object arg) {
        batteryText.setText(String.join("\n", SingletonBatteryStatus.getInstance().getBattery()));

        if (SingletonBatteryStatus.getInstance().getBatteryPerc() != null)
            stackedAreaChartClass.addEntryStackedAreaChart(SingletonBatteryStatus.getInstance().getBatteryPerc());

    }
}
