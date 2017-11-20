package pcstatus.charts;

import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class LineChartClass {
    private XYChart.Series series;
    private int time = 0;
    private NumberAxis xBoundLineChart;

    public LineChartClass(LineChart lineChart){
        NumberAxis yaxis;
        yaxis = (NumberAxis) lineChart.getYAxis();
        xBoundLineChart = (NumberAxis) lineChart.getXAxis();
        xBoundLineChart.setLowerBound(0);
        xBoundLineChart.setUpperBound(15);
        xBoundLineChart.setAutoRanging(false);
        yaxis.setAutoRanging(false);
        yaxis.setLowerBound(0);
        yaxis.setUpperBound(100);
        series = new XYChart.Series();
        series.setName("CPU load");

        lineChart.setStyle("CHART_COLOR_1: #ff3d00;");
        lineChart.getData().add(series);

    }

    public void addEntryLineChart(Float value) {
        series.getData().add(new XYChart.Data(time, value));
        int maxRange = 15;
        if (time > maxRange) {
            series.getData().remove(0);
        }
        if (time > maxRange - 1) {
            xBoundLineChart.setLowerBound(xBoundLineChart.getLowerBound() + 1);
            xBoundLineChart.setUpperBound(xBoundLineChart.getUpperBound() + 1);
        }
        time++;

       // series.getNode().setStyle(".default-color0.chart-series-line { -fx-stroke: #f0f0f0; }");
    }
}
