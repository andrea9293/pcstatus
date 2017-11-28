/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus.viewsPackage;

import javafx.scene.control.Label;
import pcstatus.charts.StackedAreaChartClass;
import pcstatus.dataPackage.SingletonDynamicGeneralStats;
import pcstatus.dataPackage.SingletonStaticGeneralStats;

import java.util.Observable;
import java.util.Observer;

/**
 * This class manages the view dedicated for battery information implementing <code>Observer</code>
 * @see java.util.Observer
 * @author Andrea Bravaccino
 */
public class BatteryBoxView implements Observer {

    /**
     * label containing information about battery
     */
    private Label batteryText;
    /**
     * type of chart showing battery performance
     */
    private StackedAreaChartClass stackedAreaChartClass;

    /**
     * the constructor initialize the parameters and register itself like <code>Observer</code>
     * @param batteryText label that will contain information about battery
     * @param stackedAreaChartClass chart that will show battery performance
     */
    public BatteryBoxView(Label batteryText, StackedAreaChartClass stackedAreaChartClass) {
        this.batteryText = batteryText;
        this.stackedAreaChartClass = stackedAreaChartClass;
        SingletonStaticGeneralStats.getInstance().addingObserver(BatteryBoxView.this);
    }

    /**
     * method updating view with new data
     * @see Observer#update(Observable, Object)
     * @param o not used
     * @param arg not used
     */
    @Override
    public void update(Observable o, Object arg) {
        batteryText.setText(String.join("\n", SingletonDynamicGeneralStats.getInstance().getBattery()));

        if (SingletonDynamicGeneralStats.getInstance().getBatteryPerc() != -1)
            stackedAreaChartClass.addEntryStackedAreaChart(SingletonDynamicGeneralStats.getInstance().getBatteryPerc());

    }
}
