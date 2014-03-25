package net.engio.pips;

import net.engio.pips.data.DataPoint;
import net.engio.pips.data.DataProcessor;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Emit data points containing long values at specified rates.
 *
 * @author bennidi
 *         Date: 3/20/14
 */
public class DataPointProducer extends DataProcessor<Long, Long>{

    private Set<DataPoint> emitted = new HashSet<DataPoint>();

    public void emitRandom(int numberOfDataPoints, int intervalInMs){
        Random rand = new Random();
        long ts = System.currentTimeMillis();
         for(int i = 0; i < numberOfDataPoints;i++){
             ts += intervalInMs;
             receive(new DataPoint<Long>(ts, rand.nextLong()));
         }
    }

    public void emitAll(long[] values, int intervalInMs){
        long ts = System.currentTimeMillis();
        for(int i = 0; i < values.length;i++){
            ts += intervalInMs;
            receive(new DataPoint<Long>(ts, values[i]));
        }
    }

    public void emitAll(long[] values){
        emitAll(values, 1);
    }

    public void clear(){
        emitted.clear();
    }

    @Override
    public void receive(DataPoint<Long> datapoint) {
        emitted.add(datapoint);
        emit(datapoint);
    }

    public Set<DataPoint> getEmitted() {
        return emitted;
    }
}
