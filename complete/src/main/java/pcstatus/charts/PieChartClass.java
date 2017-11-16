package pcstatus.charts;

import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import pcstatus.SingletonBatteryStatus;

public class PieChartClass {
    private PieChart pieChart;
    private PieChart.Data slice1;
    private PieChart.Data slice2;

    public PieChartClass(PieChart pieChart) {
        this.pieChart = pieChart;

        slice1 = new PieChart.Data("Unused space",0);
        slice2 = new PieChart.Data("Used space", 0);

        pieChart.getData().add(slice1);
        pieChart.getData().add(slice2);
        pieChart.setPrefSize(400, 300);

        pieChart.legendVisibleProperty().setValue(false);
    }

    public void addEntryPieChart(String[] avaibleFileSystem) {
        slice1.setName(Float.parseFloat(avaibleFileSystem[0]) + "%\nUnused space");
        slice1.setPieValue(Float.parseFloat(avaibleFileSystem[0]));
        slice2.setName((100f - Float.parseFloat(avaibleFileSystem[0])) + "%\nUsed space");
        slice2.setPieValue(100f - Float.parseFloat(avaibleFileSystem[0]));

        slice1.getNode().setStyle("-fx-pie-color: #bbdefb");
        slice2.getNode().setStyle("-fx-pie-color: #78909c");
    }
}
