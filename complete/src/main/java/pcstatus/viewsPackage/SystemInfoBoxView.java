/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus.viewsPackage;

import javafx.scene.control.Label;
import pcstatus.dataPackage.SingletonStaticGeneralStats;

import java.util.Observable;
import java.util.Observer;

/**
 * This class manages the view dedicated for information about system implementing <code>Observer</code>
 * @see java.util.Observer
 * @author Andrea Bravaccino
 */
public class SystemInfoBoxView implements Observer {
    /**
     * show system's information
     */
    private Label systemInfoText;

    /**
     * the constructor initialize the parameters and register itself like <code>Observer</code>
     * @param systemInfoText label that will show system's information
     */
    public SystemInfoBoxView(Label systemInfoText) {
        this.systemInfoText = systemInfoText;
        SingletonStaticGeneralStats.getInstance().addingObserver(SystemInfoBoxView.this);
    }

    /**
     * method updating view with new data
     * @see Observer#update(Observable, Object)
     * @param o not used
     * @param arg not used
     */
    @Override
    public void update(Observable o, Object arg) {
        systemInfoText.setText(String.join("\n", SingletonStaticGeneralStats.getInstance().getComputerInfo()));
    }
}
