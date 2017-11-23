package pcstatus.viewsPackage;

import javafx.scene.control.Label;
import pcstatus.charts.PieChartClass;
import pcstatus.dataPackage.SingletonBatteryStatus;

import java.util.Observable;
import java.util.Observer;

public class DisksBoxView implements Observer {

    private Label disksText;
    private PieChartClass pieChartClass;

    public DisksBoxView(Label disksText, PieChartClass pieChartClass) {
        this.disksText = disksText;
        this.pieChartClass = pieChartClass;
        SingletonBatteryStatus.getInstance().addingObserver(DisksBoxView.this);
    }

    @Override
    public void update(Observable o, Object arg) {
        disksText.setText(String.join("\n", SingletonBatteryStatus.getInstance().getDisks()));
        pieChartClass.addEntryPieChart(SingletonBatteryStatus.getInstance().getAvaibleFileSystem());
    }
}
