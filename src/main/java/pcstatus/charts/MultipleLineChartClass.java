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
import pcstatus.dataPackage.SingletonDynamicGeneralStats;

import java.util.stream.Stream;

/**
 * this class is responsible for creating and managing the multi line chart dedicated for cpu threads load
 * the chart will fit based on the number of threads of CPU
 *
 * @author Andrea Bravaccino
 */
public class MultipleLineChartClass {

    /**
     * series containing chart values
     */
    private XYChart.Series[] series;
    /**
     * is the type of chart
     */
    private LineChart multiLineChart;
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
     * @param multiLineChart is the type of chart for cpu threads load
     */
    public MultipleLineChartClass(LineChart multiLineChart){
        this.multiLineChart = multiLineChart;
        multiLineChart.animatedProperty().setValue(false);
        NumberAxis yAxis; //Y axis variable

        //getter and setting proprieties for y and x axes
        yAxis = (NumberAxis) multiLineChart.getYAxis();
        xAxis = (NumberAxis) multiLineChart.getXAxis();
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(10);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
    }

    /**
     * set series of content to draw chart and adapt to cpu threads
     */
    public void createSeries() {
        Float[] seriesValues = SingletonDynamicGeneralStats.getInstance().getPercPerThread();
        series = Stream.<XYChart.Series<String, Number>>generate(XYChart.Series::new).limit(seriesValues.length).toArray(XYChart.Series[]::new);

        for (int i = 0;i<series.length;i++){
            series[i] = new XYChart.Series();
            series[i].setName("Thread " + i);
            multiLineChart.getData().add(series[i]);
        }
    }

    /**
     * add a value to chart and move x axis to show last 15 values
     * @param value value to add to the chart
     */
    public void addEntryLineChart(Float[] value) {
        int maxRange = 10; //range of visible chart

        //move x axis to show last 1 value
        for (int i = 0; i<value.length; i++){
            series[i].getData().add(new XYChart.Data(numberOfVisibleEntries, value[i]));
            if (numberOfVisibleEntries > maxRange) {
                series[i].getData().remove(0);
            }
        }
        if (numberOfVisibleEntries > maxRange - 1) {
            xAxis.setLowerBound(xAxis.getLowerBound() + 1);
            xAxis.setUpperBound(xAxis.getUpperBound() + 1);
        }
        numberOfVisibleEntries++;
    }
}
