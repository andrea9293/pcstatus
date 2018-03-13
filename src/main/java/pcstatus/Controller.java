/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import pcstatus.charts.LineChartClass;
import pcstatus.charts.MultipleLineChartClass;
import pcstatus.charts.PieChartClass;
import pcstatus.charts.StackedAreaChartClass;
import pcstatus.viewsPackage.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import javafx.scene.image.Image;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;


/**
 * this class is responsible of initialization of all graphic side of the program
 *
 * @author Andrea Bravaccino
 */
public class Controller {

    @FXML
    private VBox settingVBox;
    @FXML
    private ImageView qrImageView;
    @FXML
    private VBox openLibs;
    /* @FXML
     private Label serverPortInformation;*/
    @FXML
    private Label bluetoothInformation;
    /*@FXML
    private Label ipAddressInformation;*/
    @FXML
    private StackedAreaChart stackedAreaChartBattery;
    @FXML
    private LineChart lineChartPercPerThread;
    @FXML
    private PieChart pieChartDisk;
    @FXML
    private Button buttonSettings;
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
    private Label systemInfoText;
    @FXML
    private Label systemLoadText;
    @FXML
    private Label cpuText;
    @FXML
    private Label batteryText;
    @FXML
    private Label disksText;

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
        new SettingsBoxView(qrImageView, bluetoothInformation, openLibs, settingVBox);

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
     *
     * @param view choosen view
     */
    private void changeView(Node view) {
        mainVbox.getChildren().removeAll(systemInfoBox, batteryBox, cpuBox, disksBox, systemLoadBox, settingsBox);
        mainVbox.getChildren().add(view);
    }

    public static Image createQR(String ip) {
        String url = "http://" + ip + "/greeting";

        int size = 250;
        try {

            Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            // Now with zxing version 3.2.1 you could change border size (white border size to just 1)
            hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, size,
                    size, hintMap);

            BufferedImage image = new BufferedImage(size, size,
                    BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, size, size);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

            return SwingFXUtils.toFXImage(image, null);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        System.out.println("\n\nYou have successfully created QR Code.");
        return null;
    }
}

