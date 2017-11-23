package pcstatus.viewsPackage;

import javafx.scene.control.Label;
import pcstatus.charts.MultipleLineChartClass;
import pcstatus.dataPackage.SingletonBatteryStatus;

import java.util.Observable;
import java.util.Observer;

public class CpuBoxView implements Observer {

    private Label cpuText;
    private MultipleLineChartClass multipleLineChartClass;

    public CpuBoxView(Label cpuText, MultipleLineChartClass multipleLineChartClass) {
        this.cpuText = cpuText;
        this.multipleLineChartClass = multipleLineChartClass;
        SingletonBatteryStatus.getInstance().addingObserver(CpuBoxView.this);
    }

    @Override
    public void update(Observable o, Object arg) {
        cpuText.setText(String.join("\n", SingletonBatteryStatus.getInstance().getCpu()));

        if (SingletonBatteryStatus.getInstance().isFirtShow()) {
            multipleLineChartClass.createSeries();
            multipleLineChartClass.addEntryLineChart(SingletonBatteryStatus.getInstance().getPercPerThread());
        } else {
            multipleLineChartClass.addEntryLineChart(SingletonBatteryStatus.getInstance().getPercPerThread());
        }
    }
}
