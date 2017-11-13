package pcstatus.charts;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import pcstatus.SingletonBatteryStatus;
import pcstatus.dataPackage.SingletonNumericGeneralStats;

import java.util.stream.Stream;

public class MultipleLineChartClass {

    private XYChart.Series[] series;
    private LineChart multiLineChart;
    private int time = 0;
    private NumberAxis xBoundLineChart;

    public MultipleLineChartClass(LineChart multiLineChart){
        this.multiLineChart = multiLineChart;
        NumberAxis yaxis;
        yaxis = (NumberAxis) multiLineChart.getYAxis();
        xBoundLineChart = (NumberAxis) multiLineChart.getXAxis();
        xBoundLineChart.setLowerBound(0);
        xBoundLineChart.setUpperBound(10);
        xBoundLineChart.setAutoRanging(false);
        yaxis.setAutoRanging(false);
        yaxis.setLowerBound(0);
        yaxis.setUpperBound(100);
    }

    public void createSeries() {
        Float[] seriesValues = SingletonBatteryStatus.getInstance().getPercPerThread();
        series = Stream.<XYChart.Series<String, Number>>generate(XYChart.Series::new).limit(seriesValues.length).toArray(XYChart.Series[]::new);

        for (int i = 0;i<series.length;i++){
            System.out.println("inizializzo" + i);
            series[i] = new XYChart.Series();
            series[i].setName("Thread " + i);
            multiLineChart.getData().add(series[i]);
        }
    }

    public void addEntryLineChart(Float[] value) {
        System.out.println("stampo dimensione di value " + value.length);
        int maxRange = 10;
        for (int i = 0; i<value.length; i++){
            series[i].getData().add(new XYChart.Data(time, value[i]));
            if (time > maxRange) {
                series[i].getData().remove(0);
            }
        }
        if (time > maxRange - 1) {
            xBoundLineChart.setLowerBound(xBoundLineChart.getLowerBound() + 1);
            xBoundLineChart.setUpperBound(xBoundLineChart.getUpperBound() + 1);
        }
        time++;
    }
}
