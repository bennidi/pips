package net.engio.pips;

import net.engio.pips.data.DataCollector;
import net.engio.pips.data.DataPoint;
import net.engio.pips.data.aggregator.Average;
import net.engio.pips.data.aggregator.Max;
import net.engio.pips.data.aggregator.Min;
import net.engio.pips.data.filter.IDataFilter;
import net.engio.pips.data.filter.Sampler;
import net.engio.pips.data.utils.ExecutionTimer;
import net.engio.pips.data.utils.Generator;
import net.engio.pips.data.utils.ItemCounter;
import net.engio.pips.data.utils.Sensor;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author bennidi
 *         Date: 2/25/14
 */
public class DataCollectionTest extends UnitTest{

    private int numberOfDataItems = 100;

    @Test
    public void testExecutionTimer() throws InterruptedException {
        DataCollector<Long> timings = new DataCollector("Execution times");
        ExecutionTimer timer = new ExecutionTimer(timings);
        for(int i = 0; i < 100 ; i ++){
            timer.begin();
            // run some code here
            Thread.sleep(1);
            timer.end();
        }

        assertEquals(100, timings.size());
        for(DataPoint<Long> timing: timings.getDatapoints()){
            assertTrue(timing.getValue() >= 1);
        }

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
        Sampler<Long> sampler = new Sampler<Long>(new IDataFilter.TimeBased<Long>(10));
        sampler.connectTo(timings);
        for(int i =0;i< numberOfDataItems; i++){
            sampler.receive(new DataPoint<Long>(System.currentTimeMillis()));
        }
        assertEquals(1, timings.size());
    }

    @Test
    public void testSamplingFrequency(){
        DataCollector<Long> timings = new DataCollector<Long>("nfnsa");
        Sampler<Long> sampler = new Sampler<Long>(new IDataFilter.TimeBased<Long>(10));
        sampler.connectTo(timings);
        for(int i =0;i< numberOfDataItems; i++){
            sampler.receive(new DataPoint<Long>(System.currentTimeMillis()));
            pause(10);
        }
        assertEquals(numberOfDataItems, timings.size());
    }

    @Test
    public void testSlidingAggregator(){
        DataPointProducer producer = new DataPointProducer();
        DataCollector<Long> All = new DataCollector<Long>("all");
        DataCollector<Double> Avg = new DataCollector<Double>("avg");
        DataCollector<Double> Min = new DataCollector<Double>("min");
        DataCollector<Double> Max = new DataCollector<Double>("max");

        producer.connectTo(All,
                new Average<Long>().add(Avg),
                new Min<Long>().add(Min),
                new Max<Long>().add(Max));

        producer.emitAll(new long[]{0l, 1l, 2l, 3l, 4l, 5l, 6l});
        assertArrayEquals(new Object[]{0.0, 0.5, 1.0, 6.0 / 4.0, 10.0 / 5.0, 15.0 / 6.0, 21.0 / 7.0}, Avg.getValues());
        assertArrayEquals(new Object[]{0.0,0.0,0.0,0.0,0.0,0.0,0.0}, Min.getValues());
        assertArrayEquals(new Object[]{0.0,1.0,2.0,3.0,4.0,5.0,6.0}, Max.getValues());
    }


    @Test
    public void testComplexPipeline(){
        DataPointProducer testProducer = new DataPointProducer();
        ItemCounter counter = new ItemCounter();
        DataCollector<Long> all = new DataCollector<Long>("all data points");
        DataCollector<Long> everySecondDP = new DataCollector<Long>("every second data point");
        DataCollector<Long> everySecondMs = new DataCollector<Long>("every second millisecond");
        DataCollector<Long> every4thMs = new DataCollector<Long>("every 4th millisecond");
        DataCollector<Long> every4thDP = new DataCollector<Long>("every 4th data point");

        testProducer
                .add(counter.add(all))
                .add(Sampler.<Long>timeBased(2).add(everySecondMs))
                .add(Sampler.<Long>timeBased(4).add(every4thMs))
                .add(Sampler.<Long>timeBased(2).add(everySecondMs))
                .add(Sampler.<Long>skip(3).add(every4thDP))
                .add(Sampler.<Long>skip(1).add(everySecondDP));

        testProducer.emitRandom(1, 100);

        assertEquals(counter.getItemCount(), testProducer.getEmitted().size());
        assertEquals(all.size(), testProducer.getEmitted().size());
        assertTrue(all.getDatapoints().containsAll(testProducer.getEmitted()));
        assertEquals(everySecondDP.getDatapoints(), everySecondMs.getDatapoints());
        assertTrue(everySecondDP.getDatapoints().containsAll(every4thDP.getDatapoints()));
        assertTrue(everySecondMs.getDatapoints().containsAll(every4thMs.getDatapoints()));

    }

    @Test
    public void testRuntimeCollector() throws InterruptedException {
        DataCollector<Double> loadAvg = new DataCollector<Double>("Average load");
        Sensor sensor  = Sensor.Each(10, TimeUnit.MILLISECONDS).pipe(new Generator() {
           @Override
           public Object next() {
               return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
           }
       }).into(loadAvg);

        sensor.start();
        Thread.sleep(1000);
        sensor.stop();
        int count = loadAvg.size();
        assertTrue(count > 50);
        Thread.sleep(1000);
        assertEquals(count, loadAvg.size());
    }
}
