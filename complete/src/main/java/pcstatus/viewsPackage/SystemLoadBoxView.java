package pcstatus.viewsPackage;

import javafx.scene.control.Label;
import pcstatus.charts.LineChartClass;
import pcstatus.dataPackage.SingletonBatteryStatus;
import pcstatus.dataPackage.SingletonNumericGeneralStats;

import java.util.Observable;
import java.util.Observer;

public class SystemLoadBoxView implements Observer {

    Label systemLoadText;
    LineChartClass lineChartClass;

    public SystemLoadBoxView(Label systemLoadText, LineChartClass lineChartClass) {
        this.systemLoadText = systemLoadText;
        this.lineChartClass = lineChartClass;
        SingletonBatteryStatus.getInstance().addingObserver(SystemLoadBoxView.this);
    }

    @Override
    public void update(Observable o, Object arg) {
        systemLoadText.setText(String.join("\n", SingletonBatteryStatus.getInstance().getMiscellaneous()));
        lineChartClass.addEntryLineChart(Float.parseFloat(SingletonNumericGeneralStats.getInstance().getCpuLoad()));
    }
}
