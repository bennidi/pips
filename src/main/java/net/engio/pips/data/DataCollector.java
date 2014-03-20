package net.engio.pips.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class DataCollector<IN> extends DataProcessor<IN, IN> implements IDataCollector<IN> {

    private Set<DataPoint<IN>> datapoints = new TreeSet<DataPoint<IN>>(new Comparator<DataPoint<IN>>() {
        @Override
        public int compare(DataPoint<IN> inDataPoint, DataPoint<IN> inDataPoint2) {
            return (int)(inDataPoint.getTsCreated() - inDataPoint2.getTsCreated());
        }
    });

    private String id;

    public DataCollector(String id) {
        this.id = id;
    }

    @Override
    public void receive(DataPoint<IN> datapoint) {
       datapoints.add(datapoint);
    }

    @Override
    public void receive(IN value) {
        receive(new DataPoint(value));
    }

    public void feed(IDataProcessor<IN,?> consumer){
        for(DataPoint dataPoint : datapoints)
            consumer.receive(dataPoint);
    }

    public int size(){
        return datapoints.size();
    }

    @Override
    public String toString() {
        return id + ":" + datapoints;
    }



    public String getId() {
        return id;
    }

    @Override
    public Set<DataPoint<IN>> getDatapoints() {
        return Collections.unmodifiableSet(datapoints);
    }
}
