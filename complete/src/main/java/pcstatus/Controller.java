package pcstatus;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import pcstatus.charts.LineChartClass;
import pcstatus.charts.MultipleLineChartClass;

public class Controller {

    @FXML
    public LineChart lineChartPercPerThread;
    @FXML
    private Button buttonSystemInfo;
    @FXML
    private Button buttonHdd;
    @FXML
    private Button buttonProcessor;
    @FXML
    private Button buttonBattery;
    @FXML
    private VBox mainVbox;
    @FXML
    private LineChart lineChartSystemLoad;
    @FXML
    private Button buttonSystemLoad;
    @FXML
    private Label systemText;
    @FXML
    private Label miscellaneous;
    @FXML
    private Label cpuText;
    @FXML
    private Label batteryText;
    @FXML
    private Label disksText;

    private LineChartClass lineChartClass;
    private MultipleLineChartClass multipleLineChartClass;
    private Node systemLoadBox;
    private Node batteryBox;
    private Node cpuBox;
    private Node systemInfoBox;
    private Node disksBox;

    @FXML
    @Autowired
    private void initialize() {
        lineChartClass = new LineChartClass(lineChartSystemLoad);
        multipleLineChartClass = new MultipleLineChartClass(lineChartPercPerThread);
        systemLoadBox = mainVbox.getChildren().get(0);
        batteryBox = mainVbox.getChildren().get(1);
        cpuBox = mainVbox.getChildren().get(2);
        disksBox = mainVbox.getChildren().get(3);
        systemInfoBox = mainVbox.getChildren().get(4);
        changeView(systemLoadBox);

        buttonSystemLoad.setOnAction(event -> changeView(systemLoadBox));
        buttonBattery.setOnAction(event -> changeView(batteryBox));
        buttonProcessor.setOnAction(event -> changeView(cpuBox));
        buttonHdd.setOnAction(event -> changeView(disksBox));
        buttonSystemInfo.setOnAction(event -> changeView(systemInfoBox));
    }

    public MultipleLineChartClass getMultipleLineChartClass() {
        return multipleLineChartClass;
    }

    public void changeView(Node view) {
        mainVbox.getChildren().removeAll(systemInfoBox, batteryBox, cpuBox, disksBox, systemLoadBox);
        mainVbox.getChildren().add(view);
    }

    public LineChartClass getLineChartClass() {
        return lineChartClass;
    }

    public void setSystemText(String systemText) {
        this.systemText.setText(systemText);
    }

    public void setMiscellaneous(String miscellaneous) {
        this.miscellaneous.setText(miscellaneous);
    }

    public void setBatteryText(String labelTexts) {
        batteryText.setText(labelTexts);
    }

    public void setCpuText(String labelTexts) {
        cpuText.setText(labelTexts);
    }

    public void setDisksText(String disksText) {
        this.disksText.setText(disksText);
    }
}
