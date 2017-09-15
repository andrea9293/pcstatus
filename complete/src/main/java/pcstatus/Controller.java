package pcstatus;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import pcstatus.charts.LineChartClass;

public class Controller {

    @FXML private Button buttonSystemInfo;
    @FXML private Button buttonHdd;
    @FXML private Button buttonProcessor;
    @FXML private Button buttonBattery;
    @FXML
    private VBox mainVbox;
    @FXML
    private LineChart lineChart;
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
    private Node systemLoadBox;
    private Node batteryBox;
    private Node cpuBox;
    private Node systemInfoBox;
    private Node disksBox;

    private ServerBatteryMain serverBatteryMain;

    @FXML
    @Autowired
    private void initialize() {
        lineChartClass = new LineChartClass(lineChart);
        systemLoadBox = mainVbox.getChildren().get(0);
        batteryBox = mainVbox.getChildren().get(1);
        cpuBox = mainVbox.getChildren().get(2);
        disksBox = mainVbox.getChildren().get(3);
        systemInfoBox = mainVbox.getChildren().get(4);
        mainVbox.getChildren().removeAll(systemInfoBox, batteryBox, cpuBox, disksBox, systemLoadBox);
        mainVbox.getChildren().add(systemLoadBox);

        buttonSystemLoad.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeView(systemLoadBox);
            }
        });
        buttonBattery.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeView(batteryBox);
            }
        });
        buttonProcessor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeView(cpuBox);
            }
        });
        buttonHdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeView(disksBox);
            }
        });
        buttonSystemInfo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeView(systemInfoBox);
            }
        });
    }

    public void setServerBatteryMain(ServerBatteryMain serverBatteryMain) {
        this.serverBatteryMain = serverBatteryMain;
    }

    public void changeView(Node view){
        mainVbox.getChildren().removeAll(systemInfoBox, batteryBox, cpuBox, disksBox, systemLoadBox);
        mainVbox.getChildren().add(view);
        serverBatteryMain.resizeWindow();
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

    @FXML
    public void setBatteryText(String labelTexts) {
        batteryText.setText(labelTexts);
    }

    @FXML
    public void setCpuText(String labelTexts) {
        cpuText.setText(labelTexts);
    }

    @FXML
    public void setDisksText(String disksText) {
        this.disksText.setText(disksText);
    }
}
