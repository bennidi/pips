package net.engio.imupers.performance.reports;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class TimeSeriesCollector {

    private String id;

    private Map<String, List<TimeSeriesConsumer>> series = new HashMap<String, List<TimeSeriesConsumer>>();

    public TimeSeriesCollector(String id) {
        this.id = id;
    }

    public synchronized TimeSeriesConsumer makeSeries(String group, String label){
        TimeSeriesConsumer mySeries = new TimeSeriesConsumer(label);
        // get or create the group
        List groupSeries = series.get(group);
        if(groupSeries == null){
            groupSeries = new ArrayList();
            series.put(group, groupSeries);
        }
        groupSeries.add(mySeries);
        return mySeries;
    }

    public void generateChart(String path, String title, String xLabel, int width){
        String defaultGroup = null;
        Map<String, TimeSeriesCollection> groups = new HashMap<String, TimeSeriesCollection>();
        // each list in the series map forms a collection
        // the original map is transformed to a new map with the same amount of keys
        // the default group is the first (arbitrary)
        for(Map.Entry<String, List<TimeSeriesConsumer>> entry: series.entrySet()){
            TimeSeriesCollection collection = new TimeSeriesCollection();
            if(defaultGroup == null)defaultGroup = entry.getKey();
            for(TimeSeriesConsumer consumer : entry.getValue()){
                if(!consumer.getSeries().isEmpty()){
                    // add the series to the collection that represents its group
                    collection.addSeries(consumer.getSeries());
                }
            }
            groups.put(entry.getKey(), collection);
        }

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,  // title
                xLabel,             // x-axis label
                defaultGroup,   // y-axis label
                groups.get(defaultGroup),            // data
                true,               // create legend?
                true,               // generate tooltips?
                false               // generate URLs?
        );

        // configure plot
        final XYPlot plot = chart.getXYPlot();
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setBackgroundPaint(Color.DARK_GRAY);

        // add other groups
        int axisIndex = 1;
        for(Map.Entry<String, TimeSeriesCollection> entry: groups.entrySet()){
            if(entry.getKey().equals(defaultGroup))continue; // skip the default group, because its already part of the chart
            final NumberAxis axis2 = new NumberAxis(entry.getKey());
            axis2.setAutoRangeIncludesZero(false);  // prevent the axis to be scaled from zero if the dataset begins with higher values
            plot.setRangeAxis(axisIndex, axis2);
            plot.setDataset(axisIndex, entry.getValue());
            plot.mapDatasetToRangeAxis(axisIndex, axisIndex);
            axisIndex++;
        }

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer();
        for(int seriesIdx =0 ; seriesIdx < plot.getSeriesCount() ; seriesIdx++){
            renderer.setSeriesShapesVisible(seriesIdx, true);
        }


        try {
            ChartUtilities.saveChartAsJPEG(new File(path), chart, width, 1024);
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
    }
}
