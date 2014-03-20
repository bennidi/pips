package net.engio.pips;

import net.engio.pips.data.DataCollector;
import net.engio.pips.data.DataPoint;
import net.engio.pips.data.filter.IDataFilter;
import net.engio.pips.data.filter.Sampler;
import org.junit.Test;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class DataCollectionTest extends UnitTest{

    private int numberOfDataItems = 100;

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
        Sampler<Long> sampler = new Sampler<Long>(new IDataFilter.TimeBased<Long>(10));
        sampler.pipeInto(timings);
        for(int i =0;i< numberOfDataItems; i++){
            sampler.receive(new DataPoint<Long>(System.currentTimeMillis()));
        }
        assertEquals(1, timings.size());
    }

    @Test
    public void testSamplingFrequency(){
        DataCollector<Long> timings = new DataCollector<Long>("nfnsa");
        Sampler<Long> sampler = new Sampler<Long>(new IDataFilter.TimeBased<Long>(10));
        sampler.pipeInto(timings);
        for(int i =0;i< numberOfDataItems; i++){
            sampler.receive(new DataPoint<Long>(System.currentTimeMillis()));
            pause(10);
        }
        assertEquals(numberOfDataItems, timings.size());
    }


}
