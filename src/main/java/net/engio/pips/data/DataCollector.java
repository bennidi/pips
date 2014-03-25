package net.engio.pips.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class DataCollector<IN> extends DataProcessor<IN, IN> implements IDataCollector<IN> {

    private List<DataPoint<IN>> datapoints = new LinkedList<DataPoint<IN>>();

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
    public List<DataPoint<IN>> getDatapoints() {
        return Collections.unmodifiableList(datapoints);
    }

    @Override
    public Object[] getValues() {
        Object[] values = new Object[datapoints.size()];
        int index = 0;
        for(DataPoint<IN> dp : datapoints)
            values [index++] = dp.getValue();
        return values;
    }

}
