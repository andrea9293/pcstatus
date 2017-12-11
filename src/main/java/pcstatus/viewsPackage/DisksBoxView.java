/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus.viewsPackage;

import javafx.scene.control.Label;
import pcstatus.charts.PieChartClass;
import pcstatus.dataPackage.SingletonDynamicGeneralStats;
import pcstatus.dataPackage.SingletonStaticGeneralStats;

import java.util.Observable;
import java.util.Observer;

/**
 * This class manages the view dedicated for disks information implementing <code>Observer</code>
 * @see java.util.Observer
 * @author Andrea Bravaccino
 */
public class DisksBoxView implements Observer {

    /**
     * label containing information about disks
     */
    private Label disksText;

    /**
     * type of chart showing used and unused space of main disk
     */
    private PieChartClass pieChartClass;

    /**
     * the constructor initialize the parameters and register itself like <code>Observer</code>
     * @param disksText label that will contain information about disks
     * @param pieChartClass chart that will show used and unused space of the main disk
     */
    public DisksBoxView(Label disksText, PieChartClass pieChartClass) {
        this.disksText = disksText;
        this.pieChartClass = pieChartClass;
        SingletonStaticGeneralStats.getInstance().addingObserver(DisksBoxView.this);
    }

    /**
     * method updating view with new data
     * @see Observer#update(Observable, Object)
     * @param o not used
     * @param arg not used
     */
    @Override
    public void update(Observable o, Object arg) {
        disksText.setText(String.join("\n", SingletonDynamicGeneralStats.getInstance().getDisks()));
        pieChartClass.addEntryPieChart(SingletonDynamicGeneralStats.getInstance().getAvaibleFileSystem());
    }
}
