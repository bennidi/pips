pips
====

Simplistic library to build type-safe data pipelines for processing streams of data items `DataPoint<V>`. Single units (`IDataProcessor<IN,OUT>`)
having input and output channels can be connected to form data processing networks. Encapsulating specific
processing logic in single data processors allows to reuse and recombine logic very easily.

Basic existing components:

+ Data Collector: Store received data items in the order they were received to provide access to them later on
+ Sampler: Filter items based on frequency criteria (accept only one item per X milliseconds or every Xth item, others are dropped)
+ Value Mapper: Convert incoming data items into different ones
+ Aggregates: Calculate aggregates like SUM,AVG,MIN of a stream of numbers
+ Sliding Aggregate: Calculate and propagate aggregated values for a sliding window. The width of the windows can be
defined in terms of time or number of items. Even more complex criteria can be implemented using `DataFilter`
+ Execution Timer: Measure execution times of code using `begin()` and `end()` of the timer
+ ItemCounter: Counts received data items
+ Synchronizer: Serialize access to connected data processors for use in multi-threaded context
+ Sensor: Periodically poll a given generator callback and pipe the retrieved value into a given `IDataProcessor`


Sample: Time the execution of arbitrary code

        DataCollector<Long> timings = new DataCollector("Execution times");
        ExecutionTimer timer = new ExecutionTimer(timings);
        for(int i = 0; i < 100 ; i ++){
            timer.begin();
            // run some code here
            Thread.sleep(1);
            timer.end();
        }
        // should contain 100 execution timings
        assertEquals(100, timings.size());
        for(DataPoint<Long> timing: timings.getDatapoints()){
            assertTrue(timing.getValue() >= 1);
        }



Sample: Generate random numbers which are emitted to a set of data processors with preceeding filters

        DataPointProducer testProducer = new DataPointProducer();
        ItemCounter counter = new ItemCounter();
        DataCollector<Long> all = new DataCollector<Long>("all data points");
        DataCollector<Long> everySecondDP = new DataCollector<Long>("every second data point");
        DataCollector<Long> everySecondMs = new DataCollector<Long>("every second millisecond");
        DataCollector<Long> every4thMs = new DataCollector<Long>("every 4th millisecond");
        DataCollector<Long> every4thDP = new DataCollector<Long>("every 4th data point");

        testProducer
                .add(counter.add(all))  // count received data points and pipe into all-collector
                .add(Sampler.<Long>timeBased(2).add(everySecondMs)) // filter based on data point time stamp
                .add(Sampler.<Long>timeBased(4).add(every4thMs))
                .add(Sampler.<Long>timeBased(2).add(everySecondMs))
                .add(Sampler.<Long>skip(3).add(every4thDP))  // filter based on item count
                .add(Sampler.<Long>skip(1).add(everySecondDP));

        testProducer.emitRandom(1, 100); // emit 100 random numbers using 1ms intervals

        assertEquals(counter.getItemCount(), testProducer.getEmitted().size()); // counter has seen all items
        assertEquals(all.size(), testProducer.getEmitted().size());  // all items are collected
        assertTrue(all.getDatapoints().containsAll(testProducer.getEmitted()));
        assertEquals(everySecondDP.getDatapoints(), everySecondMs.getDatapoints()); // filters have done their work
        assertTrue(everySecondDP.getDatapoints().containsAll(every4thDP.getDatapoints()));
        assertTrue(everySecondMs.getDatapoints().containsAll(every4thMs.getDatapoints()));








