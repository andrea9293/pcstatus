/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus.viewsPackage;

import javafx.scene.control.Label;
import pcstatus.charts.MultipleLineChartClass;
import pcstatus.dataPackage.SingletonDynamicGeneralStats;
import pcstatus.dataPackage.SingletonStaticGeneralStats;

import java.util.Observable;
import java.util.Observer;

/**
 * This class manages the view dedicated for CPU information implementing <code>Observer</code>
 *
 * @author Andrea Bravaccino
 * @see java.util.Observer
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
     *
     * @param cpuText                label that will contain information about CPU
     * @param multipleLineChartClass chart that will show CPU threads load
     */
    public CpuBoxView(Label cpuText, MultipleLineChartClass multipleLineChartClass) {
        this.cpuText = cpuText;
        this.multipleLineChartClass = multipleLineChartClass;
        SingletonStaticGeneralStats.getInstance().addingObserver(CpuBoxView.this);
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
        cpuText.setText(String.join("\n", SingletonStaticGeneralStats.getInstance().getCpuInfo()));

        if (SingletonStaticGeneralStats.getInstance().isFirtShow()) {
            multipleLineChartClass.createSeries();
            multipleLineChartClass.addEntryLineChart(SingletonDynamicGeneralStats.getInstance().getPercPerThread());
        } else {
            multipleLineChartClass.addEntryLineChart(SingletonDynamicGeneralStats.getInstance().getPercPerThread());
        }
    }
}
