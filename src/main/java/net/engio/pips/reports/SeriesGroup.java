package net.engio.pips.reports;

import net.engio.pips.data.DataCollector;
import net.engio.pips.data.IDataCollector;
import net.engio.pips.data.IDataProcessor;
import net.engio.pips.data.aggregator.Average;
import net.engio.pips.data.utils.ItemCounter;
import net.engio.pips.data.utils.TimeBasedAggregator;
import net.engio.pips.lab.Experiment;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
* A series group is used to configure a set of data collectors for being included in a time series
 * chart. Apart from the collectors themselves, aggregates can be configured to generate series
 * like a moving average from the set of collectors.
*
* @author bennidi
*         Date: 3/4/14
*/
public class SeriesGroup {

    private String collectorId;

    private String label;

    private Map<String, IDataProcessor> aggregators = new HashMap<String, IDataProcessor>();

    private Collection<IDataCollector> collectors = new ArrayList<IDataCollector>();

    private int size;

    private String yAxis = "";

    private int collectorSampleSize = 1;

    public SeriesGroup(String collectorId, String label) {
        this.collectorId = collectorId;
        this.label = label;
    }

    public SeriesGroup addCollector(IDataCollector collector){
        collectors.add(collector);
        return this;
    }

    public String getLabel() {
        return label;
    }

    public int getSize() {
        return size;
    }

    public String getCollectorId() {
        return collectorId;
    }

    public SeriesGroup aggregate(String name, IDataProcessor aggregator){
        aggregators.put(name, aggregator);
        return this;
    }

    public String getyAxis() {
        return yAxis;
    }

    public SeriesGroup setyAxis(String yAxis) {
        this.yAxis = yAxis;
        return this;
    }

    public SeriesGroup setDrawEveryNthGraph(int factor) {
        this.collectorSampleSize = factor;
        return this;
    }

    public TimeSeriesCollection createDataSet(Experiment experiment){
        Collection<IDataCollector> collectors = experiment.getCollectors(collectorId);
        collectors.addAll(this.collectors);
        TimeSeriesCollection collection = new TimeSeriesCollection();
        TimeBasedAggregator aggregator = new TimeBasedAggregator();
        // create a series from each data collector
        int numberOfCollectors = 1;
        for(IDataCollector collector : collectors){
            // ignore empty data collectors as well as according to sample size
            if(collector == null || collector.size() == 0)continue;
            if(numberOfCollectors % collectorSampleSize != 0){
                numberOfCollectors++;
                continue;
            }

            TimeSeriesConsumer wrapper = new TimeSeriesConsumer(collector.getId());
            collector.feed(wrapper);
            TimeSeries series = wrapper.getSeries();
            collection.addSeries(series);
            // prepare the time based aggregator
            if(!aggregators.isEmpty())
                collector.feed(aggregator);
            numberOfCollectors++;
        }
        DataCollector average = aggregator.fold(new Average());
        ItemCounter numberOfDatapoints = new ItemCounter();
        for(Map.Entry<String, IDataProcessor> aggregation : aggregators.entrySet()){
            TimeSeriesConsumer series = new TimeSeriesConsumer(aggregation.getKey());
            IDataProcessor aggregate = aggregation.getValue();
            aggregate.connectTo(numberOfDatapoints).connectTo(series);
            average.feed(aggregate);
            collection.addSeries(series.getSeries());
        }
        size = numberOfDatapoints.getItemCount();
        return collection;
    }
}
