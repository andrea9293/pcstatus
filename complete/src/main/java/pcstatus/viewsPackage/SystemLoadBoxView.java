package pcstatus.viewsPackage;

import javafx.scene.control.Label;
import pcstatus.charts.LineChartClass;
import pcstatus.dataPackage.SingletonDynamicGeneralStats;
import pcstatus.dataPackage.SingletonStaticGeneralStats;

import java.util.Observable;
import java.util.Observer;

/**
 * This class manages the view dedicated for system performance information implementing <code>Observer</code>
 * @see java.util.Observer
 * @author Andrea Bravaccino
 */
public class SystemLoadBoxView implements Observer {

    /**
     * label containing information about system performance
     */
    private Label systemLoadText;

    /**
     * type of chart showing CPU load
     */
    private LineChartClass lineChartClass;

    /**
     * the constructor initialize the parameters and register itself like <code>Observer</code>
     * @param systemLoadText label that will contain information about system performance
     * @param lineChartClass chart that will show  CPU load
     */
    public SystemLoadBoxView(Label systemLoadText, LineChartClass lineChartClass) {
        this.systemLoadText = systemLoadText;
        this.lineChartClass = lineChartClass;
        SingletonStaticGeneralStats.getInstance().addingObserver(SystemLoadBoxView.this);
    }

    /**
     * method updating view with new data
     * @see Observer#update(Observable, Object)
     * @param o not used
     * @param arg not used
     */
    @Override
    public void update(Observable o, Object arg) {
        systemLoadText.setText(String.join("\n", SingletonStaticGeneralStats.getInstance().getMiscellaneous()));
        lineChartClass.addEntryLineChart(SingletonDynamicGeneralStats.getInstance().getCpuLoad());
    }
}
