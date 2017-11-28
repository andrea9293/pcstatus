/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus.charts;

import javafx.scene.chart.PieChart;

/**
 * this class is responsible for creating and managing the Piechart dedicated for memory storage archive.
 * This Piechart has only 2 slice: one for used space and another for unused space of file system
 *
 * @author Andrea Bravaccino
 */
public class PieChartClass {
    /**
     * a slice of the "cake"
     */
    private PieChart.Data slice1;
    /**
     * second slice of "cake"
     */
    private PieChart.Data slice2;


    /**
     * the constructor takes care of initializing two slices (used and unused space) and set
     * proprieties of chart (legend visibility, animations and sizes
     * @param pieChart is the type of chart for cpu load
     */
    public PieChartClass(PieChart pieChart) {

        slice1 = new PieChart.Data("Unused space",0);
        slice2 = new PieChart.Data("Used space", 0);

        pieChart.getData().add(slice1);
        pieChart.getData().add(slice2);
        pieChart.setPrefSize(400, 300);
        pieChart.animatedProperty().setValue(false);

        pieChart.legendVisibleProperty().setValue(false);
    }

    /**
     * add values to Piechart
     * @param avaibleFileSystem this value indicates free memory space. From this you calculate the busy space of a filesystem
     */
    public void addEntryPieChart(Float[] avaibleFileSystem) {
        slice1.setName(avaibleFileSystem[0] + "%\nUnused space");
        slice1.setPieValue(avaibleFileSystem[0]);
        slice2.setName((100f - avaibleFileSystem[0]) + "%\nUsed space");
        slice2.setPieValue(100f - avaibleFileSystem[0]);

        slice1.getNode().setStyle("-fx-pie-color: #bbdefb");
        slice2.getNode().setStyle("-fx-pie-color: #78909c");
    }
}
