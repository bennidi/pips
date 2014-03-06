package net.engio.graph;

import net.engio.graph.common.UnitTest;
import net.engio.imupers.performance.data.DataCollector;
import net.engio.imupers.performance.data.IDataProcessor;
import net.engio.imupers.performance.data.IDataSink;
import net.engio.imupers.performance.data.filter.DataFilter;
import net.engio.imupers.performance.reports.TimeSeriesCollector;
import net.engio.imupers.performance.data.DataPoint;
import net.engio.imupers.performance.data.utils.ExecutionTimer;
import net.engio.imupers.performance.data.filter.Sampler;
import org.junit.Test;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class DataCollectionTest extends UnitTest{

    private int numberOfDataItems = 100;

    @Test
    public void testTimeSeries(){
        DataCollector<Long> timings = new DataCollector<Long>("nfnsa");
        ExecutionTimer timer = new ExecutionTimer(timings);
        for(int i =0;i< numberOfDataItems; i++){
            timer.begin();
            pause(1);
            timer.end();
        }
        assertEquals(timings.size(), numberOfDataItems);
        TimeSeriesCollector collector = new TimeSeriesCollector("whatever");
        IDataSink series = collector.makeSeries("whatever", "whatever");
        timings.feed(series);
    }

    @Test
    public void testDataPointCollector(){
        DataCollector<Long> timings = new DataCollector<Long>("nfnsa");
        for(int i =0;i< numberOfDataItems; i++){
            timings.receive(new DataPoint<Long>(System.currentTimeMillis()));
            pause(1);
        }

        assertEquals(numberOfDataItems, timings.size());

        DataCollector<Long> timingsCopy = new DataCollector<Long>("nfnsa");
        timings.feed(timingsCopy);

        assertEquals(numberOfDataItems, timingsCopy.size());
    }

    @Test
    public void testSampling(){
        DataCollector<Long> timings = new DataCollector<Long>("nfnsa");
        Sampler<Long> sampler = new Sampler<Long>(new DataFilter.TimeBased<Long>(10));
        sampler.connectTo(timings);
        for(int i =0;i< numberOfDataItems; i++){
            sampler.receive(new DataPoint<Long>(System.currentTimeMillis()));
        }
        assertEquals(1, timings.size());
    }

    @Test
    public void testSamplingFrequency(){
        DataCollector<Long> timings = new DataCollector<Long>("nfnsa");
        Sampler<Long> sampler = new Sampler<Long>(new DataFilter.TimeBased<Long>(10));
        sampler.connectTo(timings);
        for(int i =0;i< numberOfDataItems; i++){
            sampler.receive(new DataPoint<Long>(System.currentTimeMillis()));
            pause(10);
        }
        assertEquals(numberOfDataItems, timings.size());
    }


}
