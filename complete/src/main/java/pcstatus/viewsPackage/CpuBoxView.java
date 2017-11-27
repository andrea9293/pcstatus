package pcstatus.viewsPackage;

import javafx.scene.control.Label;
import pcstatus.charts.MultipleLineChartClass;
import pcstatus.dataPackage.SingletonDynamicGeneralStats;
import pcstatus.dataPackage.SingletonStaticGeneralStats;

import java.util.Observable;
import java.util.Observer;

/**
 * This class manages the view dedicated for CPU information implementing <code>Observer</code>
 * @see java.util.Observer
 * @author Andrea Bravaccino
 */
public class CpuBoxView implements Observer {

    /**
     * label containing information about CPU
     */
    private Label cpuText;

    /**
     * type of chart showing CPU threads load
     */
    private MultipleLineChartClass multipleLineChartClass;

    /**
     * the constructor initialize the parameters and register itself like <code>Observer</code>
     * @param cpuText label that will contain information about CPU
     * @param multipleLineChartClass chart that will show CPU threads load
     */
    public CpuBoxView(Label cpuText, MultipleLineChartClass multipleLineChartClass) {
        this.cpuText = cpuText;
        this.multipleLineChartClass = multipleLineChartClass;
        SingletonStaticGeneralStats.getInstance().addingObserver(CpuBoxView.this);
    }

    /**
     * method updating view with new data
     * @see Observer#update(Observable, Object)
     * @param o not used
     * @param arg not used
     */
    @Override
    public void update(Observable o, Object arg) {
        StringBuilder cpuInfo = new StringBuilder();
        cpuInfo.append("Vendor: ").append(SingletonStaticGeneralStats.getInstance().getCpuInfo()[0]).append("\n");
        cpuInfo.append(SingletonStaticGeneralStats.getInstance().getCpuInfo()[1]).append("\n");
        cpuInfo.append("Clock: ").append(SingletonStaticGeneralStats.getInstance().getCpuInfo()[2]).append("\n");
        cpuInfo.append("Physical CPU(s): ").append(SingletonStaticGeneralStats.getInstance().getCpuInfo()[3]).append("\n");
        cpuInfo.append("Logical CPU(s): ").append(SingletonStaticGeneralStats.getInstance().getCpuInfo()[4]).append("\n");
        cpuInfo.append("CPU load: ").append(SingletonStaticGeneralStats.getInstance().getCpuInfo()[5]).append("%");

        cpuText.setText(String.join("\n", cpuInfo));

        if (SingletonStaticGeneralStats.getInstance().isFirtShow()) {
            multipleLineChartClass.createSeries();
            multipleLineChartClass.addEntryLineChart(SingletonDynamicGeneralStats.getInstance().getPercPerThread());
        } else {
            multipleLineChartClass.addEntryLineChart(SingletonDynamicGeneralStats.getInstance().getPercPerThread());
        }
    }
}
