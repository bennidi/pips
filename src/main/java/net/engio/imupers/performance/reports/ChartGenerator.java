package net.engio.imupers.performance.reports;

import net.engio.imupers.performance.Benchmark;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bennidi
 *         Date: 3/3/14
 */
public class ChartGenerator implements IReporter {

    private List<SeriesGroup> groups = new ArrayList<SeriesGroup>();
    private int pixelPerDatapoint = 5;
    private String title = "Title";
    private String xLabel = "X-Axis";
    private String filename = "chart.jpg";

    public ChartGenerator setPixelPerDatapoint(int pxPerDP) {
        this.pixelPerDatapoint = pxPerDP;
        return this;
    }

    public ChartGenerator setTitle(String title) {
        this.title = title;
        return this;
    }

    public ChartGenerator setXAxisLabel(String xLabel) {
        this.xLabel = xLabel;
        return this;
    }

    public ChartGenerator setFileName(String filename){
        this.filename = filename;
        return this;
    }


    /**
     * Configure a new group that will be treated as a single dataset in the chart.
     * Each dataset has its own range axis.
     * @param collectorId : The id used to retrieve associated data collectors from the result collector
     * @param groupLabel : The label used for the range axis
     * @return
     */
    public SeriesGroup addGroup(String groupLabel, String collectorId){
       SeriesGroup g = new SeriesGroup(groupLabel, collectorId);
        groups.add(g);
        return g;
    }

    public ChartGenerator add(SeriesGroup seriesGroup) {
        groups.add(seriesGroup);
        return this;
    }

    public void generate(Benchmark benchmark){
        String path = benchmark.getReportBaseDir() + filename;
        // create empty chart
        SeriesGroup defaultGroup = this.groups.get(0);
        TimeSeriesCollection collection = defaultGroup.createDataSet(benchmark);
        int maxNumberOfDatapoints = defaultGroup.getSize();
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,  // title
                xLabel,             // x-axis label
                defaultGroup.getLabel(),   // y-axis label
                collection,            // data
                true,               // create legend?
                true,               // generate tooltips?
                false               // generate URLs?
        );

        // preconfigure plot layout
        final XYPlot plot = chart.getXYPlot();
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setBackgroundPaint(Color.DARK_GRAY);

        // add other groups
        List<SeriesGroup> groups = this.groups.subList(1, this.groups.size());
        int axisIndex = 1;
        for(SeriesGroup group : groups){
            final NumberAxis axis2 = new NumberAxis(group.getLabel());
            axis2.setAutoRangeIncludesZero(false);  // prevent the axis to be scaled from zero if the dataset begins with higher values
            plot.setRangeAxis(axisIndex, axis2);
            plot.setDataset(axisIndex, group.createDataSet(benchmark));
            plot.mapDatasetToRangeAxis(axisIndex, axisIndex);
            axisIndex++;
           if(maxNumberOfDatapoints < group.getSize())maxNumberOfDatapoints = group.getSize();
        }

        // calculate width of graph based on number of total data points
        // Note: assumes that the data of all groups spans (roughly) the same domain range
        int width = maxNumberOfDatapoints * pixelPerDatapoint;

        try {
            ChartUtilities.saveChartAsJPEG(new File(path), chart, width <= 0 ? 1024 : width, 1024);
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
    }


}
