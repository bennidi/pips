package net.engio.imupers.performance.reports;

import net.engio.imupers.performance.Benchmark;
import net.engio.imupers.performance.data.DataCollector;
import net.engio.imupers.performance.data.IDataCollector;
import net.engio.imupers.performance.data.IDataProcessor;
import net.engio.imupers.performance.data.aggregator.Average;
import net.engio.imupers.performance.data.utils.ItemCounter;
import net.engio.imupers.performance.data.utils.TimeBasedAggregator;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

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

    private int size;

    private int collectorSampleSize = 1;

    public SeriesGroup(String collectorId, String label) {
        this.collectorId = collectorId;
        this.label = label;
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

    public SeriesGroup setDrawEveryNthGraph(int factor) {
        this.collectorSampleSize = factor;
        return this;
    }

    public TimeSeriesCollection createDataSet(Benchmark benchmark){
        Collection<IDataCollector> collectors = benchmark.getResults().getCollectors(collectorId);
        TimeSeriesCollection collection = new TimeSeriesCollection();
        TimeBasedAggregator aggregator = new TimeBasedAggregator();
        // create a series from each data collector
        int numberOfCollectors = 1;
        for(IDataCollector collector : collectors){
            // ignore empty data collectors as well as according to sample size
            if(collector.size() == 0)continue;
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
