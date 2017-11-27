package pcstatus;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import pcstatus.charts.LineChartClass;
import pcstatus.charts.MultipleLineChartClass;
import pcstatus.charts.PieChartClass;
import pcstatus.charts.StackedAreaChartClass;
import pcstatus.viewsPackage.*;

/**
 * this class is responsible of initialization of all graphic side of the program
 * @author Andrea Bravaccino
 */
public class Controller {

    @FXML private Label serverPortInformation;
    @FXML private Label bluetoothInformation;
    @FXML private Label ipAddressInformation;
    @FXML private StackedAreaChart stackedAreaChartBattery;
    @FXML private LineChart lineChartPercPerThread;
    @FXML private PieChart pieChartDisk;
    @FXML private Button buttonSettings;
    @FXML private Button buttonSystemInfo;
    @FXML private Button buttonHdd;
    @FXML private Button buttonProcessor;
    @FXML private Button buttonBattery;
    @FXML private VBox mainVbox;
    @FXML private LineChart lineChartSystemLoad;
    @FXML private Button buttonSystemLoad;
    @FXML private Label systemInfoText;
    @FXML private Label systemLoadText;
    @FXML private Label cpuText;
    @FXML private Label batteryText;
    @FXML private Label disksText;

    private Node systemLoadBox;
    private Node batteryBox;
    private Node cpuBox;
    private Node systemInfoBox;
    private Node disksBox;
    private Node settingsBox;

    /**
     * this function initializes all graphic components
     */
    @FXML
    private void initialize() {
        //initialize all charts
        PieChartClass pieChartClass = new PieChartClass(pieChartDisk);
        LineChartClass lineChartClass = new LineChartClass(lineChartSystemLoad);
        MultipleLineChartClass multipleLineChartClass = new MultipleLineChartClass(lineChartPercPerThread);
        StackedAreaChartClass stackedAreaChartClass = new StackedAreaChartClass(stackedAreaChartBattery);

        //getting all view
        systemLoadBox = mainVbox.getChildren().get(0);
        batteryBox = mainVbox.getChildren().get(1);
        cpuBox = mainVbox.getChildren().get(2);
        disksBox = mainVbox.getChildren().get(3);
        systemInfoBox = mainVbox.getChildren().get(4);
        settingsBox = mainVbox.getChildren().get(5);
        changeView(systemLoadBox); //set first view

        new SystemLoadBoxView(systemLoadText, lineChartClass);
        new BatteryBoxView(batteryText, stackedAreaChartClass);
        new CpuBoxView(cpuText, multipleLineChartClass);
        new DisksBoxView(disksText, pieChartClass);
        new SystemInfoBoxView(systemInfoText);
        new SettingsBoxView(ipAddressInformation, bluetoothInformation, serverPortInformation);

        // button listener on bottom
        buttonSystemLoad.setOnAction(event -> changeView(systemLoadBox));
        buttonBattery.setOnAction(event -> changeView(batteryBox));
        buttonProcessor.setOnAction(event -> changeView(cpuBox));
        buttonHdd.setOnAction(event -> changeView(disksBox));
        buttonSystemInfo.setOnAction(event -> changeView(systemInfoBox));
        buttonSettings.setOnAction(event -> changeView(settingsBox));
    }


    /**
     * this function removes the current view and shows the user-selected view by clicking the respective button
     * @param view
     */
    private void changeView(Node view) {
        mainVbox.getChildren().removeAll(systemInfoBox, batteryBox, cpuBox, disksBox, systemLoadBox, settingsBox);
        mainVbox.getChildren().add(view);
    }
}
