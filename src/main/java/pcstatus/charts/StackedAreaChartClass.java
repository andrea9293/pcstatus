/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus.charts;


import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;

/**
 * this class is responsible for creating and managing the Stacked area chart dedicated for battery performance.
 * This chart show progress of battery from opening of the program
 *
 * @author Andrea Bravaccino
 */
public class StackedAreaChartClass {
    /**
     * series containing chart values
     */
    private XYChart.Series series;
    /**
     * variable to count number of visible entries (max 10)
     */
    private int numberOfVisibleEntries = 0;
    /**
     * X axis variable
     */
    private NumberAxis xAxis;

    /**
     * the constructor is responsible for initialize the chart (lower and upper buonds, name, colors)
     * @param stackedAreaChart is the type of chart for battery performance
     */
    public StackedAreaChartClass(StackedAreaChart stackedAreaChart){
        NumberAxis yaxis = (NumberAxis) stackedAreaChart.getYAxis();

        //getter and setting proprieties for y and x axes
        xAxis = (NumberAxis) stackedAreaChart.getXAxis();
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(10);
        xAxis.setAutoRanging(false);
        xAxis.setTickLabelsVisible(false); //hide numbers on x axis
        stackedAreaChart.setVerticalGridLinesVisible(false);//hide vertical lines
        stackedAreaChart.animatedProperty().setValue(false);
        yaxis.setAutoRanging(false);
        yaxis.setLowerBound(0);
        yaxis.setUpperBound(100);

        //set series of content to draw chart
        series = new XYChart.Series();
        series.setName("Battery level");
        stackedAreaChart.getData().add(series);

        //setting colors of charts
        Node fill = series.getNode().lookup(".chart-series-area-fill");
        fill.setStyle("-fx-fill: #fff7ad;");
        Node line = series.getNode().lookup(".chart-series-area-line");
        line.setStyle("-fx-stroke: #8bc34a;" +
                "-fx-stroke-width: 3px;"); // set width of line
        stackedAreaChart.setStyle("CHART_COLOR_1: #8bc34a;"); //color of dots
    }

    /**
     * add a value to stacked area chart
     * @param value value to add to the chart
     */
    public void addEntryStackedAreaChart(Integer value) {
        series.getData().add(new XYChart.Data(numberOfVisibleEntries, value));
        int maxRange = 10;
        if (numberOfVisibleEntries > maxRange - 1) {
            xAxis.setUpperBound(xAxis.getUpperBound() + 1);
        }
        numberOfVisibleEntries++;
    }
}




