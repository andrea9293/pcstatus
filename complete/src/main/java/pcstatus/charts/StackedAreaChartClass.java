package pcstatus.charts;


import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;

public class StackedAreaChartClass {
    private XYChart.Series series;
    private int time = 0;
    private NumberAxis xBoundstackedAreaChart;

    public StackedAreaChartClass(StackedAreaChart stackedAreaChart){
        NumberAxis yaxis = (NumberAxis) stackedAreaChart.getYAxis();
        xBoundstackedAreaChart = (NumberAxis) stackedAreaChart.getXAxis();
        xBoundstackedAreaChart.setLowerBound(0);
        xBoundstackedAreaChart.setUpperBound(10);
        xBoundstackedAreaChart.setAutoRanging(false);
        xBoundstackedAreaChart.setTickLabelsVisible(false); //hide numbers on x axis
        stackedAreaChart.setCreateSymbols(false); //hide dots
        yaxis.setAutoRanging(false);
        yaxis.setLowerBound(0);
        yaxis.setUpperBound(100);
        series = new XYChart.Series();
        series.setName("Battery level");
        stackedAreaChart.getData().add(series);

        //setting colors of charts
        Node fill = series.getNode().lookup(".chart-series-area-fill");
        fill.setStyle("-fx-fill: #fff7ad;");
        Node line = series.getNode().lookup(".chart-series-area-line");
        line.setStyle("-fx-stroke: #8bc34a;" +
                "-fx-stroke-width: 3px;"); // set width of line
        //color of dot
        stackedAreaChart.setStyle("CHART_COLOR_1: #8bc34a;");
    }

    public void addEntryStackedAreaChart(Integer value) {
        series.getData().add(new XYChart.Data(time, value));
        int maxRange = 10;
        if (time > maxRange - 1) {
            xBoundstackedAreaChart.setUpperBound(xBoundstackedAreaChart.getUpperBound() + 1);
        }
        time++;
    }
}




