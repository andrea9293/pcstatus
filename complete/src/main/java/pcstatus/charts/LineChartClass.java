/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus.charts;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * this class is responsible for creating and managing the chart dedicated for cpu load
 *
 * @author Andrea Bravaccino
 */
public class LineChartClass {
    /**
     * series containing chart values
     */
    private XYChart.Series series;
    /**
     * variable to count visible entries (max 10)
     */
    private int numberOfVisibleEntries = 0;
    /**
     * X axis variable
     */
    private NumberAxis xAxis;

    /**
     * the constructor is responsible for initialize the chart (lower and upper buonds, name, color)
     * @param lineChart is the type of chart for cpu load
     */
    public LineChartClass(LineChart lineChart){
        NumberAxis yAxis; //Y axis variable

        //getter and setting proprieties for y and x axes
        yAxis = (NumberAxis) lineChart.getYAxis();
        xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(15);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);

        //set series of content to draw chart
        series = new XYChart.Series();
        series.setName("CPU load");

        //set color and name chart
        lineChart.setStyle("CHART_COLOR_1: #ff3d00;");
        lineChart.animatedProperty().setValue(false);
        lineChart.getData().add(series);
    }

    /**
     * add a value to chart and move x axis to show last 15 values
     * @param value value to add to the chart
     */
    public void addEntryLineChart(Float value) {
        series.getData().add(new XYChart.Data(numberOfVisibleEntries, value));
        int maxRange = 15; //range of visible chart

        if (numberOfVisibleEntries > maxRange) {
            series.getData().remove(0);
        }
        if (numberOfVisibleEntries > maxRange - 1) {
            xAxis.setLowerBound(xAxis.getLowerBound() + 1);
            xAxis.setUpperBound(xAxis.getUpperBound() + 1);
        }
        numberOfVisibleEntries++;
    }
}
